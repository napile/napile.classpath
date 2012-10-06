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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.channels.Channel;
import java.nio.channels.spi.SelectorProvider;
import java.security.SecurityPermission;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyPermission;

import org.apache.harmony.lang.RuntimePermissionCollection;
import org.apache.harmony.luni.platform.Environment;
import org.apache.harmony.vm.VMStack;

/**
 * Provides access to system-related information and resources including
 * standard input and output. Enables clients to dynamically load native
 * libraries. All methods of this class are accessed in a static way and the
 * class itself can not be instantiated.
 *
 * @see Runtime
 * @author Roman S. Bushmanov
 */
public final class System
{
	static String getPropertyUnsecure(String key)
	{
		return getPropertiesUnsecure().getProperty(key);
	}

	private System()
	{
	}

	// The standard input, output, and error streams.
	// Typically, these are connected to the shell which
	// ran the Java program.
	/**
	 * Default input stream.
	 */
	public static final InputStream in = createIn();

	/**
	 * Default output stream.
	 */
	public static final PrintStream out = createOut();

	/**
	 * Default error output stream.
	 */
	public static final PrintStream err = createErr();

	/**
	 * The System Properties table.
	 */
	private static Properties systemProperties = null;

	// The System default SecurityManager
	private static SecurityManager securityManager = null;

	/**
	 * Sets the standard input stream to the given user defined input stream.
	 *
	 * @param newIn the user defined input stream to set as the standard input
	 *              stream.
	 * @throws SecurityException if a {@link SecurityManager} is installed and its {@code
	 *                           checkPermission()} method does not allow the change of the
	 *                           stream.
	 */
	@SuppressWarnings("unused")
	public static void setIn(InputStream in)
	{
		SecurityManager sm = securityManager;
		if(sm != null)
		{
			sm.checkPermission(RuntimePermissionCollection.SET_IO_PERMISSION);
		}
		setInUnsecure(in);
	}

	/**
	 * Sets the standard output stream to the given user defined output stream.
	 *
	 * @param newOut the user defined output stream to set as the standard output
	 *               stream.
	 * @throws SecurityException if a {@link SecurityManager} is installed and its {@code
	 *                           checkPermission()} method does not allow the change of the
	 *                           stream.
	 */
	@SuppressWarnings("unused")
	public static void setOut(PrintStream out)
	{
		SecurityManager sm = securityManager;
		if(sm != null)
		{
			sm.checkPermission(RuntimePermissionCollection.SET_IO_PERMISSION);
		}
		setOutUnsecure(out);
	}

	/**
	 * Sets the standard error output stream to the given user defined output
	 * stream.
	 *
	 * @param newErr the user defined output stream to set as the standard error
	 *               output stream.
	 * @throws SecurityException if a {@link SecurityManager} is installed and its {@code
	 *                           checkPermission()} method does not allow the change of the
	 *                           stream.
	 */
	@SuppressWarnings("unused")
	public static void setErr(PrintStream err)
	{
		SecurityManager sm = securityManager;
		if(sm != null)
		{
			sm.checkPermission(RuntimePermissionCollection.SET_IO_PERMISSION);
		}
		setErrUnsecure(err);
	}


	/**
	 * Copies the number of {@code length} elements of the Array {@code src}
	 * starting at the offset {@code srcPos} into the Array {@code dest} at
	 * the position {@code destPos}.
	 *
	 * @param src     the source array to copy the content.
	 * @param srcPos  the starting index of the content in {@code src}.
	 * @param dest    the destination array to copy the data into.
	 * @param destPos the starting index for the copied content in {@code dest}.
	 * @param length  the number of elements of the {@code array1} content they have
	 *                to be copied.
	 */
	public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
	{
		VMMemoryManager.arrayCopy(src, srcPos, dest, destPos, length);
	}

	/**
	 * Returns the current system time in milliseconds since January 1, 1970
	 * 00:00:00 UTC. This method shouldn't be used for measuring timeouts or
	 * other elapsed time measurements, as changing the system time can affect
	 * the results.
	 *
	 * @return the local system time in milliseconds.
	 */
	public static long currentTimeMillis()
	{
		return VMExecutionEngine.currentTimeMillis();
	}

	/**
	 * Returns the current timestamp of the most precise timer available on the
	 * local system. This timestamp can only be used to measure an elapsed
	 * period by comparing it against another timestamp. It cannot be used as a
	 * very exact system time expression.
	 *
	 * @return the current timestamp in nanoseconds.
	 */
	public static long nanoTime()
	{
		return VMExecutionEngine.nanoTime();
	}


