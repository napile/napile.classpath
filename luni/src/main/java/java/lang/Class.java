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

import static org.apache.harmony.vm.ClassFormat.ACC_ANNOTATION;
import static org.apache.harmony.vm.ClassFormat.ACC_ENUM;
import static org.apache.harmony.vm.ClassFormat.ACC_INTERFACE;
import static org.apache.harmony.vm.ClassFormat.ACC_SYNTHETIC;

import java.io.Externalizable;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.ref.SoftReference;
import java.lang.reflect.*;
import java.net.URL;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.harmony.lang.RuntimePermissionCollection;
import org.apache.harmony.lang.reflect.Reflection;
import org.apache.harmony.lang.reflect.parser.Parser;
import org.apache.harmony.vm.VMGenericsAndAnnotations;
import org.apache.harmony.vm.VMStack;

/**
 * The in-memory representation of a Java class. This representation serves as
 * the starting point for querying class-related information, a process usually
 * called "reflection". There are basically three types of {@code Class}
 * instances: those representing real classes and interfaces, those representing
 * primitive types, and those representing array classes.
 * <p/>
 * <h4>Class instances representing object types (classes or interfaces)</h4>
 * <p>
 * These represent an ordinary class or interface as found in the class
 * hierarchy. The name associated with these {@code Class} instances is simply
 * the fully qualified class name of the class or interface that it represents.
 * In addition to this human-readable name, each class is also associated by a
 * so-called <em>signature</em>, which is the letter "L", followed by the
 * class name and a semicolon (";"). The signature is what the runtime system
 * uses internally for identifying the class (for example in a DEX file).
 * </p>
 * <h4>Classes representing primitive types</h4>
 * <p>
 * These represent the standard Java primitive types and hence share their
 * names (for example "int" for the {@code int} primitive type). Although it is
 * not possible to create new instances based on these {@code Class} instances,
 * they are still useful for providing reflection information, and as the
 * component type of array classes. There is one {@code Class} instance for each
 * primitive type, and their signatures are:
 * </p>
 * <ul>
 * <li>{@code B} representing the {@code byte} primitive type</li>
 * <li>{@code S} representing the {@code short} primitive type</li>
 * <li>{@code I} representing the {@code int} primitive type</li>
 * <li>{@code J} representing the {@code long} primitive type</li>
 * <li>{@code F} representing the {@code float} primitive type</li>
 * <li>{@code D} representing the {@code double} primitive type</li>
 * <li>{@code C} representing the {@code char} primitive type</li>
 * <li>{@code Z} representing the {@code boolean} primitive type</li>
 * <li>{@code V} representing void function return values</li>
 * </ul>
 * <p>
 * <h4>Classes representing array classes</h4>
 * <p>
 * These represent the classes of Java arrays. There is one such {@code Class}
 * instance per combination of array leaf component type and arity (number of
 * dimensions). In this case, the name associated with the {@code Class}
 * consists of one or more left square brackets (one per dimension in the array)
 * followed by the signature of the class representing the leaf component type,
 * which can be either an object type or a primitive type. The signature of a
 * {@code Class} representing an array type is the same as its name. Examples
 * of array class signatures are:
 * </p>
 * <ul>
 * <li>{@code [I} representing the {@code int[]} type</li>
 * <li>{@code [Ljava/lang/String;} representing the {@code String[]} type</li>
 * <li>{@code [[[C} representing the {@code char[][][]} type (three dimensions!)</li>
 * </ul>
 *
 * @since 1.0
 * @author Evgueni Brevnov, Serguei S. Zapreyev, Alexey V. Varlamov
 */
public final class Class<T> implements Serializable, AnnotatedElement, GenericDeclaration, Type
{
	private final class ReflectionData
	{

		final String packageName;
		final String name;
		final boolean isPrimitive;
		final boolean isArray;

		/*
		 * Do no access the following fields directly from enclosing class;
		 * use the accessor methods
		 */
		private volatile int _modifiers;
		private volatile Constructor<T>[] _declaredConstructors;
		private volatile Field[] _declaredFields;
		private volatile Method[] _declaredMethods;
		private volatile Constructor<T> _defaultConstructor;
		private volatile Constructor<T>[] _publicConstructors;
		private volatile Field[] _publicFields;
		private volatile Method[] _publicMethods;

		private volatile boolean _serialPropsResolved;
		private boolean _isExternalizable;
		private boolean _isSerializable;

		ReflectionData()
		{
			name = VMClassRegistry.getName(Class.this);
			isPrimitive = VMClassRegistry.isPrimitive(Class.this);
			isArray = VMClassRegistry.isArray(Class.this);
			packageName = Class.getParentName(name);
			_modifiers = -1;
		}

		boolean isSerializable()
		{
			resolveSerialProps();
			return _isSerializable;
		}

		boolean isExternalizable()
		{
			resolveSerialProps();
			return _isExternalizable;
		}

		private void resolveSerialProps()
		{
			if(!_serialPropsResolved)
			{
				_isExternalizable = VMClassRegistry.isAssignableFrom(EXTERNALIZABLE_CLASS, Class.this);
				_isSerializable = VMClassRegistry.isAssignableFrom(SERIALIZABLE_CLASS, Class.this);
				_serialPropsResolved = true;
			}
		}

		int getModifiers()
		{
			final int localCopy = _modifiers;
			if(localCopy != -1)
			{
				return localCopy;
			}
			return _modifiers = VMClassRegistry.getModifiers(Class.this);
		}

		Constructor<T>[] getDeclaredConstructors()
		{
			final Constructor<T>[] localCopy = _declaredConstructors;
			if(localCopy != null)
			{
				return localCopy;
			}
			return _declaredConstructors = VMClassRegistry.getDeclaredConstructors(Class.this);
		}

		Field[] getDeclaredFields()
		{
			final Field[] localCopy = _declaredFields;
			if(localCopy == null)
			{
				return _declaredFields = VMClassRegistry.getDeclaredFields(Class.this);
			}
			else
			{
				return localCopy;
			}
		}

		Method[] getDeclaredMethods()
		{
			final Method[] localCopy = _declaredMethods;
			if(localCopy != null)
			{
				return localCopy;
			}
			return _declaredMethods = VMClassRegistry.getDeclaredMethods(Class.this);
		}

		Constructor<T> getDefaultConstructor() throws NoSuchMethodException
		{
			final Constructor<T> localCopy = _defaultConstructor;
			if(localCopy != null)
			{
				return localCopy;
			}
			return _defaultConstructor = Class.this.getDeclaredConstructorInternal(null);
		}

		Constructor<T>[] getPublicConstructors()
		{
			final Constructor<T>[] localCopy = _publicConstructors;
			if(localCopy != null)
			{
				return localCopy;
			}

			final Constructor<T>[] declaredConstructors = getDeclaredConstructors();
			ArrayList<Constructor<T>> constructors = new ArrayList<Constructor<T>>(declaredConstructors.length);
			for(int i = 0; i < declaredConstructors.length; i++)
			{
				Constructor<T> c = declaredConstructors[i];
				if(Modifier.isPublic(c.getModifiers()))
				{
					constructors.add(c);
				}
			}
			final int size = constructors.size();
			@SuppressWarnings("unchecked")
			final Constructor<T>[] tempArray = (Constructor<T>[]) new Constructor[size];
			return _publicConstructors = constructors.toArray(tempArray);
		}

