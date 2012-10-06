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

import static org.apache.harmony.vm.ClassFormat.ACC_ENUM;
import static org.apache.harmony.vm.ClassFormat.ACC_SYNTHETIC;

import java.lang.annotation.Annotation;

import org.apache.harmony.lang.reflect.parser.Parser;
import org.apache.harmony.vm.VMGenericsAndAnnotations;
import org.apache.harmony.vm.VMStack;

/**
 * This class represents a field. Information about the field can be accessed,
 * and the field's value can be accessed dynamically.
 *
 * @author Evgueni Brevnov, Serguei S. Zapreyev, Alexey V. Varlamov
 */
public final class Field extends AccessibleObject implements Member
{
	/**
	 * Keeps an information about this field
	 */
	private static class FieldData
	{

		final String name;
		final Class declaringClass;
		final int modifiers;
		private Class<?> type;
		private Annotation[] declaredAnnotations;
		Type genericType;
		final String descriptor;

		/**
		 * field handle which is used to retrieve all necessary information
		 * about this field object
		 */
		final long vm_member_id;

		FieldData(long vm_id, Class clss, String name, String desc, int mods)
		{
			vm_member_id = vm_id;
			declaringClass = clss;
			this.name = name;
			modifiers = mods;
			descriptor = desc;
		}

		Annotation[] getAnnotations()
		{
			if(declaredAnnotations == null)
			{
				declaredAnnotations = VMGenericsAndAnnotations.getDeclaredAnnotations(vm_member_id);
			}
			return declaredAnnotations;
		}

		Class<?> getType()
		{
			if(type == null)
			{
				type = VMReflection.getFieldType(vm_member_id);
			}
			return type;
		}
	}

	/**
	 * cache of the field data
	 */
	private final FieldData data;

	/**
	 * Copy constructor
	 *
	 * @param f original field
	 */
	Field(Field f)
	{
		data = f.data;
		isAccessible = f.isAccessible;
	}

	/**
	 * Only VM should call this constructor.
	 * String parameters must be interned.
	 *
	 * @api2vm
	 */
	Field(long id, Class clss, String name, String desc, int m)
	{
		data = new FieldData(id, clss, name, desc, m);
	}

	/**
	 * Returns the field's signature in non-printable form. This is called
	 * (only) from IO native code and needed for deriving the serialVersionUID
	 * of the class
	 *
	 * @return the field's signature.
	 */
	String getSignature() {
		return data.descriptor;
	}

	/**
	 * Indicates whether or not this field is synthetic.
	 *
	 * @return {@code true} if this field is synthetic, {@code false} otherwise
	 */
	public boolean isSynthetic() {
		return (getModifiers() & ACC_SYNTHETIC) != 0;
	}
	/**
	 * Returns the string representation of this field, including the field's
	 * generic type.
	 *
	 * @return the string representation of this field
	 * @since 1.5
	 */
	public String toGenericString() {
		StringBuilder sb = new StringBuilder(80);
		// append modifiers if any
		int modifier = getModifiers();
		if (modifier != 0) {
			sb.append(Modifier.toString(modifier)).append(' ');
		}
		// append generic type
		appendGenericType(sb, getGenericType());
		sb.append(' ');
		// append full field name
		sb.append(getDeclaringClass().getName()).append('.').append(getName());
		return sb.toString();
	}
	/**
	 * Indicates whether or not this field is an enumeration constant.
	 *
	 * @return {@code true} if this field is an enumeration constant, {@code
	 *         false} otherwise
	 * @since 1.5
	 */
	public boolean isEnumConstant() {
		return (getModifiers() & ACC_ENUM) != 0;
	}

	/**
	 * Returns the generic type of this field.
	 *
	 * @return the generic type
	 * @throws GenericSignatureFormatError if the generic field signature is invalid
	 * @throws TypeNotPresentException     if the generic type points to a missing type
	 * @throws MalformedParameterizedTypeException
	 *                                     if the generic type points to a type that cannot be
	 *                                     instantiated for some reason
	 * @since 1.5
	 */
	public Type getGenericType() throws GenericSignatureFormatError,
			TypeNotPresentException, MalformedParameterizedTypeException {
		if (data.genericType == null) {
			data.genericType = Parser.parseFieldGenericType(this, VMGenericsAndAnnotations.getSignature(data.vm_member_id));
		}
		return data.genericType;
	}

