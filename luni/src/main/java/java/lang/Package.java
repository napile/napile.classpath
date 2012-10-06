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

import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.AnnotatedElement;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.harmony.vm.VMGenericsAndAnnotations;
import org.apache.harmony.vm.VMStack;

/**
 * Contains information about a Java package. This includes implementation and
 * specification versions. Typically this information is retrieved from the
 * manifest.
 * <p/>
 * Packages are managed by class loaders. All classes loaded by the same loader
 * from the same package share a {@code Package} instance.
 *
 * @see ClassLoader
 * @since 1.0
 */
public class Package implements AnnotatedElement
{

	/**
	 * The defining loader.
	 */
	private final ClassLoader loader;

	/**
	 * A map of {url<String>, attrs<Manifest>} pairs for caching
	 * attributes of bootsrap jars.
	 */
	private static SoftReference<Map<String, Manifest>> jarCache;

	/**
	 * An url of a source jar, for deffered attributes initialization.
	 * After the initialization, if any, is reset to null.
	 */
	private String jar;

	private String implTitle;

	private String implVendor;

	private String implVersion;

	private final String name;

	private URL sealBase;

	private String specTitle;

	private String specVendor;

	private String specVersion;

	/**
	 * Name must not be null.
	 */
	Package(ClassLoader ld, String packageName, String sTitle, String sVersion, String sVendor, String iTitle, String iVersion, String iVendor, URL base)
	{
		loader = ld;
		name = packageName;
		specTitle = sTitle;
		specVersion = sVersion;
		specVendor = sVendor;
		implTitle = iTitle;
		implVersion = iVersion;
		implVendor = iVendor;
		sealBase = base;
	}

	/**
	 * Lazy initialization constructor; this Package instance will try to
	 * resolve optional attributes only if such value is requested.
	 * Name must not be null.
	 */
	Package(ClassLoader ld, String packageName, String jar)
	{
		loader = ld;
		name = packageName;
		this.jar = jar;
	}

	/**
	 * Gets the annotation associated with the specified annotation type and
	 * this package, if present.
	 *
	 * @param annotationType the annotation type to look for.
	 * @return an instance of {@link Annotation} or {@code null}.
	 * @see java.lang.reflect.AnnotatedElement#getAnnotation(java.lang.Class)
	 * @since 1.5
	 */
	@SuppressWarnings("unchecked")
	public <A extends Annotation> A getAnnotation(Class<A> annotationClass)
	{
		if(annotationClass == null)
		{
			throw new NullPointerException();
		}
		Annotation aa[] = getAnnotations();
		for(int i = 0; i < aa.length; i++)
		{
			if(aa[i].annotationType().equals(annotationClass))
			{
				return (A) aa[i];
			}
		}
		return null;
	}

	/**
	 * Gets all annotations associated with this package, if any.
	 *
	 * @return an array of {@link Annotation} instances, which may be empty.
	 * @see java.lang.reflect.AnnotatedElement#getAnnotations()
	 * @since 1.5
	 */
	public Annotation[] getAnnotations()
	{
		return getDeclaredAnnotations();
	}

	/**
	 * Gets all annotations directly declared on this package, if any.
	 *
	 * @return an array of {@link Annotation} instances, which may be empty.
	 * @see java.lang.reflect.AnnotatedElement#getDeclaredAnnotations()
	 * @since 1.5
	 */
	public Annotation[] getDeclaredAnnotations()
	{
		Class pc = null;
		try
		{
			//default package cannot be annotated
			pc = Class.forName(getName() + ".package-info", false, loader);
		}
		catch(ClassNotFoundException _)
		{
			return new Annotation[0];
		}
		return VMGenericsAndAnnotations.getDeclaredAnnotations(pc); // get all annotations directly present on this element
	}

