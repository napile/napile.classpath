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

package java.lang.reflect;

import static org.apache.harmony.vm.ClassFormat.ACC_SYNTHETIC;
import static org.apache.harmony.vm.ClassFormat.ACC_VARARGS;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.apache.harmony.lang.reflect.parser.Parser;
import org.apache.harmony.vm.VMGenericsAndAnnotations;
import org.apache.harmony.vm.VMStack;

/**
 * This class represents a constructor. Information about the constructor can be
 * accessed, and the constructor can be invoked dynamically.
 *
 * @param <T> the class that declares this constructor
 * @author Evgueni Brevnov, Serguei S. Zapreyev, Alexey V. Varlamov
 */
public final class Constructor<T> extends AccessibleObject implements GenericDeclaration, Member
{
	/**
	 * Keeps an information about this constructor
	 */
	private class ConstructorData
	{

		/**
		 * constructor handle which is used to retrieve all necessary
		 * information about this constructor object
		 */
		final long vm_member_id;

		Annotation[] declaredAnnotations;

		final Class<T> declaringClass;

		Class<?>[] exceptionTypes;

		Type[] genericExceptionTypes;

		Type[] genericParameterTypes;

		final int modifiers;

		String name;

		Annotation[][] parameterAnnotations;

		Class<?>[] parameterTypes;

		TypeVariable<Constructor<T>>[] typeParameters;
		final String descriptor;

		public ConstructorData(long vm_id, Class<T> clss, String name, String desc, int mods)
		{
			vm_member_id = vm_id;
			declaringClass = clss;
			this.name = null;
			modifiers = mods;
			descriptor = desc;
		}

		String getName()
		{
			if(name == null)
			{
				name = declaringClass.getName();
			}
			return name;
		}

		public Annotation[] getDeclaredAnnotations()
		{
			if(declaredAnnotations == null)
			{
				declaredAnnotations = VMGenericsAndAnnotations.getDeclaredAnnotations(vm_member_id);
			}
			return declaredAnnotations;
		}

		/**
		 * initializes exeptions
		 */
		public Class[] getExceptionTypes()
		{
			if(exceptionTypes == null)
			{
				exceptionTypes = VMReflection.getExceptionTypes(vm_member_id);
			}
			return exceptionTypes;
		}

		public Annotation[][] getParameterAnnotations()
		{
			if(parameterAnnotations == null)
			{
				parameterAnnotations = VMGenericsAndAnnotations.getParameterAnnotations(vm_member_id);
			}
			return parameterAnnotations;
		}

		/**
		 * initializes parameters
		 */
		public Class[] getParameterTypes()
		{
			if(parameterTypes == null)
			{
				parameterTypes = VMReflection.getParameterTypes(vm_member_id);
			}
			return parameterTypes;
		}
	}

	/**
	 * cache of the constructor data
	 */
	private final ConstructorData data;

	/**
	 * Copy constructor
	 *
	 * @param c original constructor
	 */
	Constructor(Constructor<T> c)
	{
		data = c.data;
		isAccessible = c.isAccessible;
	}

	/**
	 * Only VM should call this constructor.
	 * String parameters must be interned.
	 *
	 * @api2vm
	 */
	Constructor(long id, Class<T> clss, String name, String desc, int m)
	{
		data = new ConstructorData(id, clss, name, desc, m);
	}

	@SuppressWarnings("unchecked")
	public TypeVariable<Constructor<T>>[] getTypeParameters() throws GenericSignatureFormatError
	{
		if(data.typeParameters == null)
		{
			data.typeParameters = (TypeVariable<Constructor<T>>[]) Parser.getTypeParameters(this, VMGenericsAndAnnotations.getSignature(data.vm_member_id));
		}
		return (TypeVariable<Constructor<T>>[]) data.typeParameters.clone();
	}

	/**
	 * Returns the string representation of the constructor's declaration,
	 * including the type parameters.
	 *
	 * @return the string representation of the constructor's declaration
	 * @since 1.5
	 */
	public String toGenericString()
	{
		StringBuilder sb = new StringBuilder(80);
		// data initialization
		if(data.genericParameterTypes == null)
		{
			data.genericParameterTypes = Parser.getGenericParameterTypes(this, VMGenericsAndAnnotations.getSignature(data.vm_member_id));
		}
		if(data.genericExceptionTypes == null)
		{
			data.genericExceptionTypes = Parser.getGenericExceptionTypes(this, VMGenericsAndAnnotations.getSignature(data.vm_member_id));
		}
		// append modifiers if any
		int modifier = getModifiers();
		if(modifier != 0)
		{
			sb.append(Modifier.toString(modifier & ~ACC_VARARGS)).append(' ');
		}
		// append type parameters
		if(data.typeParameters != null && data.typeParameters.length > 0)
		{
			sb.append('<');
			for(int i = 0; i < data.typeParameters.length; i++)
			{
				appendGenericType(sb, data.typeParameters[i]);
				if(i < data.typeParameters.length - 1)
				{
					sb.append(", ");
				}
			}
			sb.append("> ");
		}
		// append constructor name
		appendArrayType(sb, getDeclaringClass());
		// append parameters
		sb.append('(');
		appendArrayGenericType(sb, data.genericParameterTypes);
		sb.append(')');
		// append exeptions if any
		if(data.genericExceptionTypes.length > 0)
		{
			sb.append(" throws ");
			appendArrayGenericType(sb, data.genericExceptionTypes);
		}
		return sb.toString();
	}