		/**
		 * Stores public fields in order they should be searched by
		 * getField(name) method.
		 */
		public synchronized Field[] getPublicFields()
		{
			final Field[] localCopy = _publicFields;
			if(localCopy != null)
			{
				return localCopy;
			}

			final Field[] declaredFields = getDeclaredFields();

			// initialize public fields of the super class
			int size = declaredFields.length;
			Class<?> superClass = Class.this.getSuperclass();
			Field[] superFields = null;
			if(superClass != null)
			{
				final Class<?>.ReflectionData superClassRefData = superClass.getReflectionData();
				superFields = superClassRefData.getPublicFields();
				size += superFields.length;
			}

			// add public fields of this class
			Collection<Field> fields = new LinkedHashSet<Field>(size);
			for(Field f : declaredFields)
			{
				if(Modifier.isPublic(f.getModifiers()))
				{
					fields.add(f);
				}
			}

			// initialize and add fields of the super interfaces
			Class<?>[] interfaces = Class.this.getInterfaces();
			for(Class<?> ci : interfaces)
			{
				final Class<?>.ReflectionData ciRefData = ci.getReflectionData();
				Field[] fi = ciRefData.getPublicFields();
				for(Field f : fi)
				{
					fields.add(f);
				}
			}

			// add public fields of the super class
			if(superFields != null)
			{
				for(Field f : superFields)
				{
					if(Modifier.isPublic(f.getModifiers()))
					{
						fields.add(f);
					}
				}
			}

			return _publicFields = fields.toArray(new Field[fields.size()]);
		}

		public synchronized Method[] getPublicMethods()
		{
			final Method[] localCopy = _publicMethods;
			if(localCopy != null)
			{
				return localCopy;
			}

			final Method[] declaredMethods = getDeclaredMethods();

			// initialize public methods of the super class
			int size = declaredMethods.length;
			Class<?> superClass = Class.this.getSuperclass();
			Method[] superPublic = null;
			if(superClass != null)
			{
				final Class<?>.ReflectionData superClassRefData = superClass.getReflectionData();
				superPublic = superClassRefData.getPublicMethods();
				size += superPublic.length;
			}

			// add methods of the super interfaces
			Class<?>[] interfaces = Class.this.getInterfaces();
			Method[][] intf = null;
			if(interfaces.length != 0)
			{
				intf = new Method[interfaces.length][];
				for(int i = 0; i < interfaces.length; i++)
				{
					Class<?> ci = interfaces[i];
					final Class<?>.ReflectionData ciRefData = ci.getReflectionData();
					intf[i] = ciRefData.getPublicMethods();
					size += intf[i].length;
				}
			}
			return _publicMethods = Reflection.mergePublicMethods(declaredMethods, superPublic, intf, size);
		}
	}

	private final class GACache
	{
		private Annotation[] allAnnotations;
		private Annotation[] declaredAnnotations;
		private Type[] genericInterfaces;
		private Type genericSuperclass;
		private TypeVariable<Class<T>>[] typeParameters;

		public synchronized Annotation[] getAllAnnotations()
		{
			if(allAnnotations != null)
			{
				return allAnnotations;
			}
			if(declaredAnnotations == null)
			{
				declaredAnnotations = VMGenericsAndAnnotations.getDeclaredAnnotations(Class.this);
			}

			// look for inherited annotations
			Class<?> superClass = Class.this.getSuperclass();
			if(superClass != null)
			{
				Annotation[] sa = superClass.getCache().getAllAnnotations();
				if(sa.length != 0)
				{
					final int size = declaredAnnotations.length;
					Annotation[] all = new Annotation[size + sa.length];
					System.arraycopy(declaredAnnotations, 0, all, 0, size);
					int pos = size;
					next:
					for(Annotation s : sa)
					{
						if(s.annotationType().isAnnotationPresent(INHERITED_CLASS))
						{
							for(int i = 0; i < size; i++)
							{
								if(all[i].annotationType().equals(s.annotationType()))
								{
									// overriden by declared annotation
									continue next;
								}
							}
							all[pos++] = s;
						}
					}
					allAnnotations = new Annotation[pos];
					System.arraycopy(all, 0, allAnnotations, 0, pos);
					return allAnnotations;
				}
			}
			return allAnnotations = declaredAnnotations;
		}

		public Annotation[] getDeclaredAnnotations()
		{
			if(declaredAnnotations == null)
			{
				declaredAnnotations = VMGenericsAndAnnotations.getDeclaredAnnotations(Class.this);
			}
			return declaredAnnotations;
		}

		public synchronized Type[] getGenericInterfaces()
		{
			if(genericInterfaces == null)
			{
				genericInterfaces = Parser.getGenericInterfaces(Class.this, VMGenericsAndAnnotations.getSignature(Class.this));
			}
			return genericInterfaces;
		}

		public Type getGenericSuperclass()
		{
			//So, here it can be only ParameterizedType or ordinary reference class type
			if(genericSuperclass == null)
			{
				genericSuperclass = Parser.getGenericSuperClass(Class.this, VMGenericsAndAnnotations.getSignature(Class.this));
			}
			return genericSuperclass;
		}

		@SuppressWarnings("unchecked")
		public synchronized TypeVariable<Class<T>>[] getTypeParameters()
		{
			if(typeParameters == null)
			{
				typeParameters = Parser.getTypeParameters(Class.this, VMGenericsAndAnnotations.getSignature(Class.this));
			}
			return typeParameters;
		}
	}

	private static final long serialVersionUID = 3206093459760846163L;

	/**
	 * Global/system assertion status
	 * <p/>
	 * package private to access from the java.lang.ClassLoader class.
	 */
	static volatile boolean disableAssertions = VMExecutionEngine.getAssertionStatus(null, false, 0) <= 0;

	static ProtectionDomain systemDomain;

	private static final Class<Cloneable> CLONEABLE_CLASS = Cloneable.class;
	@SuppressWarnings("unchecked")
	private static final Class<Enum> ENUM_CLASS = Enum.class;
	private static final Class<Externalizable> EXTERNALIZABLE_CLASS = Externalizable.class;
	private static final Class<Inherited> INHERITED_CLASS = Inherited.class;
	private static final Class<Object> OBJECT_CLASS = Object.class;
	private static final Class<Serializable> SERIALIZABLE_CLASS = Serializable.class;

	/**
	 * Provides strong referencing between the classloader
	 * and it's defined classes. Intended for class unloading implementation.
	 *
	 * @see java.lang.ClassLoader#loadedClasses
	 */
	ClassLoader definingLoader;

	transient SoftReference<GACache> softCache;

	private transient volatile ReflectionData _reflectionData;

	private transient ProtectionDomain domain;

	/**
	 * It is required for synchronization in newInstance method.
	 */
	private volatile boolean isDefaultConstructorInitialized;


	private Class()
	{
		// prevent this class to be instantiated, instance should be created by
		// JVM only
	}

	/**
	 * This must be provided by the VM vendor, as it is used by other provided
	 * class implementations in this package. This method is used by
	 * SecurityManager.classDepth(), and getClassContext() which use the
	 * parameters (-1, false) and SecurityManager.classLoaderDepth(),
	 * currentClassLoader(), and currentLoadedClass() which use the parameters
	 * (-1, true). Walk the stack and answer an array containing the maxDepth
	 * most recent classes on the stack of the calling thread. Starting with the
	 * caller of the caller of getStackClasses(), return an array of not more
	 * than maxDepth Classes representing the classes of running methods on the
	 * stack (including native methods). Frames representing the VM
	 * implementation of java.lang.reflect are not included in the list. If
	 * stopAtPrivileged is true, the walk will terminate at any frame running
	 * one of the following methods: <code><ul>
	 * <li>java/security/AccessController.doPrivileged(Ljava/security/PrivilegedAction;)Ljava/lang/Object;</li>
	 * <li>java/security/AccessController.doPrivileged(Ljava/security/PrivilegedExceptionAction;)Ljava/lang/Object;</li>
	 * <li>java/security/AccessController.doPrivileged(Ljava/security/PrivilegedAction;Ljava/security/AccessControlContext;)Ljava/lang/Object;</li>
	 * <li>java/security/AccessController.doPrivileged(Ljava/security/PrivilegedExceptionAction;Ljava/security/AccessControlContext;)Ljava/lang/Object;</li>
	 * </ul></code> If one of the doPrivileged methods is found, the walk terminate
	 * and that frame is NOT included in the returned array. Notes:
	 * <ul>
	 * <li>This method operates on the defining classes of methods on stack.
	 * NOT the classes of receivers.</li>
	 * <li>The item at index zero in the result array describes the caller of
	 * the caller of this method.</li>
	 * </ul>
	 *
	 * @param maxDepth         maximum depth to walk the stack, -1 for the entire stack
	 * @param stopAtPrivileged stop at privileged classes
	 * @return the array of the most recent classes on the stack
	 */
	static final Class<?>[] getStackClasses(int maxDepth, boolean stopAtPrivileged)
	{
		return VMStack.getClasses(maxDepth, stopAtPrivileged);
	}