	/**
	 * Causes the virtual machine to stop running and the program to exit. If
	 * {@link #runFinalizersOnExit(boolean)} has been previously invoked with a
	 * {@code true} argument, then all objects will be properly
	 * garbage-collected and finalized first.
	 *
	 * @param code the return code.
	 * @throws SecurityException if the running thread has not enough permission to exit the
	 *                           virtual machine.
	 * @see SecurityManager#checkExit
	 */
	public static void exit(int status)
	{
		Runtime.getRuntime().exit(status);
	}

	/**
	 * Indicates to the virtual machine that it would be a good time to run the
	 * garbage collector. Note that this is a hint only. There is no guarantee
	 * that the garbage collector will actually be run.
	 */
	public static void gc()
	{
		Runtime.getRuntime().gc();
	}

	/**
	 * Returns the value of the environment variable with the given name {@code
	 * var}.
	 *
	 * @param name the name of the environment variable.
	 * @return the value of the specified environment variable or {@code null}
	 *         if no variable exists with the given name.
	 * @throws SecurityException if a {@link SecurityManager} is installed and its {@code
	 *                           checkPermission()} method does not allow the querying of
	 *                           single environment variables.
	 */
	public static String getenv(String name)
	{
		if(name == null)
		{
			throw new NullPointerException("name should not be null");
		}
		SecurityManager sm = securityManager;
		if(sm != null)
		{
			sm.checkPermission(new RuntimePermission("getenv." + name));
		}
		return Environment.getenv(name);
	}

	/**
	 * Returns an unmodifiable map of all available environment variables.
	 *
	 * @return the map representing all environment variables.
	 * @throws SecurityException if a {@link SecurityManager} is installed and its {@code
	 *                           checkPermission()} method does not allow the querying of
	 *                           all environment variables.
	 */
	public static Map<String, String> getenv()
	{
		SecurityManager sm = securityManager;
		if(sm != null)
		{
			sm.checkPermission(RuntimePermissionCollection.GETENV_PERMISSION);
		}
		return Environment.getenv();
	}

	/**
	 * Returns the inherited channel from the creator of the current virtual
	 * machine.
	 *
	 * @return the inherited {@link Channel} or {@code null} if none exists.
	 * @throws IOException if an I/O error occurred.
	 * @see SelectorProvider
	 * @see SelectorProvider#inheritedChannel()
	 */
	public static Channel inheritedChannel() throws IOException
	{
		//XXX:does it mean the permission of the "access to the channel"?
		//If YES then this checkPermission must be removed because it should be presented into java.nio.channels.spi.SelectorProvider.inheritedChannel()
		//If NO  then some other permission name (which one?) should be used here
		//and the corresponding constant should be placed within org.apache.harmony.lang.RuntimePermission class:
		if(securityManager != null)
		{
			securityManager.checkPermission(new RuntimePermission("inheritedChannel")); //see java.nio.channels.spi.SelectorProvider.inheritedChannel() spec
		}

		return SelectorProvider.provider().inheritedChannel();
	}

	/**
	 * Returns the system properties. Note that this is not a copy, so that
	 * changes made to the returned Properties object will be reflected in
	 * subsequent calls to getProperty and getProperties.
	 *
	 * @return the system properties.
	 * @throws SecurityException if a {@link SecurityManager} is installed and its {@code
	 *                           checkPropertiesAccess()} method does not allow the operation.
	 */
	public static Properties getProperties()
	{
		SecurityManager secMgr = System.getSecurityManager();
		if(secMgr != null)
		{
			secMgr.checkPropertiesAccess();
		}
		return systemProperties;
	}

	/**
	 * Returns the value of a particular system property or {@code null} if no
	 * such property exists.
	 * <p/>
	 * The properties currently provided by the virtual machine are:
	 * <p/>
	 * <pre>
	 *        java.vendor.url
	 *        java.class.path
	 *        user.home
	 *        java.class.version
	 *        os.version
	 *        java.vendor
	 *        user.dir
	 *        user.timezone
	 *        path.separator
	 *        os.name
	 *        os.arch
	 *        line.separator
	 *        file.separator
	 *        user.name
	 *        java.version
	 *        java.home
	 * </pre>
	 *
	 * @param prop the name of the system property to look up.
	 * @return the value of the specified system property or {@code null} if the
	 *         property doesn't exist.
	 * @throws SecurityException if a {@link SecurityManager} is installed and its {@code
	 *                           checkPropertyAccess()} method does not allow the operation.
	 */
	public static String getProperty(String prop)
	{
		return getProperty(prop, null);
	}

