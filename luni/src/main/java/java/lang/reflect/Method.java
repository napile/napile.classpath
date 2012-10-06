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

import static org.apache.harmony.vm.ClassFormat.ACC_BRIDGE;
import static org.apache.harmony.vm.ClassFormat.ACC_VARARGS;
import static org.apache.harmony.vm.ClassFormat.ACC_SYNTHETIC;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.apache.harmony.lang.reflect.parser.Parser;
import org.apache.harmony.vm.VMGenericsAndAnnotations;
import org.apache.harmony.vm.VMStack;

/**
 * This class represents a method. Information about the method can be accessed,
 * and the method can be invoked dynamically.
 *
 * @author Evgueni Brevnov, Serguei S. Zapreyev, Alexey V. Varlamov
 */
public final class Method extends AccessibleObject implements GenericDeclaration, Member
{

	/**
	 * Keeps an information about this method
	 */
	private class MethodData
	{

		/**
		 * method handle which is used to retrieve all necessary information
		 * about this method object
		 */
		final long vm_member_id;

		Annotation[] declaredAnnotations;

		final Class<?> declaringClass;

		private Class<?>[] exceptionTypes;

		Type[] genericExceptionTypes;

		Type[] genericParameterTypes;

		Type genericReturnType;

		String methSignature;

		final int modifiers;

		final String name;

		final String descriptor;

		/**
		 * declared method annotations
		 */
		Annotation[][] parameterAnnotations;

		/**
		 * method parameters
		 */
		Class<?>[] parameterTypes;

		/**
		 * method return type
		 */
		private Class<?> returnType;

		/**
		 * method type parameters
		 */
		TypeVariable<Method>[] typeParameters;

		public MethodData(long vm_id, Class clss, String name, String desc, int mods)
		{
			vm_member_id = vm_id;
			declaringClass = clss;
			this.name = name;
			modifiers = mods;
			descriptor = desc;
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
		public Class<?>[] getExceptionTypes()
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

		/**
		 * initializes return type
		 */
		public Class<?> getReturnType()
		{
			if(returnType == null)
			{
				returnType = VMReflection.getMethodReturnType(vm_member_id);
			}
			return returnType;
		}
	}

	/**
	 * cache of the method data
	 */
	private final MethodData data;

	/**
	 * Copy constructor
	 *
	 * @param m original method
	 */
	Method(Method m)
	{
		data = m.data;
		isAccessible = m.isAccessible;
	}

	/**
	 * Only VM should call this constructor.
	 * String parameters must be interned.
	 *
	 * @api2vm
	 */
	Method(long id, Class clss, String name, String desc, int m)
	{
		data = new MethodData(id, clss, name, desc, m);
	}

	@SuppressWarnings("unchecked")
	public TypeVariable<Method>[] getTypeParameters() throws GenericSignatureFormatError
	{
		if(data.typeParameters == null)
		{
			data.typeParameters = Parser.getTypeParameters(this, VMGenericsAndAnnotations.getSignature(data.vm_member_id));
		}
		return (TypeVariable<Method>[]) data.typeParameters.clone();
	}

	/**
	 * Returns the string representation of the method's declaration, including
	 * the type parameters.
	 *
	 * @return the string representation of this method
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
			sb.append(Modifier.toString(modifier & ~(ACC_BRIDGE + ACC_VARARGS))).append(' ');
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
		// append return type
		appendGenericType(sb, getGenericReturnType());
		sb.append(' ');
		// append method name
		appendArrayType(sb, getDeclaringClass());
		sb.append("." + getName());
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
	 * Returns the parameter types as an array of {@code Type} instances, in
	 * declaration order. If this method has no parameters, an empty array is
	 * returned.
	 *
	 * @return the parameter types
	 * @throws GenericSignatureFormatError if the generic method signature is invalid
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
	 * this method has no declared exceptions, an empty array will be returned.
	 *
	 * @return an array of generic exception types
	 * @throws GenericSignatureFormatError if the generic method signature is invalid
	 * @throws TypeNotPresentException     if any exception type points to a missing type
	 * @throws MalformedParameterizedTypeException
	 *                                     if any exception type points to a type that cannot be
	 *                                     instantiated for some reason
	 * @since 1.5
	 */
	public Type[] getGenericExceptionTypes() throws GenericSignatureFormatError, TypeNotPresentException, MalformedParameterizedTypeException
	{
		if(data.genericExceptionTypes == null)
		{
			data.genericExceptionTypes = Parser.getGenericExceptionTypes(this, VMGenericsAndAnnotations.getSignature(data.vm_member_id));
		}
		return (Type[]) data.genericExceptionTypes.clone();
	}