	/**
	 * Returns a {@code Class} object which represents the class with the
	 * specified name. The name should be the name of a class as described in
	 * the {@link Class class definition}; however, {@code Class}es representing
	 * primitive types can not be found using this method.
	 * <p/>
	 * If the class has not been loaded so far, it is being loaded and linked
	 * first. This is done through either the class loader of the calling class
	 * or one of its parent class loaders. The class is also being initialized,
	 * which means that a possible static initializer block is executed.
	 *
	 * @param className the name of the non-primitive-type class to find.
	 * @return the named {@code Class} instance.
	 * @throws ClassNotFoundException      if the requested class can not be found.
	 * @throws LinkageError                if an error occurs during linkage
	 * @throws ExceptionInInitializerError if an exception occurs during static initialization of a
	 *                                     class.
	 */
	public static Class<?> forName(String name) throws ClassNotFoundException
	{
		return forName(name, true, VMClassRegistry.getClassLoader(VMStack.getCallerClass(0)));
	}


	/**
	 * Returns a {@code Class} object which represents the class with the
	 * specified name. The name should be the name of a class as described in
	 * the {@link Class class definition}, however {@code Class}es representing
	 * primitive types can not be found using this method. Security rules will
	 * be obeyed.
	 * <p/>
	 * If the class has not been loaded so far, it is being loaded and linked
	 * first. This is done through either the specified class loader or one of
	 * its parent class loaders. The caller can also request the class to be
	 * initialized, which means that a possible static initializer block is
	 * executed.
	 *
	 * @param className         the name of the non-primitive-type class to find.
	 * @param initializeBoolean indicates whether the class should be initialized.
	 * @param classLoader       the class loader to use to load the class.
	 * @return the named {@code Class} instance.
	 * @throws ClassNotFoundException      if the requested class can not be found.
	 * @throws LinkageError                if an error occurs during linkage
	 * @throws ExceptionInInitializerError if an exception occurs during static initialization of a
	 *                                     class.
	 */
	public static Class<?> forName(String name, boolean initialize, ClassLoader classLoader) throws ClassNotFoundException
	{
		if(name == null)
		{
			throw new NullPointerException();
		}
		if(name.indexOf("/") != -1)
		{
			throw new ClassNotFoundException(name);
		}

		Class<?> clazz = null;

		if(classLoader == null)
		{
			SecurityManager sc = System.getSecurityManager();
			if(sc != null && VMClassRegistry.getClassLoader(VMStack.getCallerClass(0)) != null)
			{
				sc.checkPermission(RuntimePermissionCollection.GET_CLASS_LOADER_PERMISSION);
			}
			clazz = VMClassRegistry.loadBootstrapClass(name);
		}
		else
		{
			int dims = 0;
			int len = name.length();
			while(dims < len && name.charAt(dims) == '[')
				dims++;
			if(dims > 0 && len > dims + 1 && name.charAt(dims) == 'L' && name.endsWith(";"))
			{				/*
				 * an array of a reference type is requested.
                 * do not care of arrays of primitives as
                 * they are perfectly loaded by bootstrap classloader.
                 */
				try
				{
					clazz = classLoader.loadClass(name.substring(dims + 1, len - 1));
				}
				catch(ClassNotFoundException ignore)
				{
				}
				if(clazz != null)
				{
					clazz = VMClassRegistry.loadArray(clazz, dims);
				}
			}
			else
			{
				clazz = classLoader.loadClass(name);
			}
		}
		if(clazz == null)
		{
			throw new ClassNotFoundException(name);
		}
		if(classLoader != null)
		{			/*
			 * Although class loader may have had a chance to register itself as
             * initiating for requested class, there may occur a classloader
             * which overloads loadClass method (though it is not recommended by
             * J2SE specification). Try to register initiating loader for clazz
             * from here again
             */
			classLoader.registerInitiatedClass(clazz);
		}
		if(initialize)
		{
			VMClassRegistry.initializeClass(clazz);
		}
		else
		{
			VMClassRegistry.linkClass(clazz);
		}
		return clazz;
	}

	/**
	 * Returns an array containing {@code Class} objects for all public classes
	 * and interfaces that are members of this class. This includes public
	 * members inherited from super classes and interfaces. If there are no such
	 * class members or if this object represents a primitive type then an array
	 * of length 0 is returned.
	 *
	 * @return the public class members of the class represented by this object.
	 * @throws SecurityException if a security manager exists and it does not allow member
	 *                           access.
	 */
	/**
	 * Note: We don't check member access permission for each super class.
	 * Java 1.5 API specification doesn't require this check.
	 */
	public Class[] getClasses()
	{
		checkMemberAccess(Member.PUBLIC);
		Class<?> clss = this;
		ArrayList<Class<?>> classes = null;
		while(clss != null)
		{
			Class<?>[] declared = VMClassRegistry.getDeclaredClasses(clss);
			if(declared.length != 0)
			{
				if(classes == null)
				{
					classes = new ArrayList<Class<?>>();
				}
				for(Class<?> c : declared)
				{
					if(Modifier.isPublic(c.getModifiers()))
					{
						classes.add(c);
					}
				}
			}
			clss = clss.getSuperclass();
		}
		if(classes == null)
		{
			return new Class[0];
		}
		else
		{
			return classes.toArray(new Class[classes.size()]);
		}
	}

	/**
	 * Verify the specified Class using the VM byte code verifier.
	 *
	 * @throws VerifyError if the Class cannot be verified
	 */
	void verify()
	{
		return;
	}

	/**
	 * Returns the annotation of the given type. If there is no such annotation
	 * then the method returns {@code null}.
	 *
	 * @param annotationClass the annotation type.
	 * @return the annotation of the given type, or {@code null} if there is no
	 *         such annotation.
	 * @since 1.5
	 */
	@SuppressWarnings("unchecked")
	public <A extends Annotation> A getAnnotation(Class<A> annotationClass)
	{
		if(annotationClass == null)
		{
			throw new NullPointerException();
		}
		for(Annotation aa : getCache().getAllAnnotations())
		{
			if(annotationClass == aa.annotationType())
			{
				return (A) aa;
			}
		}
		return null;
	}

	/**
	 * Returns all the annotations of this class. If there are no annotations
	 * then an empty array is returned.
	 *
	 * @return a copy of the array containing this class' annotations.
	 * @see #getDeclaredAnnotations()
	 */
	public Annotation[] getAnnotations()
	{
		Annotation[] all = getCache().getAllAnnotations();
		Annotation aa[] = new Annotation[all.length];
		System.arraycopy(all, 0, aa, 0, all.length);
		return aa;
	}

