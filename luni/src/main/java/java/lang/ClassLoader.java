/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.NoSuchElementException;

import org.apache.harmony.lang.ClassLoaderInfo;
import org.apache.harmony.lang.RuntimePermissionCollection;
import org.apache.harmony.misc.EmptyEnum;
import org.apache.harmony.vm.VMStack;

/**
 * Loads classes and resources from a repository. One or more class loaders are
 * installed at runtime. These are consulted whenever the runtime system needs a
 * specific class that is not yet available in-memory. Typically, class loaders
 * are grouped into a tree where child class loaders delegate all requests to
 * parent class loaders. Only if the parent class loader cannot satisfy the
 * request, the child class loader itself tries to handle it.
 * <p>
 * {@code ClassLoader} is an abstract class that implements the common
 * infrastructure required by all class loaders.
 * </p>
 *
 * @author Evgueni Brevnov
 * @see Class
 * @since 1.0
 */
public abstract class ClassLoader
{
	private static final class SystemClassLoader extends URLClassLoader
	{

		private boolean checkingPackageAccess = false;

		private SystemClassLoader(URL[] urls, ClassLoader parent)
		{
			super(urls, parent);
		}

		@Override
		protected java.security.PermissionCollection getPermissions(CodeSource codesource)
		{
			java.security.PermissionCollection pc = super.getPermissions(codesource);
			pc.add(org.apache.harmony.lang.RuntimePermissionCollection.EXIT_VM_PERMISSION);
			return pc;
		}

		@Override
		protected synchronized Class<?> loadClass(String className, boolean resolveClass) throws ClassNotFoundException
		{
			SecurityManager sm = System.getSecurityManager();
			if(sm != null && !checkingPackageAccess)
			{
				int index = className.lastIndexOf('.');
				if(index > 0)
				{ // skip if class is from a default package
					try
					{
						checkingPackageAccess = true;
						sm.checkPackageAccess(className.substring(0, index));
					}
					finally
					{
						checkingPackageAccess = false;
					}
				}
			}
			return super.loadClass(className, resolveClass);
		}

		private static URLClassLoader instance;

		static
		{
			ArrayList<URL> urlList = new ArrayList<URL>();
			// TODO avoid security checking?
			String extDirs = System.getProperty("java.ext.dirs", "");

			// -Djava.ext.dirs="" should be interpreted as nothing defined,
			// like we do below:
			String st[] = fracture(extDirs, File.pathSeparator);
			int l = st.length;
			for(int i = 0; i < l; i++)
			{
				try
				{
					File dir = new File(st[i]).getAbsoluteFile();
					File[] files = dir.listFiles();
					for(int j = 0; j < files.length; j++)
					{
						urlList.add(files[j].toURI().toURL());
					}
				}
				catch(Exception e)
				{
				}
			}
			// TODO avoid security checking?
			String classPath = System.getProperty("java.class.path", File.pathSeparator);
			st = fracture(classPath, File.pathSeparator);
			l = st.length;
			for(int i = 0; i < l; i++)
			{
				try
				{
					if(st[i].length() == 0)
					{
						st[i] = ".";
					}
					urlList.add(new File(st[i]).toURI().toURL());
				}
				catch(MalformedURLException e)
				{
					assert false : e.toString();
				}
			}
			instance = new SystemClassLoader(urlList.toArray(new URL[urlList.size()]), null);
		}

		public static ClassLoader getInstance()
		{
			return instance;
		}
	}

	static final class BootstrapLoader
	{

		// TODO avoid security checking
		private static final String bootstrapPath = System.getProperty("vm.boot.class.path", "");

		private static URLClassLoader resourceFinder = null;

		private static final HashMap<String, Package> systemPackages = new HashMap<String, Package>();

		/**
		 * This class contains static methods only. So it should not be
		 * instantiated.
		 */
		private BootstrapLoader()
		{
		}

		public static URL findResource(String name)
		{
			if(resourceFinder == null)
			{
				initResourceFinder();
			}
			return resourceFinder.findResource(name);
		}

		public static Enumeration<URL> findResources(String name) throws IOException
		{
			if(resourceFinder == null)
			{
				initResourceFinder();
			}
			return resourceFinder.findResources(name);
		}

		public static Package getPackage(String name)
		{
			synchronized(systemPackages)
			{
				updatePackages();
				return systemPackages.get(name.toString());
			}
		}

		public static Collection<Package> getPackages()
		{
			synchronized(systemPackages)
			{
				updatePackages();
				return systemPackages.values();
			}
		}