	/**
	 * Returns the value of a particular system property. The {@code
	 * defaultValue} will be returned if no such property has been found.
	 *
	 * @param prop         the name of the system property to look up.
	 * @param defaultValue the return value if the system property with the given name
	 *                     does not exist.
	 * @return the value of the specified system property or the {@code
	 *         defaultValue} if the property does not exist.
	 * @throws SecurityException if a {@link SecurityManager} is installed and its {@code
	 *                           checkPropertyAccess()} method does not allow the operation.
	 */
	public static String getProperty(String prop, String defaultValue)
	{
		if(prop.length() == 0)
		{
			throw new IllegalArgumentException();
		}
		SecurityManager secMgr = System.getSecurityManager();
		if(secMgr != null)
		{
			secMgr.checkPropertyAccess(prop);
		}
		return systemProperties.getProperty(prop, defaultValue);
	}

	/**
	 * Sets the value of a particular system property.
	 *
	 * @param prop  the name of the system property to be changed.
	 * @param value the value to associate with the given property {@code prop}.
	 * @return the old value of the property or {@code null} if the property
	 *         didn't exist.
	 * @throws SecurityException if a security manager exists and write access to the
	 *                           specified property is not allowed.
	 */
	public static String setProperty(String key, String value)
	{
		if(key.length() == 0)
		{
			throw new IllegalArgumentException("key is empty");
		}
		SecurityManager sm = securityManager;
		if(sm != null)
		{
			sm.checkPermission(new PropertyPermission(key, "write"));
		}
		Properties props = getPropertiesUnsecure();
		return (String) props.setProperty(key, value);
	}

	/**
	 * Removes a specific system property.
	 *
	 * @param key the name of the system property to be removed.
	 * @return the property value or {@code null} if the property didn't exist.
	 * @throws NullPointerException     if the argument {@code key} is {@code null}.
	 * @throws IllegalArgumentException if the argument {@code key} is empty.
	 * @throws SecurityException        if a security manager exists and write access to the
	 *                                  specified property is not allowed.
	 * @since 1.5
	 */
	public static String clearProperty(String key)
	{
		if(key == null)
		{
			throw new NullPointerException();
		}
		if(key.length() == 0)
		{
			throw new IllegalArgumentException();
		}

		SecurityManager secMgr = System.getSecurityManager();
		if(secMgr != null)
		{
			secMgr.checkPermission(new PropertyPermission(key, "write"));
		}
		return (String) systemProperties.remove(key);
	}

	/**
	 * Returns the active security manager.
	 *
	 * @return the system security manager object.
	 */
	public static SecurityManager getSecurityManager() {
		return securityManager;
	}

	/**
	 * Returns an integer hash code for the parameter. The hash code returned is
	 * the same one that would be returned by the method {@code
	 * java.lang.Object.hashCode()}, whether or not the object's class has
	 * overridden hashCode(). The hash code for {@code null} is {@code 0}.
	 *
	 * @param anObject the object to calculate the hash code.
	 * @return the hash code for the given object.
	 * @see java.lang.Object#hashCode
	 */
	public static int identityHashCode(Object object)
	{
		return VMMemoryManager.getIdentityHashCode(object);
	}

	/**
	 * Loads the specified file as a dynamic library.
	 *
	 * @param pathName the path of the file to be loaded.
	 * @throws SecurityException if the library was not allowed to be loaded.
	 */
	public static void load(String filename)
	{
		Runtime.getRuntime().load0(filename, VMClassRegistry.getClassLoader(VMStack.getCallerClass(0)), true);
	}

	/**
	 * Loads and links the shared library with the given name {@code libName}.
	 * The file will be searched in the default directory for shared libraries
	 * of the local system.
	 *
	 * @param libName the name of the library to load.
	 * @throws UnsatisfiedLinkError if the library could not be loaded.
	 * @throws SecurityException    if the library was not allowed to be loaded.
	 */
	public static void loadLibrary(String libName)
	{
		Runtime.getRuntime().loadLibrary0(libName, VMClassRegistry.getClassLoader(VMStack.getCallerClass(0)), true);
	}