	/**
	 * Returns the canonical name of this class. If this class does not have a
	 * canonical name as defined in the Java Language Specification, then the
	 * method returns {@code null}.
	 *
	 * @return this class' canonical name, or {@code null} if it does not have a
	 *         canonical name.
	 */
	public String getCanonicalName()
	{
		if(isLocalClass() || isAnonymousClass())
		{
			return null;
		}
		if(isArray())
		{
			String res = getComponentType().getCanonicalName();
			return res != null ? res + "[]" : null;
		}

		StringBuffer sb = new StringBuffer(getPackageName());
		ArrayList<String> sympleNames = new ArrayList<String>();
		Class<?> clss = this;
		while((clss = clss.getDeclaringClass()) != null)
		{
			if(clss.isLocalClass() || clss.isAnonymousClass())
			{
				return null;
			}
			sympleNames.add(clss.getSimpleName());
		}
		if(sb.length() > 0)
		{
			sb.append(".");
		}
		for(int i = sympleNames.size() - 1; i > -1; i--)
		{
			sb.append(sympleNames.get(i)).append(".");
		}
		sb.append(getSimpleName());

		return sb.toString();
	}

	/**
	 * Returns the class loader which was used to load the class represented by
	 * this {@code Class}. Implementations are free to return {@code null} for
	 * classes that were loaded by the bootstrap class loader.
	 *
	 * @return the class loader for the represented class.
	 * @throws SecurityException if a security manager exists and it does not allow accessing
	 *                           the class loader.
	 * @see ClassLoader
	 */

	public ClassLoader getClassLoader()
	{
		ClassLoader loader = getClassLoaderImpl();
		SecurityManager sc = System.getSecurityManager();
		if(sc != null)
		{
			ClassLoader callerLoader = VMClassRegistry.getClassLoader(VMStack.getCallerClass(0));
			if(callerLoader != null && !callerLoader.isSameOrAncestor(loader))
			{
				sc.checkPermission(RuntimePermissionCollection.GET_CLASS_LOADER_PERMISSION);
			}
		}
		return loader;
	}

	/**
	 * This must be provided by the VM vendor, as it is used by other provided
	 * class implementations in this package. Outside of this class, it is used
	 * by SecurityManager.checkMemberAccess(), classLoaderDepth(),
	 * currentClassLoader() and currentLoadedClass(). Return the ClassLoader for
	 * this Class without doing any security checks. The bootstrap ClassLoader
	 * is returned, unlike getClassLoader() which returns null in place of the
	 * bootstrap ClassLoader.
	 *
	 * @return the ClassLoader
	 * @see ClassLoader#isSystemClassLoader()
	 */
	final ClassLoader getClassLoaderImpl()
	{
		assert (VMClassRegistry.getClassLoader0(this) == definingLoader);
		return definingLoader;
	}

	/**
	 * Returns a {@code Class} object which represents the component type if
	 * this class represents an array type. Returns {@code null} if this class
	 * does not represent an array type. The component type of an array type is
	 * the type of the elements of the array.
	 *
	 * @return the component type of this class.
	 */
	public Class<?> getComponentType()
	{
		if(!isArray())
		{
			return null;
		}
		return VMClassRegistry.getComponentType(this);
	}

	/**
	 * Returns a {@code Constructor} object which represents the public
	 * constructor matching the specified parameter types.
	 *
	 * @param parameterTypes the parameter types of the requested constructor.
	 * @return the constructor described by {@code parameterTypes}.
	 * @throws NoSuchMethodException if the constructor can not be found.
	 * @throws SecurityException     if a security manager exists and it does not allow member
	 *                               access.
	 * @see #getDeclaredConstructor(Class[])
	 */

	public Constructor<T> getConstructor(Class... argumentTypes) throws NoSuchMethodException
	{
		checkMemberAccess(Member.PUBLIC);
		Constructor<T> ctors[] = getReflectionData().getPublicConstructors();
		for(int i = 0; i < ctors.length; i++)
		{
			Constructor<T> c = ctors[i];
			try
			{
				if(isTypeMatches(argumentTypes, c.getParameterTypes()))
				{
					return Reflection.copyConstructor(c);
				}
			}
			catch(LinkageError ignore)
			{
			}
		}
		throw new NoSuchMethodException(getName() + printMethodSignature(argumentTypes));
	}

	/**
	 * Returns an array containing {@code Constructor} objects for all public
	 * constructors for the class represented by this {@code Class}. If there
	 * are no public constructors or if this {@code Class} represents an array
	 * class, a primitive type or void then an empty array is returned.
	 *
	 * @return an array with the public constructors of the class represented by
	 *         this {@code Class}.
	 * @throws SecurityException if a security manager exists and it does not allow member
	 *                           access.
	 * @see #getDeclaredConstructors()
	 */
	public Constructor[] getConstructors()
	{
		checkMemberAccess(Member.PUBLIC);
		return Reflection.copyConstructors(getReflectionData().getPublicConstructors());
	}

	/**
	 * Returns the annotations that are directly defined on the class
	 * represented by this {@code Class}. Annotations that are inherited are not
	 * included in the result. If there are no annotations at all, an empty
	 * array is returned.
	 *
	 * @return a copy of the array containing the annotations defined for the
	 *         class that this {@code Class} represents.
	 * @see #getAnnotations()
	 */
	public Annotation[] getDeclaredAnnotations()
	{
		Annotation[] declared = getCache().getDeclaredAnnotations();
		Annotation aa[] = new Annotation[declared.length];
		System.arraycopy(declared, 0, aa, 0, declared.length);
		return aa;
	}

	/**
	 * Returns an array containing {@code Class} objects for all classes and
	 * interfaces that are declared as members of the class which this {@code
	 * Class} represents. If there are no classes or interfaces declared or if
	 * this class represents an array class, a primitive type or void, then an
	 * empty array is returned.
	 *
	 * @return an array with {@code Class} objects for all the classes and
	 *         interfaces that are used in member declarations.
	 * @throws SecurityException if a security manager exists and it does not allow member
	 *                           access.
	 */
	@SuppressWarnings("unchecked")
	public Class[] getDeclaredClasses()
	{
		checkMemberAccess(Member.DECLARED);
		return VMClassRegistry.getDeclaredClasses(this);
	}

	/**
	 * Returns a {@code Constructor} object which represents the constructor
	 * matching the specified parameter types that is declared by the class
	 * represented by this {@code Class}.
	 *
	 * @param parameterTypes the parameter types of the requested constructor.
	 * @return the constructor described by {@code parameterTypes}.
	 * @throws NoSuchMethodException if the requested constructor can not be found.
	 * @throws SecurityException     if a security manager exists and it does not allow member
	 *                               access.
	 * @see #getConstructor(Class[])
	 */
	@SuppressWarnings("unchecked")
	public Constructor<T> getDeclaredConstructor(Class... argumentTypes) throws NoSuchMethodException
	{
		checkMemberAccess(Member.DECLARED);
		return Reflection.copyConstructor(getDeclaredConstructorInternal(argumentTypes));
	}

	/**
	 * Returns an array containing {@code Constructor} objects for all
	 * constructors declared in the class represented by this {@code Class}. If
	 * there are no constructors or if this {@code Class} represents an array
	 * class, a primitive type or void then an empty array is returned.
	 *
	 * @return an array with the constructors declared in the class represented
	 *         by this {@code Class}.
	 * @throws SecurityException if a security manager exists and it does not allow member
	 *                           access.
	 * @see #getConstructors()
	 */
	@SuppressWarnings("unchecked")
	public Constructor[] getDeclaredConstructors()
	{
		checkMemberAccess(Member.DECLARED);
		return Reflection.copyConstructors(getReflectionData().getDeclaredConstructors());
	}