		private static void initResourceFinder()
		{
			synchronized(bootstrapPath)
			{
				if(resourceFinder != null)
				{
					return;
				}
				// -Xbootclasspath:"" should be interpreted as nothing defined,
				// like we do below:
				String st[] = fracture(bootstrapPath, File.pathSeparator);
				int l = st.length;
				ArrayList<URL> urlList = new ArrayList<URL>();
				for(int i = 0; i < l; i++)
				{
					try
					{
						urlList.add(new File(st[i]).toURI().toURL());
					}
					catch(MalformedURLException e)
					{
					}
				}
				URL[] urls = new URL[urlList.size()];
				resourceFinder = new URLClassLoader(urlList.toArray(urls), null);
			}
		}

		private static void updatePackages()
		{
			String[][] packages = VMClassRegistry.getSystemPackages(systemPackages.size());
			if(null == packages)
			{
				return;
			}
			for(int i = 0; i < packages.length; i++)
			{

				String name = packages[i][0];
				if(systemPackages.containsKey(name))
				{
					continue;
				}

				String jarURL = packages[i][1];
				systemPackages.put(name, new Package(null, name, jarURL));
			}
		}
	}

	/**
	 * default protection domain.
	 */
	private ProtectionDomain defaultDomain;

	/**
	 * system default class loader. It is initialized while
	 * getSystemClassLoader(..) method is executing.
	 */
	private static ClassLoader systemClassLoader = null;

	/**
	 * empty set of certificates
	 */
	private static final Certificate[] EMPTY_CERTIFICATES = new Certificate[0];

	/**
	 * this field has false as default value, it becomes true if system class
	 * loader is initialized.
	 */
	private static boolean initialized = false;

	/**
	 * package private to access from the java.lang.Class class. The following
	 * mapping is used <String name, Boolean flag>, where name - class name,
	 * flag - true if assertion is enabled, false if disabled.
	 */
	Hashtable<String, Boolean> classAssertionStatus;

	/**
	 * package private to access from the java.lang.Class class. The following
	 * mapping is used <String name, Object[] signers>, where name - class name,
	 * signers - array of signers.
	 */
	Hashtable<String, Object[]> classSigners;

	/**
	 * package private to access from the java.lang.Class class.
	 */
	int defaultAssertionStatus;
	boolean clearAssertionStatus;

	/**
	 * package private to access from the java.lang.Class class. The following
	 * mapping is used <String name, Boolean flag>, where name - package name,
	 * flag - true if assertion is enabled, false if disabled.
	 */
	Hashtable<String, Boolean> packageAssertionStatus;

	/**
	 * packages defined by this class loader are stored in this hash. The
	 * following mapping is used <String name, Package pkg>, where name -
	 * package name, pkg - corresponding package.
	 */
	private final HashMap<String, Package> definedPackages;

	/**
	 * The class registry, provides strong referencing between the classloader
	 * and it's defined classes. Intended for class unloading implementation.
	 *
	 * @see java.lang.Class#definingLoader
	 * @see #registerLoadedClass()
	 */
	private ArrayList<Class<?>> loadedClasses = new ArrayList<Class<?>>();

	/**
	 * package private to access from the java.lang.Class class. The following
	 * mapping is used <String name, Certificate[] certificates>, where name -
	 * the name of a package, certificates - array of certificates.
	 */
	private final Hashtable<String, Certificate[]> packageCertificates = new Hashtable<String, Certificate[]>();

	/**
	 * parent class loader
	 */
	private final ClassLoader parentClassLoader;


	/**
	 * Constructs a new instance of this class with the system class loader as
	 * its parent.
	 *
	 * @throws SecurityException if a security manager exists and it does not allow the
	 *                           creation of a new {@code ClassLoader}.
	 */
	protected ClassLoader()
	{
		this(getSystemClassLoader());
	}

	/**
	 * Constructs a new instance of this class with the specified class loader
	 * as its parent.
	 *
	 * @param parentLoader The {@code ClassLoader} to use as the new class loader's
	 *                     parent.
	 * @throws SecurityException if a security manager exists and it does not allow the
	 *                           creation of new a new {@code ClassLoader}.
	 */
	protected ClassLoader(ClassLoader parent)
	{
		SecurityManager sc = System.getSecurityManager();
		if(sc != null)
		{
			sc.checkCreateClassLoader();
		}
		parentClassLoader = parent;
		// this field is used to determine whether class loader was initialized
		// properly.
		definedPackages = new HashMap<String, Package>();
	}