	/**
	 * Provides a hint to the virtual machine that it would be useful to attempt
	 * to perform any outstanding object finalizations.
	 */
	public static void runFinalization()
	{
		Runtime.getRuntime().runFinalization();
	}

	/**
	 * Ensures that, when the virtual machine is about to exit, all objects are
	 * finalized. Note that all finalization which occurs when the system is
	 * exiting is performed after all running threads have been terminated.
	 *
	 * @param flag the flag determines if finalization on exit is enabled.
	 * @deprecated this method is unsafe.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	public static void runFinalizersOnExit(boolean flag)
	{
		Runtime.runFinalizersOnExit(flag);
	}

	/**
	 * Sets all system properties. Note that the object which is passed in
	 * not copied, so that subsequent changes made to the object will be
	 * reflected in calls to getProperty and getProperties.
	 *
	 * @param p the new system property.
	 * @throws SecurityException if a {@link SecurityManager} is installed and its {@code
	 *                           checkPropertiesAccess()} method does not allow the operation.
	 */
	public static void setProperties(Properties props)
	{
		SecurityManager sm = securityManager;
		if(sm != null)
		{
			sm.checkPropertiesAccess();
		}
		systemProperties = props;
	}

	/**
	 * Sets the active security manager. Note that once the security manager has
	 * been set, it can not be changed. Attempts to do that will cause a
	 * security exception.
	 *
	 * @param sm the new security manager.
	 * @throws SecurityException if the security manager has already been set and if its
	 *                           checkPermission method does not allow to redefine the
	 *                           security manager.
	 */
	public static synchronized void setSecurityManager(SecurityManager sm)
	{
		if(securityManager != null)
		{
			securityManager.checkPermission(RuntimePermissionCollection.SET_SECURITY_MANAGER_PERMISSION);
		}

		if(sm != null)
		{
			// before the new manager assumed office, make a pass through
			// the common operations and let it load needed classes (if any),
			// to avoid infinite recursion later on
			try
			{
				sm.checkPermission(new SecurityPermission("getProperty.package.access"));
			}
			catch(Exception ignore)
			{
			}
			try
			{
				sm.checkPackageAccess("java.lang");
			}
			catch(Exception ignore)
			{
			}
		}

		securityManager = sm;
	}


	/**
	 * Returns the platform specific file name format for the shared library
	 * named by the argument.
	 *
	 * @param userLibName the name of the library to look up.
	 * @return the platform specific filename for the library
	 */
	public static String mapLibraryName(String libname)
	{
		if(libname == null)
			throw new NullPointerException("libname should not be empty");
		return VMExecutionEngine.mapLibraryName(libname);
	}

	/**
	 * Constructs a system <code>err</code> stream. This method is used only
	 * for initialization of <code>err</code> field
	 */
	private static PrintStream createErr()
	{
		return new PrintStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.err)), true);
	}

	/**
	 * Constructs a system <code>in</code> stream. This method is used only
	 * for initialization of <code>in</code> field
	 */
	private static InputStream createIn()
	{
		return new BufferedInputStream(new FileInputStream(FileDescriptor.in));
	}

	/**
	 * Constructs a system <code>out</code> stream. This method is used only
	 * for initialization of <code>out</code> field
	 */
	private static PrintStream createOut()
	{
		return new PrintStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.out)), true);
	}

	/**
	 * Returns system properties without security checks. Initializes the system
	 * properties if it isn't done yet.
	 */
	private static Properties getPropertiesUnsecure()
	{
		Properties sp = systemProperties;
		if(sp == null)
		{
			systemProperties = sp = VMExecutionEngine.getProperties();
		}
		return sp;
	}

	/**
	 * Initiaies the VM shutdown sequence.
	 */
	static void execShutdownSequence()
	{
		Runtime.getRuntime().execShutdownSequence();
	}

	/**
	 * Sets the value of <code>err</code> field without any security checks
	 */
	private static native void setErrUnsecure(PrintStream err);

	/**
	 * Sets the value of <code>in</code> field without any security checks
	 */
	private static native void setInUnsecure(InputStream in);

	/**
	 * Sets the value of <code>out</code> field without any security checks
	 */
	private static native void setOutUnsecure(PrintStream out);

	/**
	 * Helps to throw an arbitrary throwable without mentioning within
	 * <code>throw</code> clause and so bypass
	 * exception checking by a compiler.
	 *
	 * @see java.lang.Class#newInstance()
	 */
	native static void rethrow(Throwable tr);
}
