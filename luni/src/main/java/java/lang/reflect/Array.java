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
/**
 * @author Evgueni Brevnov
 */


package java.lang.reflect;

/**
 * This class provides static methods to create and access arrays dynamically.
 */
public final class Array
{

	/**
	 * Prevent this class from being instantiated.
	 */
	private Array()
	{
		//do nothing
	}


	/**
	 * Returns the element of the array at the specified index. This reproduces
	 * the effect of {@code array[index]}. If the array component is a primitive
	 * type, the result is automatically wrapped.
	 *
	 * @param array the array
	 * @param index the index
	 * @return the requested element, possibly wrapped
	 * @throws NullPointerException           if the array is null
	 * @throws IllegalArgumentException       if {@code array} is not an array
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static Object get(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		try
		{
			return ((Object[]) array)[index];
		}
		catch(ClassCastException e)
		{
			if(array instanceof int[])
			{
				return new Integer(((int[]) array)[index]);
			}
			if(array instanceof boolean[])
			{
				return ((boolean[]) array)[index] ? Boolean.TRUE : Boolean.FALSE;
			}
			if(array instanceof float[])
			{
				return new Float(((float[]) array)[index]);
			}
			if(array instanceof char[])
			{
				return new Character(((char[]) array)[index]);
			}
			if(array instanceof double[])
			{
				return new Double(((double[]) array)[index]);
			}
			if(array instanceof long[])
			{
				return new Long(((long[]) array)[index]);
			}
			if(array instanceof short[])
			{
				return new Short(((short[]) array)[index]);
			}
			if(array instanceof byte[])
			{
				return new Byte(((byte[]) array)[index]);
			}
		}
		throw new IllegalArgumentException("Specified argument is not an array");
	}

	/**
	 * Returns the element of the array at the specified index, converted to a
	 * {@code boolean}, if possible. This reproduces the effect of {@code
	 * array[index]}
	 *
	 * @param array the array
	 * @param index the index
	 * @return the requested element
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if {@code array} is not an array or the element at the
	 *                                        index position can not be converted to the return type
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static boolean getBoolean(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		try
		{
			return ((boolean[]) array)[index];
		}
		catch(ClassCastException e)
		{
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * Returns the element of the array at the specified index, converted to a
	 * {@code byte}, if possible. This reproduces the effect of {@code
	 * array[index]}
	 *
	 * @param array the array
	 * @param index the index
	 * @return the requested element
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if {@code array} is not an array or the element at the
	 *                                        index position can not be converted to the return type
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static byte getByte(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		try
		{
			return ((byte[]) array)[index];
		}
		catch(ClassCastException e)
		{
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * Returns the element of the array at the specified index, converted to a
	 * {@code char}, if possible. This reproduces the effect of {@code
	 * array[index]}
	 *
	 * @param array the array
	 * @param index the index
	 * @return the requested element
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if {@code array} is not an array or the element at the
	 *                                        index position can not be converted to the return type
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static char getChar(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		try
		{
			return ((char[]) array)[index];
		}
		catch(ClassCastException e)
		{
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * Returns the element of the array at the specified index, converted to a
	 * {@code double}, if possible. This reproduces the effect of {@code
	 * array[index]}
	 *
	 * @param array the array
	 * @param index the index
	 * @return the requested element
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if {@code array} is not an array or the element at the
	 *                                        index position can not be converted to the return type
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static double getDouble(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		if(array instanceof double[])
		{
			return ((double[]) array)[index];
		}
		return getFloat(array, index);
	}

	/**
	 * Returns the element of the array at the specified index, converted to a
	 * {@code float}, if possible. This reproduces the effect of {@code
	 * array[index]}
	 *
	 * @param array the array
	 * @param index the index
	 * @return the requested element
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if {@code array} is not an array or the element at the
	 *                                        index position can not be converted to the return type
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static float getFloat(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		if(array instanceof float[])
		{
			return ((float[]) array)[index];
		}
		return getLong(array, index);
	}

	/**
	 * Returns the element of the array at the specified index, converted to an
	 * {@code int}, if possible. This reproduces the effect of {@code
	 * array[index]}
	 *
	 * @param array the array
	 * @param index the index
	 * @return the requested element
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if {@code array} is not an array or the element at the
	 *                                        index position can not be converted to the return type
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static int getInt(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		if(array instanceof int[])
		{
			return ((int[]) array)[index];
		}
		if(array instanceof char[])
		{
			return ((char[]) array)[index];
		}
		return getShort(array, index);
	}

	/**
	 * Returns the length of the array. This reproduces the effect of {@code
	 * array.length}
	 *
	 * @param array the array
	 * @return the length of the array
	 * @throws NullPointerException     if the {@code array} is {@code null}
	 * @throws IllegalArgumentException if {@code array} is not an array
	 */
	public static int getLength(Object array) throws IllegalArgumentException
	{
		try
		{
			return ((Object[]) array).length;
		}
		catch(ClassCastException e)
		{
			if(array instanceof int[])
			{
				return ((int[]) array).length;
			}
			if(array instanceof boolean[])
			{
				return ((boolean[]) array).length;
			}
			if(array instanceof float[])
			{
				return ((float[]) array).length;
			}
			if(array instanceof char[])
			{
				return ((char[]) array).length;
			}
			if(array instanceof double[])
			{
				return ((double[]) array).length;
			}
			if(array instanceof long[])
			{
				return ((long[]) array).length;
			}
			if(array instanceof short[])
			{
				return ((short[]) array).length;
			}
			if(array instanceof byte[])
			{
				return ((byte[]) array).length;
			}
		}
		throw new IllegalArgumentException("Specified argument is not an array");
	}