	/**
	 * Returns the system class loader. This is the parent for new
	 * {@code ClassLoader} instances and is typically the class loader used to
	 * start the application. If a security manager is present and the caller's
	 * class loader is neither {@code null} nor the same as or an ancestor of
	 * the system class loader, then this method calls the security manager's
	 * checkPermission method with a RuntimePermission("getClassLoader")
	 * permission to ensure that it is ok to access the system class loader. If
	 * not, a {@code SecurityException} is thrown.
	 *
	 * @return the system class loader.
	 * @throws SecurityException if a security manager exists and it does not allow access to
	 *                           the system class loader.
	 */
	public static ClassLoader getSystemClassLoader()
	{
		if(!initialized)
		{
			// we assume only one thread will initialize system class loader. So
			// we don't synchronize initSystemClassLoader() method.
			initSystemClassLoader();
			// system class loader is initialized properly.
			initialized = true;
			// setContextClassLoader(...) method throws SecurityException if
			// current thread isn't allowed to set systemClassLoader as a
			// context class loader. Actually, it is abnormal situation if
			// thread can not change his own context class loader.
			// Thread.currentThread().setContextClassLoader(systemClassLoader);
		}
		//assert initialized;
		SecurityManager sc = System.getSecurityManager();
		if(sc != null)
		{
			// we use VMClassRegistry.getClassLoader(...) method instead of
			// Class.getClassLoader() due to avoid redundant security
			// checking
			ClassLoader callerLoader = VMClassRegistry.getClassLoader(VMStack.getCallerClass(0));
			if(callerLoader != null && callerLoader != systemClassLoader)
			{
				sc.checkPermission(RuntimePermissionCollection.GET_CLASS_LOADER_PERMISSION);
			}
		}
		return systemClassLoader;
	}

	/**
	 * Finds the URL of the resource with the specified name. The system class
	 * loader's resource lookup algorithm is used to find the resource.
	 *
	 * @param resName the name of the resource to find.
	 * @return the {@code URL} object for the requested resource or {@code null}
	 *         if the resource can not be found.
	 * @see Class#getResource
	 */
	public static URL getSystemResource(String name)
	{
		return getSystemClassLoader().getResource(name);
	}

	/**
	 * Returns an enumeration of URLs for the resource with the specified name.
	 * The system class loader's resource lookup algorithm is used to find the
	 * resource.
	 *
	 * @param resName the name of the resource to find.
	 * @return an enumeration of {@code URL} objects containing the requested
	 *         resources.
	 * @throws IOException if an I/O error occurs.
	 */
	public static Enumeration<URL> getSystemResources(String name) throws IOException
	{
		return getSystemClassLoader().getResources(name);
	}

	/**
	 * Returns a stream for the resource with the specified name. The system
	 * class loader's resource lookup algorithm is used to find the resource.
	 * Basically, the contents of the java.class.path are searched in order,
	 * looking for a path which matches the specified resource.
	 *
	 * @param resName the name of the resource to find.
	 * @return a stream for the resource or {@code null}.
	 * @see Class#getResourceAsStream
	 */
	public static InputStream getSystemResourceAsStream(String name)
	{
		return getSystemClassLoader().getResourceAsStream(name);
	}

	/**
	 * Constructs a new class from an array of bytes containing a class
	 * definition in class file format.
	 *
	 * @param classRep the memory image of a class file.
	 * @param offset   the offset into {@code classRep}.
	 * @param length   the length of the class file.
	 * @return the {@code Class} object created from the specified subset of
	 *         data in {@code classRep}.
	 * @throws ClassFormatError          if {@code classRep} does not contain a valid class.
	 * @throws IndexOutOfBoundsException if {@code offset < 0}, {@code length < 0} or if
	 *                                   {@code offset + length} is greater than the length of
	 *                                   {@code classRep}.
	 * @deprecated Use {@link #defineClass(String, byte[], int, int)}
	 */
	@Deprecated
	protected final Class<?> defineClass(byte[] classRep, int offset, int length) throws ClassFormatError
	{
		return null;
	}

	/**
	 * Constructs a new class from an array of bytes containing a class
	 * definition in class file format.
	 *
	 * @param className the expected name of the new class, may be {@code null} if not
	 *                  known.
	 * @param classRep  the memory image of a class file.
	 * @param offset    the offset into {@code classRep}.
	 * @param length    the length of the class file.
	 * @return the {@code Class} object created from the specified subset of
	 *         data in {@code classRep}.
	 * @throws ClassFormatError          if {@code classRep} does not contain a valid class.
	 * @throws IndexOutOfBoundsException if {@code offset < 0}, {@code length < 0} or if
	 *                                   {@code offset + length} is greater than the length of
	 *                                   {@code classRep}.
	 */
	protected final Class<?> defineClass(String className, byte[] classRep, int offset, int length) throws ClassFormatError
	{
		return null;
	}