	/**
	 * Returns a {@code Field} object for the field with the specified name
	 * which is declared in the class represented by this {@code Class}.
	 *
	 * @param name the name of the requested field.
	 * @return the requested field in the class represented by this class.
	 * @throws NoSuchFieldException if the requested field can not be found.
	 * @throws SecurityException    if a security manager exists and it does not allow member
	 *                              access.
	 * @see #getField(String)
	 */
	public Field getDeclaredField(String fieldName) throws NoSuchFieldException
	{
		checkMemberAccess(Member.DECLARED);
		final Field[] declaredFields = getReflectionData().getDeclaredFields();
		for(int i = 0; i < declaredFields.length; i++)
		{
			Field f = declaredFields[i];
			if(fieldName.equals(f.getName()))
			{
				return Reflection.copyField(f);
			}
		}
		throw new NoSuchFieldException(fieldName.toString());
	}

	/**
	 * Returns an array containing {@code Field} objects for all fields declared
	 * in the class represented by this {@code Class}. If there are no fields or
	 * if this {@code Class} represents an array class, a primitive type or void
	 * then an empty array is returned.
	 *
	 * @return an array with the fields declared in the class represented by
	 *         this class.
	 * @throws SecurityException if a security manager exists and it does not allow member
	 *                           access.
	 * @see #getFields()
	 */
	public Field[] getDeclaredFields()
	{
		checkMemberAccess(Member.DECLARED);
		return Reflection.copyFields(getReflectionData().getDeclaredFields());
	}

	/**
	 * Returns a {@code Method} object which represents the method matching the
	 * specified name and parameter types that is declared by the class
	 * represented by this {@code Class}.
	 *
	 * @param name           the requested method's name.
	 * @param parameterTypes the parameter types of the requested method.
	 * @return the method described by {@code name} and {@code parameterTypes}.
	 * @throws NoSuchMethodException if the requested constructor can not be found.
	 * @throws NullPointerException  if {@code name} is {@code null}.
	 * @throws SecurityException     if a security manager exists and it does not allow member
	 *                               access.
	 * @see #getMethod(String, Class[])
	 */
	@SuppressWarnings("unchecked")
	public Method getDeclaredMethod(String methodName, Class... argumentTypes) throws NoSuchMethodException
	{
		checkMemberAccess(Member.DECLARED);
		return Reflection.copyMethod(findMatchingMethod(getReflectionData().getDeclaredMethods(), methodName, argumentTypes));
	}

	/**
	 * Returns an array containing {@code Method} objects for all methods
	 * declared in the class represented by this {@code Class}. If there are no
	 * methods or if this {@code Class} represents an array class, a primitive
	 * type or void then an empty array is returned.
	 *
	 * @return an array with the methods declared in the class represented by
	 *         this {@code Class}.
	 * @throws SecurityException if a security manager exists and it does not allow member
	 *                           access.
	 * @see #getMethods()
	 */
	public Method[] getDeclaredMethods()
	{
		checkMemberAccess(Member.DECLARED);
		return Reflection.copyMethods(getReflectionData().getDeclaredMethods());
	}

	/**
	 * Returns the declaring {@code Class} of this {@code Class}. Returns
	 * {@code null} if the class is not a member of another class or if this
	 * {@code Class} represents an array class, a primitive type or void.
	 *
	 * @return the declaring {@code Class} or {@code null}.
	 */
	public Class<?> getDeclaringClass()
	{
		return VMClassRegistry.getDeclaringClass(this);
	}

	/**
	 * Returns the enclosing {@code Class} of this {@code Class}. If there is no
	 * enclosing class the method returns {@code null}.
	 *
	 * @return the enclosing {@code Class} or {@code null}.
	 */
	public Class<?> getEnclosingClass()
	{
		return VMClassRegistry.getEnclosingClass(this); // see VMClassRegistry.getEnclosingClass() spec
	}

	/**
	 * Gets the enclosing {@code Constructor} of this {@code Class}, if it is an
	 * anonymous or local/automatic class; otherwise {@code null}.
	 *
	 * @return the enclosing {@code Constructor} instance or {@code null}.
	 */
	public Constructor<?> getEnclosingConstructor()
	{
		Member m = VMClassRegistry.getEnclosingMember(this); // see VMClassRegistry.getEnclosingMember() spec
		return m instanceof Constructor ? (Constructor<?>) m : null;
	}

	/**
	 * Gets the enclosing {@code Method} of this {@code Class}, if it is an
	 * anonymous or local/automatic class; otherwise {@code null}.
	 *
	 * @return the enclosing {@code Method} instance or {@code null}.
	 */
	public Method getEnclosingMethod()
	{
		Member m = VMClassRegistry.getEnclosingMember(this); // see VMClassRegistry.getEnclosingMember() spec
		return m instanceof Method ? (Method) m : null;
	}

	/**
	 * Gets the {@code enum} constants associated with this {@code Class}.
	 * Returns {@code null} if this {@code Class} does not represent an {@code
	 * enum} type.
	 *
	 * @return an array with the {@code enum} constants or {@code null}.
	 * @since 1.5
	 */
	@SuppressWarnings("unchecked")
	public T[] getEnumConstants()
	{
		if(isEnum())
		{
			try
			{
				final Method values = getMethod("values");
				AccessController.doPrivileged(new PrivilegedAction()
				{
					public Object run()
					{
						values.setAccessible(true);
						return null;
					}
				});
				return (T[]) values.invoke(null);
			}
			catch(Exception ignore)
			{
			}
		}
		return null;
	}

	/**
	 * Returns a {@code Field} object which represents the public field with the
	 * specified name. This method first searches the class C represented by
	 * this {@code Class}, then the interfaces implemented by C and finally the
	 * superclasses of C.
	 *
	 * @param name the name of the requested field.
	 * @return the public field specified by {@code name}.
	 * @throws NoSuchFieldException if the field can not be found.
	 * @throws SecurityException    if a security manager exists and it does not allow member
	 *                              access.
	 * @see #getDeclaredField(String)
	 */
	public Field getField(String fieldName) throws NoSuchFieldException
	{
		checkMemberAccess(Member.PUBLIC);
		final Field[] fields = getReflectionData().getPublicFields();
		for(Field f : fields)
		{
			if(fieldName.equals(f.getName()))
			{
				return Reflection.copyField(f);
			}
		}
		throw new NoSuchFieldException(fieldName.toString());
	}


	/**
	 * Returns an array containing {@code Field} objects for all public fields
	 * for the class C represented by this {@code Class}. Fields may be declared
	 * in C, the interfaces it implements or in the superclasses of C. The
	 * elements in the returned array are in no particular order.
	 * <p>
	 * If there are no public fields or if this class represents an array class,
	 * a primitive type or {@code void} then an empty array is returned.
	 * </p>
	 *
	 * @return an array with the public fields of the class represented by this
	 *         {@code Class}.
	 * @throws SecurityException if a security manager exists and it does not allow member
	 *                           access.
	 * @see #getDeclaredFields()
	 */
	public Field[] getFields()
	{
		checkMemberAccess(Member.PUBLIC);
		return Reflection.copyFields(getReflectionData().getPublicFields());
	}

	/**
	 * Gets the {@link Type}s of the interfaces that this {@code Class} directly
	 * implements. If the {@code Class} represents a primitive type or {@code
	 * void} then an empty array is returned.
	 *
	 * @return an array of {@link Type} instances directly implemented by the
	 *         class represented by this {@code class}.
	 */
	public Type[] getGenericInterfaces()
	{
		return new Type[0];
	}

	/**
	 * Gets the {@code Type} that represents the superclass of this {@code
	 * class}.
	 *
	 * @return an instance of {@code Type} representing the superclass.
	 * @since 1.5
	 */
	public Type getGenericSuperclass()
	{
		return null;
	}