	/**
	 * Returns the element of the array at the specified index, converted to a
	 * {@code long}, if possible. This reproduces the effect of {@code
	 * array[index]}
	 *
	 * @param array the array
	 * @param index the index
	 * @return the requested element
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if {@code array} is not an array or the element at the
	 *                                        index position can not be converted to the return type
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static long getLong(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		if(array instanceof long[])
		{
			return ((long[]) array)[index];
		}
		return getInt(array, index);
	}

	/**
	 * Returns the element of the array at the specified index, converted to a
	 * {@code short}, if possible. This reproduces the effect of {@code
	 * array[index]}
	 *
	 * @param array the array
	 * @param index the index
	 * @return the requested element
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if {@code array} is not an array or the element at the
	 *                                        index position can not be converted to the return type
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static short getShort(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		if(array instanceof short[])
		{
			return ((short[]) array)[index];
		}
		return getByte(array, index);
	}

	/**
	 * Returns a new multidimensional array of the specified component type and
	 * dimensions. This reproduces the effect of {@code new
	 * componentType[d0][d1]...[dn]} for a dimensions array of { d0, d1, ... ,
	 * dn }.
	 *
	 * @param componentType the component type of the new array
	 * @param dimensions    the dimensions of the new array
	 * @return the new array
	 * @throws NullPointerException       if the component type is {@code null}
	 * @throws NegativeArraySizeException if any of the dimensions are negative
	 * @throws IllegalArgumentException   if the array of dimensions is of size zero, or exceeds the
	 *                                    limit of the number of dimension for an array (currently 255)
	 */
	public static Object newInstance(Class<?> componentType, int length) throws NegativeArraySizeException
	{
		return newInstance(componentType, new int[]{length});
	}

	/**
	 * Returns a new array of the specified component type and length. This
	 * reproduces the effect of {@code new componentType[size]}.
	 *
	 * @param componentType the component type of the new array
	 * @param dimensions          the length of the new array
	 * @return the new array
	 * @throws NullPointerException       if the component type is null
	 * @throws NegativeArraySizeException if {@code size < 0}
	 */
	public static Object newInstance(Class<?> componentType, int[] dimensions) throws IllegalArgumentException, NegativeArraySizeException
	{
		if(componentType == null)
		{
			throw new NullPointerException();
		}
		if(componentType == Void.TYPE || dimensions.length == 0)
		{
			throw new IllegalArgumentException("Can not create new array instance for the specified arguments");
		}
		return VMReflection.newArrayInstance(componentType, dimensions);
	}