	/**
	 * Indicates whether or not the specified {@code object} is equal to this
	 * field. To be equal, the specified object must be an instance of
	 * {@code Field} with the same declaring class, type and name as this field.
	 *
	 * @param obj the object to compare
	 * @return {@code true} if the specified object is equal to this method,
	 *         {@code false} otherwise
	 * @see #hashCode
	 */
	public boolean equals(Object obj)
	{
		if(obj instanceof Field)
		{
			Field that = (Field) obj;
			if(data.vm_member_id == that.data.vm_member_id)
			{
				assert getDeclaringClass() == that.getDeclaringClass() && getName() == that.getName();
				return true;
			}
		}
		return false;
	}


	/**
	 * Returns the value of the field in the specified object. This reproduces
	 * the effect of {@code object.fieldName}
	 * <p/>
	 * If the type of this field is a primitive type, the field value is
	 * automatically wrapped.
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is null, a NullPointerException is thrown. If
	 * the object is not an instance of the declaring class of the method, an
	 * IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 *
	 * @param obj the object to access
	 * @return the field value, possibly wrapped
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public Object get(Object obj) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkGet(VMStack.getCallerClass(0), obj);
		return VMField.getObject(obj, data.vm_member_id);
	}

	/**
	 * Returns the value of the field in the specified object as a {@code
	 * boolean}. This reproduces the effect of {@code object.fieldName}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 *
	 * @param obj the object to access
	 * @return the field value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public boolean getBoolean(Object obj) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkGet(VMStack.getCallerClass(0), obj);
		return VMField.getBoolean(obj, data.vm_member_id);
	}

	/**
	 * Returns the value of the field in the specified object as a {@code byte}.
	 * This reproduces the effect of {@code object.fieldName}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 *
	 * @param obj the object to access
	 * @return the field value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public byte getByte(Object obj) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkGet(VMStack.getCallerClass(0), obj);
		return VMField.getByte(obj, data.vm_member_id);
	}

	/**
	 * Returns the value of the field in the specified object as a {@code char}.
	 * This reproduces the effect of {@code object.fieldName}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 *
	 * @param obj the object to access
	 * @return the field value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public char getChar(Object obj) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkGet(VMStack.getCallerClass(0), obj);
		return VMField.getChar(obj, data.vm_member_id);
	}

	/**
	 * Returns the class that declares this field.
	 *
	 * @return the declaring class
	 */
	public Class<?> getDeclaringClass()
	{
		return data.declaringClass;
	}

	/**
	 * Returns the value of the field in the specified object as a {@code
	 * double}. This reproduces the effect of {@code object.fieldName}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 *
	 * @param obj the object to access
	 * @return the field value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public double getDouble(Object obj) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkGet(VMStack.getCallerClass(0), obj);
		return VMField.getDouble(obj, data.vm_member_id);
	}

	/**
	 * Returns the value of the field in the specified object as a {@code float}.
	 * This reproduces the effect of {@code object.fieldName}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 *
	 * @param obj the object to access
	 * @return the field value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public float getFloat(Object obj) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkGet(VMStack.getCallerClass(0), obj);
		return VMField.getFloat(obj, data.vm_member_id);
	}

	/**
	 * Returns the value of the field in the specified object as an {@code int}.
	 * This reproduces the effect of {@code object.fieldName}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 *
	 * @param obj the object to access
	 * @return the field value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public int getInt(Object obj) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkGet(VMStack.getCallerClass(0), obj);
		return VMField.getInt(obj, data.vm_member_id);
	}

	/**
	 * Returns the value of the field in the specified object as a {@code long}.
	 * This reproduces the effect of {@code object.fieldName}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 *
	 * @param obj the object to access
	 * @return the field value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public long getLong(Object obj) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkGet(VMStack.getCallerClass(0), obj);
		return VMField.getLong(obj, data.vm_member_id);
	}

	/**
	 * Returns the modifiers for this field. The {@link Modifier} class should
	 * be used to decode the result.
	 *
	 * @return the modifiers for this field
	 * @see Modifier
	 */
	@Override
	public int getModifiers()
	{
		return data.modifiers;
	}

	/**
	 * Returns the name of this field.
	 *
	 * @return the name of this field
	 */
	public String getName()
	{
		return data.name;
	}