	/**
	 * Returns an array of {@code Class} objects that match the interfaces
	 * specified in the {@code implements} declaration of the class represented
	 * by this {@code Class}. The order of the elements in the array is
	 * identical to the order in the original class declaration. If the class
	 * does not implement any interfaces, an empty array is returned.
	 *
	 * @return an array with the interfaces of the class represented by this
	 *         class.
	 */
	@SuppressWarnings("unchecked")
	public Class[] getInterfaces()
	{
		return VMClassRegistry.getInterfaces(this);
	}

	/**
	 * Returns a {@code Method} object which represents the public method with
	 * the specified name and parameter types. This method first searches the
	 * class C represented by this {@code Class}, then the superclasses of C and
	 * finally the interfaces implemented by C and finally the superclasses of C
	 * for a method with matching name.
	 *
	 * @param name           the requested method's name.
	 * @param parameterTypes the parameter types of the requested method.
	 * @return the public field specified by {@code name}.
	 * @throws NoSuchMethodException if the method can not be found.
	 * @throws SecurityException     if a security manager exists and it does not allow member
	 *                               access.
	 * @see #getDeclaredMethod(String, Class[])
	 */
	@SuppressWarnings("unchecked")
	public Method getMethod(String methodName, Class... argumentTypes) throws NoSuchMethodException
	{
		checkMemberAccess(Member.PUBLIC);
		return Reflection.copyMethod(findMatchingMethod(getReflectionData().getPublicMethods(), methodName, argumentTypes));
	}


	/**
	 * Returns an array containing {@code Method} objects for all public methods
	 * for the class C represented by this {@code Class}. Methods may be
	 * declared in C, the interfaces it implements or in the superclasses of C.
	 * The elements in the returned array are in no particular order.
	 * <p>
	 * If there are no public methods or if this {@code Class} represents a
	 * primitive type or {@code void} then an empty array is returned.
	 * </p>
	 *
	 * @return an array with the methods of the class represented by this
	 *         {@code Class}.
	 * @throws SecurityException if a security manager exists and it does not allow member
	 *                           access.
	 * @see #getDeclaredMethods()
	 */
	public Method[] getMethods()
	{
		checkMemberAccess(Member.PUBLIC);
		return Reflection.copyMethods(getReflectionData().getPublicMethods());
	}

	/**
	 * Returns an integer that represents the modifiers of the class represented
	 * by this {@code Class}. The returned value is a combination of bits
	 * defined by constants in the {@link Modifier} class.
	 *
	 * @return the modifiers of the class represented by this {@code Class}.
	 */
	public int getModifiers()
	{
		return getReflectionData().getModifiers();
	}

	/**
	 * Returns the name of the class represented by this {@code Class}. For a
	 * description of the format which is used, see the class definition of
	 * {@link Class}.
	 *
	 * @return the name of the class represented by this {@code Class}.
	 */
	public String getName()
	{
		return getReflectionData().name;
	}

	/**
	 * Returns the simple name of the class represented by this {@code Class} as
	 * defined in the source code. If there is no name (that is, the class is
	 * anonymous) then an empty string is returned. If the receiver is an array
	 * then the name of the underlying type with square braces appended (for
	 * example {@code &quot;Integer[]&quot;}) is returned.
	 *
	 * @return the simple name of the class represented by this {@code Class}.
	 */
	public String getSimpleName()
	{
		return VMClassRegistry.getSimpleName(this);
	}


	/**
	 * Returns the {@code ProtectionDomain} of the class represented by this
	 * class.
	 * <p>
	 * Note: In order to conserve space in an embedded target like Android, we
	 * allow this method to return {@code null} for classes in the system
	 * protection domain (that is, for system classes). System classes are
	 * always given full permissions (that is, AllPermission). This can not be
	 * changed through the {@link java.security.Policy} class.
	 * </p>
	 *
	 * @return the {@code ProtectionDomain} of the class represented by this
	 *         class.
	 * @throws SecurityException if a security manager exists and it does not allow member
	 *                           access.
	 */
	public ProtectionDomain getProtectionDomain()
	{
		SecurityManager sc = System.getSecurityManager();
		if(sc != null)
		{
			sc.checkPermission(RuntimePermissionCollection.GET_PROTECTION_DOMAIN_PERMISSION);
		}
		if(domain == null)
		{
			if(systemDomain == null)
			{
				Permissions allPermissions = new Permissions();
				allPermissions.add(new AllPermission());
				systemDomain = new ProtectionDomain(null, allPermissions);
			}
			return systemDomain;
		}
		return domain;
	}

	/**
	 * Returns the URL of the resource specified by {@code resName}. The mapping
	 * between the resource name and the URL is managed by the class' class
	 * loader.
	 *
	 * @param resName the name of the resource.
	 * @return the requested resource's {@code URL} object or {@code null} if
	 *         the resource can not be found.
	 * @see ClassLoader
	 */
	public URL getResource(String resource)
	{
		resource = getAbsoluteResource(resource);
		ClassLoader classLoader = getClassLoaderImpl();
		return classLoader == null ? ClassLoader.getSystemResource(resource) : classLoader.getResource(resource);
	}

	/**
	 * Returns a read-only stream for the contents of the resource specified by
	 * {@code resName}. The mapping between the resource name and the stream is
	 * managed by the class' class loader.
	 *
	 * @param resName the name of the resource.
	 * @return a stream for the requested resource or {@code null} if no
	 *         resource with the specified name can be found.
	 * @see ClassLoader
	 */
	public InputStream getResourceAsStream(String resource)
	{
		resource = getAbsoluteResource(resource);
		ClassLoader classLoader = getClassLoaderImpl();
		return classLoader == null ? ClassLoader.getSystemResourceAsStream(resource) : classLoader.getResourceAsStream(resource);
	}

	/**
	 * Returns the signers for the class represented by this {@code Class} or
	 * {@code null} if either there are no signers or this {@code Class}
	 * represents a primitive type or void.
	 *
	 * @return the signers of the class represented by this {@code Class}.
	 */
	public Object[] getSigners()
	{
		try
		{
			Object[] signers = (Object[]) getClassLoaderImpl().classSigners.get(getName());
			return (Object[]) signers.clone();
		}
		catch(NullPointerException e)
		{
		}
		try
		{
			return (Object[]) domain.getCodeSource().getCertificates().clone();
		}
		catch(NullPointerException e)
		{
		}
		return null;
	}

	/**
	 * Returns the {@code Class} object which represents the superclass of the
	 * class represented by this {@code Class}. If this {@code Class} represents
	 * the {@code Object} class, a primitive type, an interface or void then the
	 * method returns {@code null}. If this {@code Class} represents an array
	 * class then the {@code Object} class is returned.
	 *
	 * @return the superclass of the class represented by this {@code Class}.
	 */
	public Class<? super T> getSuperclass()
	{
		return VMClassRegistry.getSuperclass(this);
	}

	/**
	 * Returns an array containing {@code TypeVariable} objects for type
	 * variables declared by the generic class represented by this {@code
	 * Class}. Returns an empty array if the class is not generic.
	 *
	 * @return an array with the type variables of the class represented by this
	 *         class.
	 * @since 1.5
	 */
	@SuppressWarnings("unchecked")
	public TypeVariable<Class<T>>[] getTypeParameters() throws GenericSignatureFormatError
	{
		return (TypeVariable<Class<T>>[]) getCache().getTypeParameters().clone();
	}

	/**
	 * Indicates whether this {@code Class} represents an annotation class.
	 *
	 * @return {@code true} if this {@code Class} represents an annotation
	 *         class; {@code false} otherwise.
	 */
	public boolean isAnnotation()
	{
		return (getModifiers() & ACC_ANNOTATION) != 0;
	}