	/**
	 * Constructs a new class from an array of bytes containing a class
	 * definition in class file format and assigns the specified protection
	 * domain to the new class. If the provided protection domain is
	 * {@code null} then a default protection domain is assigned to the class.
	 *
	 * @param className        the expected name of the new class, may be {@code null} if not
	 *                         known.
	 * @param classRep         the memory image of a class file.
	 * @param offset           the offset into {@code classRep}.
	 * @param length           the length of the class file.
	 * @param protectionDomain the protection domain to assign to the loaded class, may be
	 *                         {@code null}.
	 * @return the {@code Class} object created from the specified subset of
	 *         data in {@code classRep}.
	 * @throws ClassFormatError          if {@code classRep} does not contain a valid class.
	 * @throws IndexOutOfBoundsException if {@code offset < 0}, {@code length < 0} or if
	 *                                   {@code offset + length} is greater than the length of
	 *                                   {@code classRep}.
	 * @throws NoClassDefFoundError      if {@code className} is not equal to the name of the class
	 *                                   contained in {@code classRep}.
	 */
	protected final Class<?> defineClass(String className, byte[] classRep, int offset, int length, ProtectionDomain protectionDomain) throws java.lang.ClassFormatError
	{
		return null;
	}

	/**
	 * Defines a new class with the specified name, byte code from the byte
	 * buffer and the optional protection domain. If the provided protection
	 * domain is {@code null} then a default protection domain is assigned to
	 * the class.
	 *
	 * @param name             the expected name of the new class, may be {@code null} if not
	 *                         known.
	 * @param b                the byte buffer containing the byte code of the new class.
	 * @param protectionDomain the protection domain to assign to the loaded class, may be
	 *                         {@code null}.
	 * @return the {@code Class} object created from the data in {@code b}.
	 * @throws ClassFormatError     if {@code b} does not contain a valid class.
	 * @throws NoClassDefFoundError if {@code className} is not equal to the name of the class
	 *                              contained in {@code b}.
	 * @since 1.5
	 */
	protected final Class<?> defineClass(String name, ByteBuffer b, ProtectionDomain protectionDomain) throws ClassFormatError
	{
		byte[] temp = new byte[b.remaining()];
		b.get(temp);
		return defineClass(name, temp, 0, temp.length, protectionDomain);
	}

	/**
	 * Overridden by subclasses, throws a {@code ClassNotFoundException} by
	 * default. This method is called by {@code loadClass} after the parent
	 * {@code ClassLoader} has failed to find a loaded class of the same name.
	 *
	 * @param className the name of the class to look for.
	 * @return the {@code Class} object that is found.
	 * @throws ClassNotFoundException if the class cannot be found.
	 */
	protected Class<?> findClass(String className) throws ClassNotFoundException
	{
		return null;
	}

	/**
	 * Returns the class with the specified name if it has already been loaded
	 * by the virtual machine or {@code null} if it has not yet been loaded.
	 *
	 * @param className the name of the class to look for.
	 * @return the {@code Class} object or {@code null} if the requested class
	 *         has not been loaded.
	 */
	protected final Class<?> findLoadedClass(String className)
	{
		return null;
	}

	/**
	 * Finds the class with the specified name, loading it using the system
	 * class loader if necessary.
	 *
	 * @param className the name of the class to look for.
	 * @return the {@code Class} object with the requested {@code className}.
	 * @throws ClassNotFoundException if the class can not be found.
	 */
	protected final Class<?> findSystemClass(String className) throws ClassNotFoundException
	{
		return null;
	}

	/**
	 * Returns this class loader's parent.
	 *
	 * @return this class loader's parent or {@code null}.
	 * @throws SecurityException if a security manager exists and it does not allow to
	 *                           retrieve the parent class loader.
	 */
	public final ClassLoader getParent()
	{
		SecurityManager sc = System.getSecurityManager();
		if(sc != null)
		{
			ClassLoader callerLoader = VMClassRegistry.getClassLoader(VMStack.getCallerClass(0));
			if(callerLoader != null && !callerLoader.isSameOrAncestor(this))
			{
				sc.checkPermission(RuntimePermissionCollection.GET_CLASS_LOADER_PERMISSION);
			}
		}
		return parentClassLoader;
	}


	/**
	 * Returns an URL which can be used to access the resource described by
	 * resName, using the class loader's resource lookup algorithm. The default
	 * behavior is just to return null.
	 *
	 * @param resName the name of the resource to find.
	 * @return the {@code URL} object for the requested resource or {@code null}
	 *         if either the resource can not be found or a security manager
	 *         does not allow to access the resource.
	 * @see Class#getResource
	 */
	public URL getResource(String name)
	{
		String nm = name.toString();
		checkInitialized();
		URL foundResource = (parentClassLoader == null) ? BootstrapLoader.findResource(nm) : parentClassLoader.getResource(nm);
		return foundResource == null ? findResource(nm) : foundResource;
	}