	/**
	 * Sets the element of the array at the specified index to the value. This
	 * reproduces the effect of {@code array[index] = value}. If the array
	 * component is a primitive type, the value is automatically unwrapped.
	 *
	 * @param array the array
	 * @param index the index
	 * @param value the new value
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if {@code array} is not an array or the value cannot be
	 *                                        converted to the array type by a widening conversion
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static void set(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{

		if(array == null)
		{
			throw new NullPointerException();
		}

		try
		{
			((Object[]) array)[index] = value;
			return;
		}
		catch(ClassCastException e)
		{
			if(value instanceof Number)
			{
				if(value instanceof Integer)
				{
					setInt(array, index, ((Integer) value).intValue());
					return;
				}
				else if(value instanceof Float)
				{
					setFloat(array, index, ((Float) value).floatValue());
					return;
				}
				else if(value instanceof Double)
				{
					setDouble(array, index, ((Double) value).doubleValue());
					return;
				}
				else if(value instanceof Long)
				{
					setLong(array, index, ((Long) value).longValue());
					return;
				}
				else if(value instanceof Short)
				{
					setShort(array, index, ((Short) value).shortValue());
					return;
				}
				else if(value instanceof Byte)
				{
					setByte(array, index, ((Byte) value).byteValue());
					return;
				}
			}
			else if(value instanceof Boolean)
			{
				setBoolean(array, index, ((Boolean) value).booleanValue());
				return;
			}
			else if(value instanceof Character)
			{
				setChar(array, index, ((Character) value).charValue());
				return;
			}
		}
		catch(ArrayStoreException e)
		{
			throw new IllegalArgumentException(e.getMessage());
		}
		throw new IllegalArgumentException("Can not assign the specified value to the specified array component");
	}

	/**
	 * Sets the element of the array at the specified index to the {@code
	 * boolean} value. This reproduces the effect of {@code array[index] =
	 * value}.
	 *
	 * @param array the array
	 * @param index the index
	 * @param value the new value
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if the {@code array} is not an array or the value cannot be
	 *                                        converted to the array type by a widening conversion
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static void setBoolean(Object array, int index, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		try
		{
			((boolean[]) array)[index] = value;
		}
		catch(ClassCastException e)
		{
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * Sets the element of the array at the specified index to the {@code byte}
	 * value. This reproduces the effect of {@code array[index] = value}.
	 *
	 * @param array the array
	 * @param index the index
	 * @param value the new value
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if the {@code array} is not an array or the value cannot be
	 *                                        converted to the array type by a widening conversion
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static void setByte(Object array, int index, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		if(array instanceof byte[])
		{
			((byte[]) array)[index] = value;
			return;
		}
		setShort(array, index, value);
	}

	/**
	 * Set the element of the array at the specified index to the {@code char}
	 * value. This reproduces the effect of {@code array[index] = value}.
	 *
	 * @param array the array
	 * @param index the index
	 * @param value the new value
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if the {@code array} is not an array or the value cannot be
	 *                                        converted to the array type by a widening conversion
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static void setChar(Object array, int index, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		if(array instanceof char[])
		{
			((char[]) array)[index] = value;
			return;
		}
		setInt(array, index, value);
	}

	/**
	 * Set the element of the array at the specified index to the {@code double}
	 * value. This reproduces the effect of {@code array[index] = value}.
	 *
	 * @param array the array
	 * @param index the index
	 * @param value the new value
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if the {@code array} is not an array or the value cannot be
	 *                                        converted to the array type by a widening conversion
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static void setDouble(Object array, int index, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		try
		{
			((double[]) array)[index] = value;
		}
		catch(ClassCastException e)
		{
			throw new IllegalArgumentException(e.getMessage());
		}
	}


	/**
	 * Set the element of the array at the specified index to the {@code float}
	 * value. This reproduces the effect of {@code array[index] = value}.
	 *
	 * @param array the array
	 * @param index the index
	 * @param value the new value
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if the {@code array} is not an array or the value cannot be
	 *                                        converted to the array type by a widening conversion
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static void setFloat(Object array, int index, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		if(array instanceof float[])
		{
			((float[]) array)[index] = value;
			return;
		}
		setDouble(array, index, value);
	}


	/**
	 * Set the element of the array at the specified index to the {@code int}
	 * value. This reproduces the effect of {@code array[index] = value}.
	 *
	 * @param array the array
	 * @param index the index
	 * @param value the new value
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if the {@code array} is not an array or the value cannot be
	 *                                        converted to the array type by a widening conversion
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static void setInt(Object array, int index, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		if(array instanceof int[])
		{
			((int[]) array)[index] = value;
			return;
		}
		setLong(array, index, value);
	}

	/**
	 * Set the element of the array at the specified index to the {@code long}
	 * value. This reproduces the effect of {@code array[index] = value}.
	 *
	 * @param array the array
	 * @param index the index
	 * @param value the new value
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if the {@code array} is not an array or the value cannot be
	 *                                        converted to the array type by a widening conversion
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static void setLong(Object array, int index, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		if(array instanceof long[])
		{
			((long[]) array)[index] = value;
			return;
		}
		setFloat(array, index, value);
	}

	/**
	 * Set the element of the array at the specified index to the {@code short}
	 * value. This reproduces the effect of {@code array[index] = value}.
	 *
	 * @param array the array
	 * @param index the index
	 * @param value the new value
	 * @throws NullPointerException           if the {@code array} is {@code null}
	 * @throws IllegalArgumentException       if the {@code array} is not an array or the value cannot be
	 *                                        converted to the array type by a widening conversion
	 * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >= array.length}
	 */
	public static void setShort(Object array, int index, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		if(array instanceof short[])
		{
			((short[]) array)[index] = value;
			return;
		}
		setInt(array, index, value);
	}
}