	/**
	 * Indicates whether the specified annotation is present.
	 *
	 * @param annotationType the annotation type to look for.
	 * @return {@code true} if the annotation is present; {@code false}
	 *         otherwise.
	 * @see java.lang.reflect.AnnotatedElement#isAnnotationPresent(java.lang.Class)
	 * @since 1.5
	 */
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass)
	{
		return getAnnotation(annotationClass) != null;
	}

	/**
	 * Returns the title of the implementation of this package, or {@code null}
	 * if this is unknown. The format of this string is unspecified.
	 *
	 * @return the implementation title, may be {@code null}.
	 */
	public String getImplementationTitle()
	{
		if(jar != null)
		{
			init();
		}
		return implTitle;
	}

	/**
	 * Returns the name of the vendor or organization that provides this
	 * implementation of the package, or {@code null} if this is unknown. The
	 * format of this string is unspecified.
	 *
	 * @return the implementation vendor name, may be {@code null}.
	 */
	public String getImplementationVendor()
	{
		if(jar != null)
		{
			init();
		}
		return implVendor;
	}

	/**
	 * Returns the version of the implementation of this package, or {@code
	 * null} if this is unknown. The format of this string is unspecified.
	 *
	 * @return the implementation version, may be {@code null}.
	 */
	public String getImplementationVersion()
	{
		if(jar != null)
		{
			init();
		}
		return implVersion;
	}

	/**
	 * Returns the name of this package in the standard dot notation; for
	 * example: "java.lang".
	 *
	 * @return the name of this package.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Attempts to locate the requested package in the caller's class loader. If
	 * no package information can be located, {@code null} is returned.
	 *
	 * @param packageName the name of the package to find.
	 * @return the requested package, or {@code null}.
	 * @see ClassLoader#getPackage(java.lang.String)
	 */
	public static Package getPackage(String name)
	{
		ClassLoader callerLoader = VMClassRegistry.getClassLoader(VMStack.getCallerClass(0));
		return callerLoader == null ? ClassLoader.BootstrapLoader.getPackage(name) : callerLoader.getPackage(name);
	}


	/**
	 * Returns all the packages known to the caller's class loader.
	 *
	 * @return all the packages known to the caller's class loader.
	 * @see ClassLoader#getPackages
	 */
	public static Package[] getPackages()
	{
		ClassLoader callerLoader = VMClassRegistry.getClassLoader(VMStack.getCallerClass(0));
		if(callerLoader == null)
		{
			Collection<Package> pkgs = ClassLoader.BootstrapLoader.getPackages();
			return (Package[]) pkgs.toArray(new Package[pkgs.size()]);
		}
		return callerLoader.getPackages();
	}

	/**
	 * Returns the title of the specification this package implements, or
	 * {@code null} if this is unknown.
	 *
	 * @return the specification title, may be {@code null}.
	 */
	public String getSpecificationTitle()
	{
		if(jar != null)
		{
			init();
		}
		return specTitle;
	}

	/**
	 * Returns the name of the vendor or organization that owns and maintains
	 * the specification this package implements, or {@code null} if this is
	 * unknown.
	 *
	 * @return the specification vendor name, may be {@code null}.
	 */
	public String getSpecificationVendor()
	{
		if(jar != null)
		{
			init();
		}
		return specVendor;
	}

	/**
	 * Returns the version of the specification this package implements, or
	 * {@code null} if this is unknown. The version string is a sequence of
	 * non-negative integers separated by dots; for example: "1.2.3".
	 *
	 * @return the specification version string, may be {@code null}.
	 */
	public String getSpecificationVersion()
	{
		if(jar != null)
		{
			init();
		}
		return specVersion;
	}


	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	/**
	 * Indicates whether this package's specification version is compatible with
	 * the specified version string. Version strings are compared by comparing
	 * each dot separated part of the version as an integer.
	 *
	 * @param version the version string to compare against.
	 * @return {@code true} if the package versions are compatible; {@code
	 *         false} otherwise.
	 * @throws NumberFormatException if this package's version string or the one provided are not
	 *                               in the correct format.
	 */

	public boolean isCompatibleWith(String desiredVersion) throws NumberFormatException
	{

		if(jar != null)
		{
			init();
		}

		if(specVersion == null || specVersion.length() == 0)
		{
			throw new NumberFormatException("No specification version defined for the package");
		}

		if(!specVersion.matches("[\\p{javaDigit}]+(.[\\p{javaDigit}]+)*"))
		{
			throw new NumberFormatException("Package specification version is not of the correct dotted form : " + specVersion);
		}

		if(desiredVersion == null || desiredVersion.length() == 0)
		{
			throw new NumberFormatException("Empty version to check");
		}

		if(!desiredVersion.matches("[\\p{javaDigit}]+(.[\\p{javaDigit}]+)*"))
		{
			throw new NumberFormatException("Desired version is not of the correct dotted form : " + desiredVersion);
		}

		StringTokenizer specVersionTokens = new StringTokenizer(specVersion, ".");

		StringTokenizer desiredVersionTokens = new StringTokenizer(desiredVersion, ".");

		try
		{
			while(specVersionTokens.hasMoreElements())
			{
				int desiredVer = Integer.parseInt(desiredVersionTokens.nextToken());
				int specVer = Integer.parseInt(specVersionTokens.nextToken());
				if(specVer != desiredVer)
				{
					return specVer > desiredVer;
				}
			}
		}
		catch(NoSuchElementException e)
		{
		   /*
			* run out of tokens for desiredVersion
            */
		}

        /*
         *   now, if desired is longer than spec, and they have been
         *   equal so far (ex.  1.4  <->  1.4.0.0) then the remainder
         *   better be zeros
         */

		while(desiredVersionTokens.hasMoreTokens())
		{
			if(0 != Integer.parseInt(desiredVersionTokens.nextToken()))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Indicates whether this package is sealed.
	 *
	 * @return {@code true} if this package is sealed; {@code false} otherwise.
	 */
	public boolean isSealed()
	{
		if(jar != null)
		{
			init();
		}
		return sealBase != null;
	}


	/**
	 * Indicates whether this package is sealed with respect to the specified
	 * URL.
	 *
	 * @param url the URL to check.
	 * @return {@code true} if this package is sealed with {@code url}; {@code
	 *         false} otherwise
	 */
	public boolean isSealed(URL url)
	{
		if(jar != null)
		{
			init();
		}
		return url.equals(sealBase);
	}

	@Override
	public String toString()
	{
		if(jar != null)
		{
			init();
		}
		return "package " + name + (specTitle != null ? " " + specTitle : "") + (specVersion != null ? " " + specVersion : "");
	}

	/**
	 * Performs initialization of optional attributes, if the source jar location
	 * was specified in the lazy constructor.
	 */
	private void init()
	{
		try
		{
			Map<String, Manifest> map = null;
			Manifest manifest = null;
			URL sealURL = null;
			if(jarCache != null && (map = jarCache.get()) != null)
			{
				manifest = map.get(jar);
			}

			if(manifest == null)
			{
				final URL url = sealURL = new URL(jar);

				manifest = AccessController.doPrivileged(new PrivilegedAction<Manifest>()
				{
					public Manifest run()
					{
						try
						{
							return ((JarURLConnection) url.openConnection()).getManifest();
						}
						catch(Exception e)
						{
							return new Manifest();
						}
					}
				});
				if(map == null)
				{
					map = new Hashtable<String, Manifest>();
					if(jarCache == null)
					{
						jarCache = new SoftReference<Map<String, Manifest>>(map);
					}
				}
				map.put(jar, manifest);
			}

			Attributes mainAttrs = manifest.getMainAttributes();
			Attributes pkgAttrs = manifest.getAttributes(name.replace('.', '/') + "/");

			specTitle = pkgAttrs == null || (specTitle = pkgAttrs.getValue(Attributes.Name.SPECIFICATION_TITLE)) == null ? mainAttrs.getValue(Attributes.Name.SPECIFICATION_TITLE) : specTitle;
			specVersion = pkgAttrs == null || (specVersion = pkgAttrs.getValue(Attributes.Name.SPECIFICATION_VERSION)) == null ? mainAttrs.getValue(Attributes.Name.SPECIFICATION_VERSION) : specVersion;
			specVendor = pkgAttrs == null || (specVendor = pkgAttrs.getValue(Attributes.Name.SPECIFICATION_VENDOR)) == null ? mainAttrs.getValue(Attributes.Name.SPECIFICATION_VENDOR) : specVendor;
			implTitle = pkgAttrs == null || (implTitle = pkgAttrs.getValue(Attributes.Name.IMPLEMENTATION_TITLE)) == null ? mainAttrs.getValue(Attributes.Name.IMPLEMENTATION_TITLE) : implTitle;
			implVersion = pkgAttrs == null || (implVersion = pkgAttrs.getValue(Attributes.Name.IMPLEMENTATION_VERSION)) == null ? mainAttrs.getValue(Attributes.Name.IMPLEMENTATION_VERSION) : implVersion;
			implVendor = pkgAttrs == null || (implVendor = pkgAttrs.getValue(Attributes.Name.IMPLEMENTATION_VENDOR)) == null ? mainAttrs.getValue(Attributes.Name.IMPLEMENTATION_VENDOR) : implVendor;
			String sealed = pkgAttrs == null || (sealed = pkgAttrs.getValue(Attributes.Name.SEALED)) == null ? mainAttrs.getValue(Attributes.Name.SEALED) : sealed;
			if(Boolean.valueOf(sealed).booleanValue())
			{
				sealBase = sealURL != null ? sealURL : new URL(jar);
			}
		}
		catch(Exception e)
		{
		}
		jar = null;
	}
}