	/**
	 * Returns an Enumeration of URL which can be used to access the resources
	 * described by resName, using the class loader's resource lookup algorithm.
	 * The default behavior is just to return an empty Enumeration.
	 *
	 * @param resName the name of the resource to find.
	 * @return an enumeration of {@code URL} objects for the requested resource.
	 * @throws IOException if an I/O error occurs.
	 */
	public Enumeration<URL> getResources(String name) throws IOException
	{
		checkInitialized();
		ClassLoader cl = this;
		final ArrayList<Enumeration<URL>> foundResources = new ArrayList<Enumeration<URL>>();
		Enumeration<URL> resourcesEnum;
		do
		{
			resourcesEnum = cl.findResources(name);
			if(resourcesEnum != null && resourcesEnum.hasMoreElements())
			{
				foundResources.add(resourcesEnum);
			}
		}
		while((cl = cl.parentClassLoader) != null);
		resourcesEnum = BootstrapLoader.findResources(name);
		if(resourcesEnum != null && resourcesEnum.hasMoreElements())
		{
			foundResources.add(resourcesEnum);
		}
		return new Enumeration<URL>()
		{

			private int position = foundResources.size() - 1;

			public boolean hasMoreElements()
			{
				while(position >= 0)
				{
					if(foundResources.get(position).hasMoreElements())
					{
						return true;
					}
					position--;
				}
				return false;
			}

			public URL nextElement()
			{
				while(position >= 0)
				{
					try
					{
						return (foundResources.get(position)).nextElement();
					}
					catch(NoSuchElementException e)
					{
					}
					position--;
				}
				throw new NoSuchElementException();
			}
		};
	}

	/**
	 * Returns a stream on a resource found by looking up resName using the
	 * class loader's resource lookup algorithm. The default behavior is just to
	 * return null.
	 *
	 * @param resName the name of the resource to find.
	 * @return a stream for the resource or {@code null} if either the resource
	 *         can not be found or a security manager does not allow to access
	 *         the resource.
	 * @see Class#getResourceAsStream
	 */
	public InputStream getResourceAsStream(String name)
	{
		URL foundResource = getResource(name);
		if(foundResource != null)
		{
			try
			{
				return foundResource.openStream();
			}
			catch(IOException e)
			{
			}
		}
		return null;
	}