	/**
	 * Returns the return type of this method as a {@code Type} instance.
	 *
	 * @return the return type of this method
	 * @throws GenericSignatureFormatError if the generic method signature is invalid
	 * @throws TypeNotPresentException     if the return type points to a missing type
	 * @throws MalformedParameterizedTypeException
	 *                                     if the return type points to a type that cannot be
	 *                                     instantiated for some reason
	 * @since 1.5
	 */
	public Type getGenericReturnType() throws GenericSignatureFormatError, TypeNotPresentException, MalformedParameterizedTypeException
	{
		if(data.genericReturnType == null)
		{
			data.genericReturnType = Parser.getGenericReturnTypeImpl(this, VMGenericsAndAnnotations.getSignature(data.vm_member_id));
		}
		return data.genericReturnType;
	}

	/**
	 * Returns an array of arrays that represent the annotations of the formal
	 * parameters of this method. If there are no parameters on this method,
	 * then an empty array is returned. If there are no annotations set, then
	 * and array of empty arrays is returned.
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

	@Override
	public Annotation[] getAnnotations()
	{
		return getDeclaredAnnotations();
	}

	@Override
	public Annotation[] getDeclaredAnnotations()
	{
		Annotation a[] = data.getDeclaredAnnotations();
		Annotation aa[] = new Annotation[a.length];
		System.arraycopy(a, 0, aa, 0, a.length);
		return aa;
	}

	/**
	 * Indicates whether or not this method takes a variable number argument.
	 *
	 * @return {@code true} if a vararg is declared, {@code false} otherwise
	 * @since 1.5
	 */
	public boolean isVarArgs()
	{
		return (getModifiers() & ACC_VARARGS) != 0;
	}

	/**
	 * Indicates whether or not this method is a bridge.
	 *
	 * @return {@code true} if this method is a bridge, {@code false} otherwise
	 * @since 1.5
	 */
	public boolean isBridge()
	{
		return (getModifiers() & ACC_BRIDGE) != 0;
	}

	/**
	 * Indicates whether or not this method is synthetic.
	 *
	 * @return {@code true} if this method is synthetic, {@code false} otherwise
	 */
	public boolean isSynthetic()
	{
		return (getModifiers() & ACC_SYNTHETIC) != 0;
	}

	/**
	 * Returns the default value for the annotation member represented by this
	 * method.
	 *
	 * @return the default value, or {@code null} if none
	 * @throws TypeNotPresentException if this annotation member is of type {@code Class} and no
	 *                                 definition can be found
	 * @since 1.5
	 */
	public Object getDefaultValue()
	{
		return VMGenericsAndAnnotations.getDefaultValue(data.vm_member_id);
	}

	/**
	 * Indicates whether or not the specified {@code object} is equal to this
	 * method. To be equal, the specified object must be an instance
	 * of {@code Method} with the same declaring class and parameter types
	 * as this method.
	 *
	 * @param obj the object to compare
	 * @return {@code true} if the specified object is equal to this
	 *         method, {@code false} otherwise
	 * @see #hashCode
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Method)
		{
			Method another = (Method) obj;
			if(data.vm_member_id == another.data.vm_member_id)
			{
				assert getDeclaringClass() == another.getDeclaringClass() && getName() == another.getName() && getReturnType() == another.getReturnType() && Arrays.equals(getParameterTypes(), another.getParameterTypes());
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the class that declares this method.
	 *
	 * @return the declaring class
	 */
	public Class<?> getDeclaringClass()
	{
		return data.declaringClass;
	}

	/**
	 * Returns the exception types as an array of {@code Class} instances. If
	 * this method has no declared exceptions, an empty array is returned.
	 *
	 * @return the declared exception classes
	 */
	public Class<?>[] getExceptionTypes()
	{
		return (Class[]) data.getExceptionTypes().clone();
	}

	/**
	 * Returns the modifiers for this method. The {@link Modifier} class should
	 * be used to decode the result.
	 *
	 * @return the modifiers for this method
	 * @see Modifier
	 */
	public int getModifiers()
	{
		return data.modifiers;
	}

	/**
	 * Returns the name of the method represented by this {@code Method}
	 * instance.
	 *
	 * @return the name of this method
	 */
	public String getName()
	{
		return data.name;
	}