	/**
	 * Indicates whether the specified annotation is present for the class
	 * represented by this {@code Class}.
	 *
	 * @param annotationClass the annotation to look for.
	 * @return {@code true} if the class represented by this {@code Class} is
	 *         annotated with {@code annotationClass}; {@code false} otherwise.
	 * @since 1.5
	 */
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass)
	{
		if(annotationClass == null)
		{
			throw new NullPointerException();
		}
		for(Annotation aa : getCache().getAllAnnotations())
		{
			if(annotationClass.equals(aa.annotationType()))
				return true;
		}
		return false;
	}


	/**
	 * Indicates whether the class represented by this {@code Class} is
	 * anonymously declared.
	 *
	 * @return {@code true} if the class represented by this {@code Class} is
	 *         anonymous; {@code false} otherwise.
	 * @since 1.5
	 */
	public boolean isAnonymousClass()
	{
		return getSimpleName().length() == 0;
	}

	/**
	 * Indicates whether the class represented by this {@code Class} is an array
	 * class.
	 *
	 * @return {@code true} if the class represented by this {@code Class} is an
	 *         array class; {@code false} otherwise.
	 */
	public boolean isArray()
	{
		return getReflectionData().isArray;
	}


	/**
	 * Indicates whether the specified class type can be converted to the class
	 * represented by this {@code Class}. Conversion may be done via an identity
	 * conversion or a widening reference conversion (if either the receiver or
	 * the argument represent primitive types, only the identity conversion
	 * applies).
	 *
	 * @param cls the class to check.
	 * @return {@code true} if {@code cls} can be converted to the class
	 *         represented by this {@code Class}; {@code false} otherwise.
	 * @throws NullPointerException if {@code cls} is {@code null}.
	 */
	public boolean isAssignableFrom(Class<?> clazz)
	{

		if(SERIALIZABLE_CLASS.equals(this))
		{
			return clazz.getReflectionData().isSerializable();
		}

		if(EXTERNALIZABLE_CLASS.equals(this))
		{
			return clazz.getReflectionData().isExternalizable();
		}

		return VMClassRegistry.isAssignableFrom(this, clazz);
	}


	/**
	 * Indicates whether the class represented by this {@code Class} is an
	 * {@code enum}.
	 *
	 * @return {@code true} if the class represented by this {@code Class} is an
	 *         {@code enum}; {@code false} otherwise.
	 */
	public boolean isEnum()
	{
		// check for superclass is needed for compatibility
		// otherwise there are false positives on anonymous element classes
		return ((getModifiers() & ACC_ENUM) != 0 && getSuperclass() == ENUM_CLASS);
	}

	/**
	 * Indicates whether the specified object can be cast to the class
	 * represented by this {@code Class}. This is the runtime version of the
	 * {@code instanceof} operator.
	 *
	 * @param object the object to check.
	 * @return {@code true} if {@code object} can be cast to the type
	 *         represented by this {@code Class}; {@code false} if {@code
	 *         object} is {@code null} or cannot be cast.
	 */
	public boolean isInstance(Object obj)
	{
		return VMClassRegistry.isInstance(this, obj);
	}


	/**
	 * Indicates whether this {@code Class} represents an interface.
	 *
	 * @return {@code true} if this {@code Class} represents an interface;
	 *         {@code false} otherwise.
	 */
	public boolean isInterface()
	{
		return (getModifiers() & ACC_INTERFACE) != 0;
	}

	/**
	 * Indicates whether the class represented by this {@code Class} is defined
	 * locally.
	 *
	 * @return {@code true} if the class represented by this {@code Class} is
	 *         defined locally; {@code false} otherwise.
	 */
	public boolean isLocalClass()
	{
		return false;
	}

	/**
	 * Indicates whether the class represented by this {@code Class} is a member
	 * class.
	 *
	 * @return {@code true} if the class represented by this {@code Class} is a
	 *         member class; {@code false} otherwise.
	 */
	public boolean isMemberClass()
	{
		return false;
	}

	/**
	 * Indicates whether this {@code Class} represents a primitive type.
	 *
	 * @return {@code true} if this {@code Class} represents a primitive type;
	 *         {@code false} otherwise.
	 */
	public boolean isPrimitive()
	{
		return getReflectionData().isPrimitive;
	}

	/**
	 * Indicates whether this {@code Class} represents a synthetic type.
	 *
	 * @return {@code true} if this {@code Class} represents a synthetic type;
	 *         {@code false} otherwise.
	 */
	public boolean isSynthetic()
	{
		return (getModifiers() & ACC_SYNTHETIC) != 0;
	}

	/**
	 * Returns a new instance of the class represented by this {@code Class},
	 * created by invoking the default (that is, zero-argument) constructor. If
	 * there is no such constructor, or if the creation fails (either because of
	 * a lack of available memory or because an exception is thrown by the
	 * constructor), an {@code InstantiationException} is thrown. If the default
	 * constructor exists but is not accessible from the context where this
	 * method is invoked, an {@code IllegalAccessException} is thrown.
	 *
	 * @return a new instance of the class represented by this {@code Class}.
	 * @throws IllegalAccessException if the default constructor is not visible.
	 * @throws InstantiationException if the instance can not be created.
	 * @throws SecurityException      if a security manager exists and it does not allow creating
	 *                                new instances.
	 */
	public T newInstance() throws InstantiationException, IllegalAccessException
	{
		T newInstance = null;
		final ReflectionData localReflectionData = getReflectionData();
		SecurityManager sc = System.getSecurityManager();
		if(sc != null)
		{
			sc.checkMemberAccess(this, Member.PUBLIC);
			sc.checkPackageAccess(localReflectionData.packageName);
		}

        /*
		 * HARMONY-1930: The synchronization issue is possible here.
         *
         * The issues is caused by fact that:
         * - first thread starts defaultConstructor initialization, including
         *   setting "isAccessible" flag to "true" for Constrcutor object
         * - another thread bypasses initialization and calls "newInstance"
         *   for defaultConstructor (while isAccessible is "false" yet)
         * - so, for this "another" thread the Constructor.newInstance checks
         *   the access rights by mistake and IllegalAccessException happens
         */
		while(!isDefaultConstructorInitialized)
		{
			synchronized(localReflectionData)
			{
				if(isDefaultConstructorInitialized)
				{
					break; // non-first threads can be here - nothing to do
				}

				// only first thread can reach this point & do initialization
				final Constructor<T> c;
				try
				{
					c = localReflectionData.getDefaultConstructor();
				}
				catch(NoSuchMethodException e)
				{
					throw new InstantiationException(e.getMessage() + " method not found");
				}
				try
				{
					AccessController.doPrivileged(new PrivilegedAction<Object>()
					{
						public Object run()
						{
							c.setAccessible(true);
							return null;
						}
					});
				}
				catch(SecurityException e)
				{
					// can't change accessibility of the default constructor
					IllegalAccessException ex = new IllegalAccessException();
					ex.initCause(e);
					throw ex;
				}

				// default constructor is initialized, access flag is set
				isDefaultConstructorInitialized = true;
				break;
			}
		}

		// initialization is done, threads may work from here in any order
		final Constructor<T> defaultConstructor;
		try
		{
			defaultConstructor = localReflectionData.getDefaultConstructor();
		}
		catch(NoSuchMethodException e)
		{
			throw new AssertionError(e);
		}
		Reflection.checkMemberAccess(VMStack.getCallerClass(0), defaultConstructor.getDeclaringClass(), defaultConstructor.getDeclaringClass(), defaultConstructor.getModifiers());

		try
		{
			newInstance = defaultConstructor.newInstance();
		}
		catch(InvocationTargetException e)
		{
			System.rethrow(e.getCause());
		}
		return newInstance;
	}

	@Override
	public String toString()
	{
		return isPrimitive() ? getName() : (isInterface() ? "interface " : "class ") + getName();
	}


	/**
	 * Returns the {@code Package} of which the class represented by this
	 * {@code Class} is a member. Returns {@code null} if no {@code Package}
	 * object was created by the class loader of the class.
	 *
	 * @return Package the {@code Package} of which this {@code Class} is a
	 *         member or {@code null}.
	 */
	public Package getPackage()
	{
		ClassLoader classLoader = getClassLoaderImpl();
		return classLoader == null ? ClassLoader.BootstrapLoader.getPackage(getPackageName()) : classLoader.getPackage(getPackageName());
	}


	/**
	 * Returns the assertion status for the class represented by this {@code
	 * Class}. Assertion is enabled / disabled based on the class loader,
	 * package or class default at runtime.
	 *
	 * @return the assertion status for the class represented by this {@code
	 *         Class}.
	 */
	public boolean desiredAssertionStatus()
	{
		if(disableAssertions)
		{
			return false;
		}

		ClassLoader loader = getClassLoaderImpl();
		if(loader == null)
		{
			// system class, status is controlled via cmdline only
			return VMExecutionEngine.getAssertionStatus(this, true, 0) > 0;
		}

		// First check exact class name
		String name = null;
		Map<String, Boolean> m = loader.classAssertionStatus;
		if(m != null && m.size() != 0)
		{
			name = getTopLevelClassName();
			Boolean status = m.get(name);
			if(status != null)
			{
				return status.booleanValue();
			}
		}
		if(!loader.clearAssertionStatus)
		{
			int systemStatus = VMExecutionEngine.getAssertionStatus(this, false, 0);
			if(systemStatus != 0)
			{
				return systemStatus > 0;
			}
		}

		// Next try (super)packages name(s) recursively
		m = loader.packageAssertionStatus;
		if(m != null && m.size() != 0)
		{
			if(name == null)
			{
				name = getName();
			}
			name = getParentName(name);
			// if this class is in the default package,
			// it is checked in the 1st iteration
			do
			{
				Boolean status = m.get(name);
				if(status != null)
				{
					return status.booleanValue();
				}
			}
			while((name = getParentName(name)).length() > 0);
		}
		if(!loader.clearAssertionStatus)
		{
			int systemStatus = VMExecutionEngine.getAssertionStatus(this, true, loader.defaultAssertionStatus);
			if(systemStatus != 0)
			{
				return systemStatus > 0;
			}
		}

		// Finally check the default status
		return loader.defaultAssertionStatus > 0;
	}

	/**
	 * Casts this {@code Class} to represent a subclass of the specified class.
	 * If successful, this {@code Class} is returned; otherwise a {@code
	 * ClassCastException} is thrown.
	 *
	 * @param clazz the required type.
	 * @return this {@code Class} cast as a subclass of the given type.
	 * @throws ClassCastException if this {@code Class} cannot be cast to the specified type.
	 */
	@SuppressWarnings("unchecked")
	public <U> Class<? extends U> asSubclass(Class<U> clazz) throws ClassCastException
	{
		if(!VMClassRegistry.isAssignableFrom(clazz, this))
		{
			throw new ClassCastException(toString());
		}

		return (Class<? extends U>) this;
	}

	/**
	 * Casts the specified object to the type represented by this {@code Class}.
	 * If the object is {@code null} then the result is also {@code null}.
	 *
	 * @param obj the object to cast.
	 * @return the object that has been cast.
	 * @throws ClassCastException if the object cannot be cast to the specified type.
	 */
	@SuppressWarnings("unchecked")
	public T cast(Object obj) throws ClassCastException
	{
		if(obj != null && !VMClassRegistry.isInstance(this, obj))
		{
			throw new ClassCastException(obj.getClass().toString());
		}
		return (T) obj;
	}

	// Not public api

	private void checkMemberAccess(int accessType)
	{
		SecurityManager sc = System.getSecurityManager();
		if(sc != null)
		{
			sc.checkMemberAccess(this, accessType);
			sc.checkPackageAccess(getReflectionData().packageName);
		}
	}

	/**
	 * Answers whether the arrays are equal
	 */
	static boolean isTypeMatches(Class<?>[] t1, Class<?>[] t2)
	{
		if(t1 == null)
			return t2 == null || t2.length == 0;
		if(t2 == null)
			return t1 == null || t1.length == 0;
		if(t1.length != t2.length)
			return false;
		for(int i = 0; i < t2.length; i++)
			if(t1[i] != t2[i])
			{
				return false;
			}
		return true;
	}

	private static Method findMatchingMethod(Method[] methods, String methodName, Class<?>[] argumentTypes) throws NoSuchMethodException
	{
		Method matcher = null;
		for(int i = 0; i < methods.length; i++)
		{
			Method m = methods[i];
			if(matcher != null && matcher.getDeclaringClass() != m.getDeclaringClass())
			{
				return matcher;
			}
			try
			{
				if(methodName.equals(m.getName()) && isTypeMatches(argumentTypes, m.getParameterTypes()) && (matcher == null || matcher.getReturnType().isAssignableFrom(m.getReturnType())))
				{
					matcher = m;
				}
			}
			catch(LinkageError ignore)
			{
			}
		}

		if(matcher == null)
			throw new NoSuchMethodException(methodName.toString() + printMethodSignature(argumentTypes));
		return matcher;
	}

	private static String getParentName(String name)
	{
		int dotPosition = name.lastIndexOf('.');
		return dotPosition == -1 ? "" : name.substring(0, dotPosition);
	}

	private static String printMethodSignature(Class<?>[] types)
	{
		StringBuffer sb = new StringBuffer("(");
		if(types != null && types.length > 0)
		{
			sb.append(types[0] != null ? types[0].getName() : "null");
			for(int i = 1; i < types.length; i++)
			{
				sb.append(", ");
				sb.append(types[i] != null ? types[i].getName() : "null");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Accessor for the reflection data field, which needs to have
	 * minimal thread-safety for consistency; this method encapsulates
	 * that.
	 */
	private ReflectionData getReflectionData()
	{
		// read the volatile field once
		final ReflectionData localData = _reflectionData;
		if(localData == null)
		{
			// if null, construct, write to the field and return
			return _reflectionData = new ReflectionData();
		}
		// else, just return the field
		return localData;
	}

	private GACache getCache()
	{
		GACache cache = null;
		if(softCache != null)
			cache = softCache.get();
		if(cache == null)
			softCache = new SoftReference<GACache>(cache = new GACache());
		return cache;
	}

	String getPackageName()
	{
		return getReflectionData().packageName;
	}

	private Constructor<T> getDeclaredConstructorInternal(Class<?>[] argumentTypes) throws NoSuchMethodException
	{
		final Constructor<T>[] declaredConstructors = getReflectionData().getDeclaredConstructors();
		for(int i = 0; i < declaredConstructors.length; i++)
		{
			Constructor<T> c = declaredConstructors[i];
			if(isTypeMatches(argumentTypes, c.getParameterTypes()))
				return c;
		}
		throw new NoSuchMethodException(getName() + printMethodSignature(argumentTypes));
	}

	private String getAbsoluteResource(String resource)
	{
		if(resource.startsWith("/"))
			return resource.substring(1);
		String pkgName = getPackageName();
		if(pkgName.length() > 0)
			resource = pkgName.replace('.', '/') + '/' + resource;
		return resource;
	}

	private String getTopLevelClassName()
	{
		Class<?> declaringClass = getDeclaringClass();
		return declaringClass == null ? getName() : declaringClass.getTopLevelClassName();
	}

}