	/**
	 * Returns the generic parameter types as an array of {@code Type}
	 * instances, in declaration order. If this constructor has no generic
	 * parameters, an empty array is returned.
	 *
	 * @return the parameter types
	 * @throws GenericSignatureFormatError if the generic constructor signature is invalid
	 * @throws TypeNotPresentException     if any parameter type points to a missing type
	 * @throws MalformedParameterizedTypeException
	 *                                     if any parameter type points to a type that cannot be
	 *                                     instantiated for some reason
	 * @since 1.5
	 */
	public Type[] getGenericParameterTypes() throws GenericSignatureFormatError, TypeNotPresentException, MalformedParameterizedTypeException
	{
		if(data.genericParameterTypes == null)
		{
			data.genericParameterTypes = Parser.getGenericParameterTypes(this, VMGenericsAndAnnotations.getSignature(data.vm_member_id));
		}

		return (Type[]) data.genericParameterTypes.clone();
	}


	/**
	 * Returns the exception types as an array of {@code Type} instances. If
	 * this constructor has no declared exceptions, an empty array will be
	 * returned.
	 *
	 * @return an array of generic exception types
	 * @throws GenericSignatureFormatError if the generic constructor signature is invalid
	 * @throws TypeNotPresentException     if any exception type points to a missing type
	 * @throws MalformedParameterizedTypeException
	 *                                     if any exception type points to a type that cannot be
	 *                                     instantiated for some reason
	 * @since 1.5
	 */
	public Type[] getGenericExceptionTypes() throws GenericSignatureFormatError, TypeNotPresentException, MalformedParameterizedTypeException
	{
		if(data.genericExceptionTypes == null)
			data.genericExceptionTypes = Parser.getGenericExceptionTypes(this, VMGenericsAndAnnotations.getSignature(data.vm_member_id));

		return (Type[]) data.genericExceptionTypes.clone();
	}

	/**
	 * Returns an array of arrays that represent the annotations of the formal
	 * parameters of this constructor. If there are no parameters on this
	 * constructor, then an empty array is returned. If there are no annotations
	 * set, then an array of empty arrays is returned.
	 *
	 * @return an array of arrays of {@code Annotation} instances
	 * @since 1.5
	 */
	public Annotation[][] getParameterAnnotations()
	{
		Annotation a[][] = data.getParameterAnnotations();
		Annotation aa[][] = new Annotation[a.length][];
		for(int i = 0; i < a.length; i++)
		{
			aa[i] = new Annotation[a[i].length];
			System.arraycopy(a[i], 0, aa[i], 0, a[i].length);
		}
		return aa;
	}

	/**
	 * Indicates whether or not this constructor takes a variable number of
	 * arguments.
	 *
	 * @return {@code true} if a vararg is declare, otherwise
	 *         {@code false}
	 * @since 1.5
	 */
	public boolean isVarArgs()
	{
		return (getModifiers() & ACC_VARARGS) != 0;
	}


	/**
	 * Indicates whether or not this constructor is synthetic (artificially
	 * introduced by the compiler).
	 *
	 * @return {@code true} if this constructor is synthetic, {@code false}
	 *         otherwise
	 */
	@Override
	public boolean isSynthetic()
	{
		return (getModifiers() & ACC_SYNTHETIC) != 0;
	}

	/**
	 * Indicates whether or not the specified {@code object} is equal to this
	 * constructor. To be equal, the specified object must be an instance
	 * of {@code Constructor} with the same declaring class and parameter types
	 * as this constructor.
	 *
	 * @param obj the object to compare
	 * @return {@code true} if the specified object is equal to this
	 *         constructor, {@code false} otherwise
	 * @see #hashCode
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Constructor)
		{
			Constructor another = (Constructor) obj;
			if(data.vm_member_id == another.data.vm_member_id)
			{
				assert getDeclaringClass() == another.getDeclaringClass() && Arrays.equals(getParameterTypes(), another.getParameterTypes());
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the class that declares this constructor.
	 *
	 * @return the declaring class
	 */
	public Class<T> getDeclaringClass()
	{
		return null;
	}

	/**
	 * Returns the exception types as an array of {@code Class} instances. If
	 * this constructor has no declared exceptions, an empty array will be
	 * returned.
	 *
	 * @return the declared exception classes
	 */
	public Class<?>[] getExceptionTypes()
	{
		return (Class[]) data.getExceptionTypes().clone();
	}

	/**
	 * Returns the modifiers for this constructor. The {@link Modifier} class
	 * should be used to decode the result.
	 *
	 * @return the modifiers for this constructor
	 * @see Modifier
	 */
	public int getModifiers()
	{
		return data.modifiers;
	}