	/**
	 * Returns an array of {@code Class} objects associated with the parameter
	 * types of this method. If the method was declared with no parameters, an
	 * empty array will be returned.
	 *
	 * @return the parameter types
	 */
	public Class<?>[] getParameterTypes()
	{
		return (Class[]) data.getParameterTypes().clone();
	}

	/**
	 * Returns the {@code Class} associated with the return type of this
	 * method.
	 *
	 * @return the return type
	 */
	public Class<?> getReturnType()
	{
		return data.getReturnType();
	}

	/**
	 * Returns an integer hash code for this method. Objects which are equal
	 * return the same value for this method. The hash code for this Method is
	 * the hash code of the name of this method.
	 *
	 * @return hash code for this method
	 * @see #equals
	 */
	@Override
	public int hashCode()
	{
		return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
	}

	/**
	 * Returns the result of dynamically invoking this method. This reproduces
	 * the effect of {@code receiver.methodName(arg1, arg2, ..., argN)} This
	 * method performs the following:
	 * <ul>
	 * <li>If this method is static, the receiver argument is ignored.</li>
	 * <li>Otherwise, if the receiver is null, a NullPointerException is thrown.
	 * </li>
	 * <li>If the receiver is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.</li>
	 * <li>If this Method object is enforcing access control (see
	 * AccessibleObject) and this method is not accessible from the current
	 * context, an IllegalAccessException is thrown.</li>
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
	 * <li>If this method is static, it is invoked directly. If it is
	 * non-static, this method and the receiver are then used to perform a
	 * standard dynamic method lookup. The resulting method is then invoked.</li>
	 * <li>If an exception is thrown during the invocation it is caught and
	 * wrapped in an InvocationTargetException. This exception is then thrown.</li>
	 * <li>If the invocation completes normally, the return value itself is
	 * returned. If the method is declared to return a primitive type, the
	 * return value is first wrapped. If the return type is void, null is
	 * returned.</li>
	 * </ul>
	 *
	 * @param obj the object on which to call this method
	 * @param args     the arguments to the method
	 * @return the new, initialized, object
	 * @throws NullPointerException      if the receiver is null for a non-static method
	 * @throws IllegalAccessException    if this method is not accessible
	 * @throws IllegalArgumentException  if an incorrect number of arguments are passed, the receiver
	 *                                   is incompatible with the declaring class, or an argument
	 *                                   could not be converted by a widening conversion
	 * @throws InvocationTargetException if an exception was thrown by the invoked method
	 * @see AccessibleObject
	 */
	public Object invoke(Object obj, Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{

		obj = checkObject(getDeclaringClass(), getModifiers(), obj);

		// check parameter validity
		checkInvokationArguments(data.getParameterTypes(), args);

		if(!isAccessible)
		{
			reflectExporter.checkMemberAccess(VMStack.getCallerClass(0), getDeclaringClass(), obj == null ? getDeclaringClass() : obj.getClass(), getModifiers());
		}
		return VMReflection.invokeMethod(data.vm_member_id, obj, args);
	}

	/**
	 * Returns a string containing a concise, human-readable description of this
	 * method. The format of the string is:
	 * <p/>
	 * <ol>
	 * <li>modifiers (if any)
	 * <li>return type or 'void'
	 * <li>declaring class name
	 * <li>'('
	 * <li>parameter types, separated by ',' (if any)
	 * <li>')'
	 * <li>'throws' plus exception types, separated by ',' (if any)
	 * </ol>
	 * <p/>
	 * For example: {@code public native Object
	 * java.lang.Method.invoke(Object,Object) throws
	 * IllegalAccessException,IllegalArgumentException
	 * ,InvocationTargetException}
	 *
	 * @return a printable representation for this method
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		// append modifiers if any
		int modifier = getModifiers();
		if(modifier != 0)
		{
			// BRIDGE & VARARGS recognized incorrectly
			final int MASK = ~(ACC_BRIDGE + ACC_VARARGS);
			sb.append(Modifier.toString(modifier & MASK)).append(' ');
		}
		// append return type
		appendArrayType(sb, getReturnType());
		sb.append(' ');
		// append full method name
		sb.append(getDeclaringClass().getName()).append('.').append(getName());
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

	    /* NON API SECTION */

	/**
	 * This method is required by serialization mechanism.
	 *
	 * @return the signature of the method
	 */
	String getSignature()
	{
		return data.descriptor;
	}

	long getId()
	{
		return data.vm_member_id;
	}
}