	/**
	 * Returns the value of the field in the specified object as a {@code short}
	 * . This reproduces the effect of {@code object.fieldName}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 *
	 * @param obj the object to access
	 * @return the field value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public short getShort(Object obj) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkGet(VMStack.getCallerClass(0), obj);
		return VMField.getShort(obj, data.vm_member_id);
	}

	/**
	 * Return the {@link Class} associated with the type of this field.
	 *
	 * @return the type of this field
	 */
	public Class<?> getType()
	{
		return data.getType();
	}

	/**
	 * Returns an integer hash code for this field. Objects which are equal
	 * return the same value for this method.
	 * <p/>
	 * The hash code for a Field is the exclusive-or combination of the hash
	 * code of the field's name and the hash code of the name of its declaring
	 * class.
	 *
	 * @return the hash code for this field
	 * @see #equals
	 */
	@Override
	public int hashCode()
	{
		return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
	}

	/**
	 * Sets the value of the field in the specified object to the value. This
	 * reproduces the effect of {@code object.fieldName = value}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 * <p/>
	 * If the field type is a primitive type, the value is automatically
	 * unwrapped. If the unwrap fails, an IllegalArgumentException is thrown. If
	 * the value cannot be converted to the field type via a widening
	 * conversion, an IllegalArgumentException is thrown.
	 *
	 * @param obj the object to access
	 * @param value  the new value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public void set(Object obj, Object value) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkSet(VMStack.getCallerClass(0), obj);
		VMField.setObject(obj, data.vm_member_id, value);
	}

	/**
	 * Sets the value of the field in the specified object to the {@code
	 * boolean} value. This reproduces the effect of {@code object.fieldName =
	 * value}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 * <p/>
	 * If the value cannot be converted to the field type via a widening
	 * conversion, an IllegalArgumentException is thrown.
	 *
	 * @param obj the object to access
	 * @param value  the new value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public void setBoolean(Object obj, boolean value) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkSet(VMStack.getCallerClass(0), obj);
		VMField.setBoolean(obj, data.vm_member_id, value);
	}

	/**
	 * Sets the value of the field in the specified object to the {@code byte}
	 * value. This reproduces the effect of {@code object.fieldName = value}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 * <p/>
	 * If the value cannot be converted to the field type via a widening
	 * conversion, an IllegalArgumentException is thrown.
	 *
	 * @param obj the object to access
	 * @param value  the new value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public void setByte(Object obj, byte value) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkSet(VMStack.getCallerClass(0), obj);
		VMField.setByte(obj, data.vm_member_id, value);
	}

	/**
	 * Sets the value of the field in the specified object to the {@code char}
	 * value. This reproduces the effect of {@code object.fieldName = value}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 * <p/>
	 * If the value cannot be converted to the field type via a widening
	 * conversion, an IllegalArgumentException is thrown.
	 *
	 * @param obj the object to access
	 * @param value  the new value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public void setChar(Object obj, char value) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkSet(VMStack.getCallerClass(0), obj);
		VMField.setChar(obj, data.vm_member_id, value);
	}

	/**
	 * Sets the value of the field in the specified object to the {@code double}
	 * value. This reproduces the effect of {@code object.fieldName = value}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 * <p/>
	 * If the value cannot be converted to the field type via a widening
	 * conversion, an IllegalArgumentException is thrown.
	 *
	 * @param obj the object to access
	 * @param value  the new value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public void setDouble(Object obj, double value) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkSet(VMStack.getCallerClass(0), obj);
		VMField.setDouble(obj, data.vm_member_id, value);
	}

	/**
	 * Sets the value of the field in the specified object to the {@code float}
	 * value. This reproduces the effect of {@code object.fieldName = value}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 * <p/>
	 * If the value cannot be converted to the field type via a widening
	 * conversion, an IllegalArgumentException is thrown.
	 *
	 * @param obj the object to access
	 * @param value  the new value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public void setFloat(Object obj, float value) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkSet(VMStack.getCallerClass(0), obj);
		VMField.setFloat(obj, data.vm_member_id, value);
	}

	/**
	 * Set the value of the field in the specified object to the {@code int}
	 * value. This reproduces the effect of {@code object.fieldName = value}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 * <p/>
	 * If the value cannot be converted to the field type via a widening
	 * conversion, an IllegalArgumentException is thrown.
	 *
	 * @param obj the object to access
	 * @param value  the new value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public void setInt(Object obj, int value) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkSet(VMStack.getCallerClass(0), obj);
		VMField.setInt(obj, data.vm_member_id, value);
	}

	/**
	 * Sets the value of the field in the specified object to the {@code long}
	 * value. This reproduces the effect of {@code object.fieldName = value}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 * <p/>
	 * If the value cannot be converted to the field type via a widening
	 * conversion, an IllegalArgumentException is thrown.
	 *
	 * @param obj the object to access
	 * @param value  the new value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public void setLong(Object obj, long value) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkSet(VMStack.getCallerClass(0), obj);
		VMField.setLong(obj, data.vm_member_id, value);
	}

	/**
	 * Sets the value of the field in the specified object to the {@code short}
	 * value. This reproduces the effect of {@code object.fieldName = value}
	 * <p/>
	 * If this field is static, the object argument is ignored.
	 * Otherwise, if the object is {@code null}, a NullPointerException is
	 * thrown. If the object is not an instance of the declaring class of the
	 * method, an IllegalArgumentException is thrown.
	 * <p/>
	 * If this Field object is enforcing access control (see AccessibleObject)
	 * and this field is not accessible from the current context, an
	 * IllegalAccessException is thrown.
	 * <p/>
	 * If the value cannot be converted to the field type via a widening
	 * conversion, an IllegalArgumentException is thrown.
	 *
	 * @param obj the object to access
	 * @param value  the new value
	 * @throws NullPointerException     if the object is {@code null} and the field is non-static
	 * @throws IllegalArgumentException if the object is not compatible with the declaring class
	 * @throws IllegalAccessException   if this field is not accessible
	 */
	public void setShort(Object obj, short value) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkSet(VMStack.getCallerClass(0), obj);
		VMField.setShort(obj, data.vm_member_id, value);
	}

	/**
	 * Returns a string containing a concise, human-readable description of this
	 * field.
	 * <p/>
	 * The format of the string is:
	 * <ol>
	 * <li>modifiers (if any)
	 * <li>type
	 * <li>declaring class name
	 * <li>'.'
	 * <li>field name
	 * </ol>
	 * <p/>
	 * For example: {@code public static java.io.InputStream
	 * java.lang.System.in}
	 *
	 * @return a printable representation for this field
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(80);
		// append modifiers if any
		int modifier = getModifiers();
		if(modifier != 0)
		{
			sb.append(Modifier.toString(modifier)).append(' ');
		}
		// append return type
		appendArrayType(sb, getType());
		sb.append(' ');
		// append full field name
		sb.append(getDeclaringClass().getName()).append('.').append(getName());
		return sb.toString();
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		Annotation a[] = data.getAnnotations();
		Annotation aa[] = new Annotation[a.length];
		System.arraycopy(a, 0, aa, 0, a.length);
		return aa;
	}

	@Override
	public Annotation[] getAnnotations()
	{
		return getDeclaredAnnotations();
	}

	/* NON API SECTION */

	/**
	 * Checks that the specified obj is valid object for a getXXX operation.
	 *
	 * @param callerClass caller class of a getXXX method
	 * @param obj         object to check
	 * @return null if this field is static, otherwise obj one
	 * @throws IllegalArgumentException if obj argument is not valid
	 * @throws IllegalAccessException   if caller doesn't have access permission
	 */
	private Object checkGet(Class callerClass, Object obj) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkObject(getDeclaringClass(), getModifiers(), obj);
		if(!isAccessible)
		{
			reflectExporter.checkMemberAccess(callerClass, getDeclaringClass(), obj == null ? getDeclaringClass() : obj.getClass(), getModifiers());
		}
		return obj;
	}

	/**
	 * Checks that the specified obj is valid object for a setXXX operation.
	 *
	 * @param callerClass caller class of a setXXX method
	 * @param obj         object to check
	 * @return null if this field is static, otherwise obj
	 * @throws IllegalArgumentException if obj argument is not valid one
	 * @throws IllegalAccessException   if caller doesn't have access permission
	 *                                  or this field is final
	 */
	private Object checkSet(Class callerClass, Object obj) throws IllegalArgumentException, IllegalAccessException
	{
		obj = checkObject(getDeclaringClass(), getModifiers(), obj);
		if(Modifier.isFinal(getModifiers()) && !(isAccessible && obj != null))
		{
			throw new IllegalAccessException("Can not assign new value to the field with final modifier");
		}
		if(!isAccessible)
		{
			reflectExporter.checkMemberAccess(callerClass, getDeclaringClass(), obj == null ? getDeclaringClass() : obj.getClass(), getModifiers());
		}
		return obj;
	}

	long getId()
	{
		return data.vm_member_id;
	}
}