	/**
	 * Loads the class with the specified name. Invoking this method is
	 * equivalent to calling {@code loadClass(className, false)}.
	 *
	 * @param className the name of the class to look for.
	 * @return the {@code Class} object.
	 * @throws ClassNotFoundException if the class can not be found.
	 */
	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		return loadClass(name, false);
	}

	/**
	 * Loads the class with the specified name, optionally linking it after
	 * loading. The following steps are performed:
	 * <ol>
	 * <li> Call {@link #findLoadedClass(String)} to determine if the requested
	 * class has already been loaded.</li>
	 * <li>If the class has not yet been loaded: Invoke this method on the
	 * parent class loader.</li>
	 * <li>If the class has still not been loaded: Call
	 * {@link #findClass(String)} to find the class.</li>
	 * </ol>
	 *
	 * @param className the name of the class to look for.
	 * @param resolve   Indicates if the class should be resolved after loading. This
	 *                  parameter is ignored on the Android reference implementation;
	 *                  classes are not resolved.
	 * @return the {@code Class} object.
	 * @throws ClassNotFoundException if the class can not be found.
	 */
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
	{
		checkInitialized();
		if(name == null)
		{
			throw new NullPointerException();
		}
		if(name.indexOf("/") != -1)
		{
			throw new ClassNotFoundException(name);
		}

		Class<?> clazz = findLoadedClass(name);
		if(clazz == null)
		{
			if(parentClassLoader == null)
			{
				clazz = VMClassRegistry.loadBootstrapClass(name);
			}
			else
			{
				try
				{
					clazz = parentClassLoader.loadClass(name);
					if(clazz != null)
					{
						try
						{
							VMStack.getCallerClass(0).asSubclass(ClassLoader.class);
						}
						catch(ClassCastException ex)
						{
							// caller class is not a subclass of
							// java/lang/ClassLoader so register as
							// initiating loader as we are called from
							// outside of ClassLoader delegation chain
							registerInitiatedClass(clazz);
						}
					}
				}
				catch(ClassNotFoundException e)
				{
				}
			}
			if(clazz == null)
			{
				clazz = findClass(name);
				if(clazz == null)
				{
					throw new ClassNotFoundException(name);
				}
			}
		}
		if(resolve)
		{
			resolveClass(clazz);
		}
		return clazz;
	}

	/**
	 * Forces a class to be linked (initialized). If the class has already been
	 * linked this operation has no effect.
	 *
	 * @param clazz the class to link.
	 */
	protected final void resolveClass(Class<?> clazz)
	{
		if(clazz == null)
		{
			throw new NullPointerException();
		}
		VMClassRegistry.linkClass(clazz);
	}

	/**
	 * Indicates whether this class loader is the system class loader. This
	 * method must be provided by the virtual machine vendor, as it is used by
	 * other provided class implementations in this package. A sample
	 * implementation of this method is provided by the reference
	 * implementation. This method is used by
	 * SecurityManager.classLoaderDepth(), currentClassLoader() and
	 * currentLoadedClass(). Returns true if the receiver is a system class
	 * loader.
	 * <p/>
	 * Note that this method has package visibility only. It is defined here to
	 * avoid the security manager check in getSystemClassLoader, which would be
	 * required to implement this method anywhere else.
	 *
	 * @return {@code true} if the receiver is a system class loader
	 * @see Class#getClassLoaderImpl()
	 */
	final boolean isSystemClassLoader()
	{
		return ClassLoaderInfo.isSystemClassLoader(this);
	}

	/**
	 * Finds the URL of the resource with the specified name. This
	 * implementation just returns {@code null}; it should be overridden in
	 * subclasses.
	 *
	 * @param resName the name of the resource to find.
	 * @return the {@code URL} object for the requested resource.
	 */
	protected URL findResource(String resName)
	{
		return null;
	}

	/**
	 * Finds an enumeration of URLs for the resource with the specified name.
	 * This implementation just returns an empty {@code Enumeration}; it should
	 * be overridden in subclasses.
	 *
	 * @param resName the name of the resource to find.
	 * @return an enumeration of {@code URL} objects for the requested resource.
	 * @throws IOException if an I/O error occurs.
	 */
	protected Enumeration<URL> findResources(String name) throws IOException
	{
		return EmptyEnum.getInstance();
	}

	/**
	 * Returns the absolute path of the file containing the library with the
	 * specified name, or {@code null}. If this method returns {@code null} then
	 * the virtual machine searches the directories specified by the system
	 * property "java.library.path".
	 *
	 * @param libName the name of the library to find.
	 * @return the absolute path of the library.
	 */
	protected String findLibrary(String libName)
	{
		return null;
	}

	/**
	 * Returns the package with the specified name. Package information is
	 * searched in this class loader.
	 *
	 * @param name the name of the package to find.
	 * @return the package with the requested name; {@code null} if the package
	 *         can not be found.
	 */
	protected Package getPackage(String name)
	{
		checkInitialized();
		Package pkg = null;
		if(name == null)
		{
			throw new NullPointerException();
		}
		synchronized(definedPackages)
		{
			pkg = definedPackages.get(name);
		}
		if(pkg == null)
		{
			if(parentClassLoader == null)
			{
				pkg = BootstrapLoader.getPackage(name);
			}
			else
			{
				pkg = parentClassLoader.getPackage(name);
			}
		}
		return pkg;
	}

	/**
	 * Returns all the packages known to this class loader.
	 *
	 * @return an array with all packages known to this class loader.
	 */
	protected Package[] getPackages()
	{
		checkInitialized();
		ArrayList<Package> packages = new ArrayList<Package>();
		fillPackages(packages);
		return packages.toArray(new Package[packages.size()]);
	}

	/**
	 * Defines and returns a new {@code Package} using the specified
	 * information. If {@code sealBase} is {@code null}, the package is left
	 * unsealed. Otherwise, the package is sealed using this URL.
	 *
	 * @param name        the name of the package.
	 * @param specTitle   the title of the specification.
	 * @param specVersion the version of the specification.
	 * @param specVendor  the vendor of the specification.
	 * @param implTitle   the implementation title.
	 * @param implVersion the implementation version.
	 * @param implVendor  the specification vendor.
	 * @param sealBase    the URL used to seal this package or {@code null} to leave the
	 *                    package unsealed.
	 * @return the {@code Package} object that has been created.
	 * @throws IllegalArgumentException if a package with the specified name already exists.
	 */
	protected Package definePackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException
	{
		synchronized(definedPackages)
		{
			if(getPackage(name) != null)
			{
				throw new IllegalArgumentException("Package " + name + "has been already defined.");
			}
			Package pkg = new Package(this, name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
			definedPackages.put(name, pkg);
			return pkg;
		}
	}

	/**
	 * Sets the signers of the specified class.
	 *
	 * @param c       the {@code Class} object for which to set the signers.
	 * @param signers the signers for {@code c}.
	 */
	protected final void setSigners(Class<?> clazz, Object[] signers)
	{
		checkInitialized();
		String name = clazz.getName();
		ClassLoader classLoader = clazz.getClassLoaderImpl();
		if(classLoader != null)
		{
			if(classLoader.classSigners == null)
			{
				classLoader.classSigners = new Hashtable<String, Object[]>();
			}
			classLoader.classSigners.put(name, signers);
		}
	}

	/**
	 * <p>
	 * This must be provided by the VM vendor. It is used by
	 * SecurityManager.checkMemberAccess() with depth = 3. Note that
	 * checkMemberAccess() assumes the following stack when called:<br>
	 * </p>
	 * <p/>
	 * <pre>
	 *          &lt; user code &amp;gt; &lt;- want this class
	 *          Class.getDeclared*();
	 *          Class.checkMemberAccess();
	 *          SecurityManager.checkMemberAccess(); &lt;- current frame
	 * </pre>
	 * <p/>
	 * <p>
	 * Returns the ClassLoader of the method (including natives) at the
	 * specified depth on the stack of the calling thread. Frames representing
	 * the VM implementation of java.lang.reflect are not included in the list.
	 * </p>
	 * Notes:
	 * <ul>
	 * <li>This method operates on the defining classes of methods on stack.
	 * NOT the classes of receivers.</li>
	 * <li>The item at depth zero is the caller of this method</li>
	 * </ul>
	 *
	 * @param depth the stack depth of the requested ClassLoader
	 * @return the ClassLoader at the specified depth
	 */
	static final ClassLoader getStackClassLoader(int depth)
	{
		Class<?> clazz = VMStack.getCallerClass(depth);
		return clazz != null ? clazz.getClassLoaderImpl() : null;
	}

	/**
	 * This method must be provided by the VM vendor, as it is called by
	 * java.lang.System.loadLibrary(). System.loadLibrary() cannot call
	 * Runtime.loadLibrary() because this method loads the library using the
	 * ClassLoader of the calling method. Loads and links the library specified
	 * by the argument.
	 *
	 * @param libName the name of the library to load
	 * @param loader  the classloader in which to load the library
	 * @throws UnsatisfiedLinkError if the library could not be loaded
	 * @throws SecurityException    if the library was not allowed to be loaded
	 */
	static final void loadLibraryWithClassLoader(String libName, ClassLoader loader)
	{
		SecurityManager sc = System.getSecurityManager();
		if(sc != null)
		{
			sc.checkLink(libName);
		}
		if(loader != null)
		{
			String fullLibName = loader.findLibrary(libName);
			if(fullLibName != null)
			{
				loadLibrary(fullLibName, loader, null);
				return;
			}
		}
		String path = System.getProperty("java.library.path", "");
		path += System.getProperty("vm.boot.library.path", "");
		loadLibrary(libName, loader, path);
	}

	/**
	 * Sets the assertion status of the class with the specified name.
	 *
	 * @param cname  the name of the class for which to set the assertion status.
	 * @param enable the new assertion status.
	 */
	public void setClassAssertionStatus(String name, boolean flag)
	{
		if(name != null)
		{
			Class.disableAssertions = false;
			synchronized(definedPackages)
			{
				if(classAssertionStatus == null)
				{
					classAssertionStatus = new Hashtable<String, Boolean>();
				}
			}
			classAssertionStatus.put(name, Boolean.valueOf(flag));
		}
	}

	/**
	 * Sets the assertion status of the package with the specified name.
	 *
	 * @param pname  the name of the package for which to set the assertion status.
	 * @param enable the new assertion status.
	 */
	public void setPackageAssertionStatus(String name, boolean flag)
	{
		if(name == null)
		{
			name = "";
		}
		Class.disableAssertions = false;
		synchronized(definedPackages)
		{
			if(packageAssertionStatus == null)
			{
				packageAssertionStatus = new Hashtable<String, Boolean>();
			}
		}
		packageAssertionStatus.put(name, Boolean.valueOf(flag));
	}

	/**
	 * Sets the default assertion status for this class loader.
	 *
	 * @param enable the new assertion status.
	 */
	public void setDefaultAssertionStatus(boolean flag)
	{
		if(flag)
		{
			Class.disableAssertions = false;
		}
		defaultAssertionStatus = flag ? 1 : -1;
	}

	/**
	 * Sets the default assertion status for this class loader to {@code false}
	 * and removes any package default and class assertion status settings.
	 */
	public void clearAssertionStatus()
	{
		clearAssertionStatus = true;
		defaultAssertionStatus = -1;
		packageAssertionStatus = null;
		classAssertionStatus = null;
	}

	static final void loadLibrary(String libName, ClassLoader loader, String libraryPath)
	{
		SecurityManager sc = System.getSecurityManager();
		if(sc != null)
		{
			sc.checkLink(libName);
		}
		String pathSeparator = System.getProperty("path.separator");
		String fileSeparator = System.getProperty("file.separator");
		String st[] = fracture(libraryPath, pathSeparator);
		int l = st.length;
		for(int i = 0; i < l; i++)
		{
			try
			{
				VMClassRegistry.loadLibrary(st[i] + fileSeparator + libName, loader);
				return;
			}
			catch(UnsatisfiedLinkError e)
			{
			}
		}
		throw new UnsatisfiedLinkError(libName);
	}

	/**
	 * Registers this class loader as initiating for a class
	 * Declared as package private to use it from java.lang.Class.forName
	 */
	native void registerInitiatedClass(Class<?> clazz);

	// not public api

	/**
	 * This method should be called from each method that performs unsafe
	 * actions.
	 */
	private void checkInitialized()
	{
		if(definedPackages == null)
			throw new SecurityException("Class loader was not initialized properly.");
	}

	/**
	 * Helper method for the getPackages() method.
	 */
	private void fillPackages(ArrayList<Package> packages)
	{
		if(parentClassLoader == null)
			packages.addAll(BootstrapLoader.getPackages());
		else
			parentClassLoader.fillPackages(packages);
		synchronized(definedPackages)
		{
			packages.addAll(definedPackages.values());
		}
	}

	final boolean isSameOrAncestor(ClassLoader loader)
	{
		while(loader != null)
		{
			if(this == loader)
			{
				return true;
			}
			loader = loader.parentClassLoader;
		}
		return false;
	}

	/**
	 * Initializes the system class loader.
	 */
	@SuppressWarnings("unchecked")
	private static void initSystemClassLoader()
	{
		if(systemClassLoader != null)
		{
			throw new IllegalStateException("Recursive invocation while initializing system class loader");
		}
		systemClassLoader = SystemClassLoader.getInstance();

		String smName = System.getPropertyUnsecure("java.security.manager");
		if(smName != null)
		{
			try
			{
				final Class<SecurityManager> smClass;
				if("".equals(smName) || "default".equalsIgnoreCase(smName))
				{
					smClass = java.lang.SecurityManager.class;
				}
				else
				{
					smClass = (Class<SecurityManager>) systemClassLoader.loadClass(smName);
					if(!SecurityManager.class.isAssignableFrom(smClass))
					{
						throw new Error(smClass + " must inherit java.lang.SecurityManager");
					}
				}
				AccessController.doPrivileged(new PrivilegedExceptionAction<Object>()
				{
					public Object run() throws Exception
					{
						System.setSecurityManager(smClass.newInstance());
						return null;
					}
				});
			}
			catch(Exception e)
			{
				throw (Error) new InternalError().initCause(e);
			}
		}

		String className = System.getPropertyUnsecure("java.system.class.loader");
		if(className != null)
		{
			try
			{
				final Class<?> userClassLoader = systemClassLoader.loadClass(className);
				if(!ClassLoader.class.isAssignableFrom(userClassLoader))
				{
					throw new Error(userClassLoader.toString() + " must inherit java.lang.ClassLoader");
				}
				systemClassLoader = AccessController.doPrivileged(new PrivilegedExceptionAction<ClassLoader>()
				{
					public ClassLoader run() throws Exception
					{
						Constructor c = userClassLoader.getConstructor(ClassLoader.class);
						return (ClassLoader) c.newInstance(systemClassLoader);
					}
				});
			}
			catch(ClassNotFoundException e)
			{
				throw new Error(e);
			}
			catch(PrivilegedActionException e)
			{
				throw new Error(e.getCause());
			}
		}
	}

	/**
	 * Helper method to avoid StringTokenizer using.
	 */
	private static String[] fracture(String str, String sep)
	{
		if(str.length() == 0)
		{
			return new String[0];
		}
		ArrayList<String> res = new ArrayList<String>();
		int in = 0;
		int curPos = 0;
		int i = str.indexOf(sep);
		int len = sep.length();
		while(i != -1)
		{
			String s = str.substring(curPos, i);
			res.add(s);
			in++;
			curPos = i + len;
			i = str.indexOf(sep, curPos);
		}

		len = str.length();
		if(curPos <= len)
		{
			String s = str.substring(curPos, len);
			in++;
			res.add(s);
		}

		return res.toArray(new String[in]);
	}
}