	/**
	 * Returns the name of this constructor.
	 *
	 * @return the name of this constructor
	 */
	public String getName()
	{
		return data.getName();
	}

	/**
	 * Returns an array of the {@code Class} objects associated with the
	 * parameter types of this constructor. If the constructor was declared with
	 * no parameters, an empty array will be returned.
	 *
	 * @return the parameter types
	 */
	public Class<?>[] getParameterTypes()
	{
		return (Class[]) data.getParameterTypes().clone();
	}

	/**
	 * Returns an integer hash code for this constructor. Constructors which are
	 * equal return the same value for this method. The hash code for a
	 * Constructor is the hash code of the name of the declaring class.
	 *
	 * @return the hash code
	 * @see #equals
	 */
	@Override
	public int hashCode()
	{
		return getDeclaringClass().getName().hashCode();
	}

	/**
	 * Returns a new instance of the declaring class, initialized by dynamically
	 * invoking the constructor represented by this {@code Constructor} object.
	 * This reproduces the effect of {@code new declaringClass(arg1, arg2, ... ,
	 * argN)} This method performs the following:
	 * <ul>
	 * <li>A new instance of the declaring class is created. If the declaring
	 * class cannot be instantiated (i.e. abstract class, an interface, an array
	 * type, or a primitive type) then an InstantiationException is thrown.</li>
	 * <li>If this Constructor object is enforcing access control (see
	 * {@link AccessibleObject}) and this constructor is not accessible from the
	 * current context, an IllegalAccessException is thrown.</li>
	 * <li>If the number of arguments passed and the number of parameters do not
	 * match, an IllegalArgumentException is thrown.</li>
	 * <li>For each argument passed:
	 * <ul>
	 * <li>If the corresponding parameter type is a primitive type, the argument
	 * is unwrapped. If the unwrapping fails, an IllegalArgumentException is
	 * thrown.</li>
	 * <li>If the resulting argument cannot be converted to the parameter type
	 * via a widening conversion, an IllegalArgumentException is thrown.</li>
	 * </ul>
	 * <li>The constructor represented by this {@code Constructor} object is
	 * then invoked. If an exception is thrown during the invocation, it is
	 * caught and wrapped in an InvocationTargetException. This exception is
	 * then thrown. If the invocation completes normally, the newly initialized
	 * object is returned.
	 * </ul>
	 *
	 * @param args the arguments to the constructor
	 * @return the new, initialized, object
	 * @throws InstantiationException    if the class cannot be instantiated
	 * @throws IllegalAccessException    if this constructor is not accessible
	 * @throws IllegalArgumentException  if an incorrect number of arguments are passed, or an
	 *                                   argument could not be converted by a widening conversion
	 * @throws InvocationTargetException if an exception was thrown by the invoked constructor
	 * @see AccessibleObject
	 */
	@SuppressWarnings("unchecked")
	public T newInstance(Object... args) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if(Modifier.isAbstract(getDeclaringClass().getModifiers()))
		{
			throw new InstantiationException("Can not instantiate abstract " + getDeclaringClass());
		}

		// check parameter validity
		checkInvokationArguments(data.getParameterTypes(), args);

		if(!isAccessible)
		{
			reflectExporter.checkMemberAccess(VMStack.getCallerClass(0), getDeclaringClass(), getDeclaringClass(), getModifiers());
		}
		return (T) VMReflection.newClassInstance(data.vm_member_id, args);
	}

	/**
	 * Returns a string containing a concise, human-readable description of this
	 * constructor. The format of the string is:
	 * <p/>
	 * <ol>
	 * <li>modifiers (if any)
	 * <li>declaring class name
	 * <li>'('
	 * <li>parameter types, separated by ',' (if any)
	 * <li>')'
	 * <li>'throws' plus exception types, separated by ',' (if any)
	 * </ol>
	 * <p/>
	 * For example:
	 * {@code public String(byte[],String) throws UnsupportedEncodingException}
	 *
	 * @return a printable representation for this constructor
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(80);
		// append modifiers if any
		int modifier = getModifiers();
		if(modifier != 0)
		{
			// VARARGS incorrectly recognized
			final int MASK = ~ACC_VARARGS;
			sb.append(Modifier.toString(modifier & MASK)).append(' ');
		}
		// append constructor name
		appendArrayType(sb, getDeclaringClass());
		// append parameters
		sb.append('(');
		appendArrayType(sb, data.getParameterTypes());
		sb.append(')');
		// append exeptions if any
		Class[] exn = data.getExceptionTypes();
		if(exn.length > 0)
		{
			sb.append(" throws ");
			appendSimpleType(sb, exn);
		}
		return sb.toString();
	}

	/**
	 * This method is used by serialization mechanism.
	 *
	 * @return the signature of the constructor
	 */
	String getSignature()
	{
		return data.descriptor;
	}

	@Override
	public Annotation[] getAnnotations()
	{
		return getDeclaredAnnotations();
	}

	@Override
	public Annotation[] getDeclaredAnnotations()
	{
		return data.getDeclaredAnnotations();
	}

	long getId()
	{
		return data.vm_member_id;
	}
}
