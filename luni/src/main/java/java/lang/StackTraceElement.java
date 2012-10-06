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

/*[INCLUDE-IF mJava14]*/

package java.lang;

import java.io.Serializable;

/**
 * A representation of a single stack frame. Arrays of {@code StackTraceElement}
 * are stored in {@link Throwable} objects to represent the whole state of the
 * call stack at the time a {@code Throwable} gets thrown.
 *
 * @author Dmitry B. Yershov
 * @see Throwable#getStackTrace()
 * @since 1.4
 */
public final class StackTraceElement implements Serializable
{

	private static final long serialVersionUID = 6992337162326171013L;

	private final String declaringClass;

	private final String methodName;

	private final String fileName;

	private final int lineNumber;

	/**
	 * Constructs a new {@code StackTraceElement} for a specified execution
	 * point.
	 *
	 * @param cls    the fully qualified name of the class where execution is at.
	 * @param method the name of the method where execution is at.
	 * @param file   The name of the file where execution is at or {@code null}.
	 * @param line   the line of the file where execution is at, a negative number
	 *               if unknown or {@code -2} if the execution is in a native
	 *               method.
	 * @throws NullPointerException if {@code cls} or {@code method} is {@code null}.
	 * @since 1.5
	 */
	public StackTraceElement(String declaringClass, String methodName, String fileName, int lineNumber)
	{
		this.declaringClass = declaringClass.toString();
		this.methodName = methodName.toString();
		this.fileName = fileName;
		this.lineNumber = lineNumber;
	}

	/**
	 * Compares this instance with the specified object and indicates if they
	 * are equal. In order to be equal, the following conditions must be
	 * fulfilled:
	 * <ul>
	 * <li>{@code obj} must be a stack trace element,</li>
	 * <li>the method names of this stack trace element and of {@code obj} must
	 * not be {@code null},</li>
	 * <li>the class, method and file names as well as the line number of this
	 * stack trace element and of {@code obj} must be equal.</li>
	 * </ul>
	 *
	 * @param obj the object to compare this instance with.
	 * @return {@code true} if the specified object is equal to this
	 *         {@code StackTraceElement}; {@code false} otherwise.
	 * @see #hashCode
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}
		if(obj != null && obj instanceof StackTraceElement)
		{
			StackTraceElement ste = (StackTraceElement) obj;
			return declaringClass.equals(ste.declaringClass) && methodName.equals(ste.methodName) && (fileName == ste.fileName || (fileName != null && fileName.equals(ste.fileName))) && lineNumber == ste.lineNumber;
		}
		return false;
	}


	/**
	 * Returns the fully qualified name of the class belonging to this
	 * {@code StackTraceElement}.
	 *
	 * @return the fully qualified type name of the class
	 */
	public String getClassName()
	{
		return declaringClass;
	}

	/**
	 * Returns the name of the Java source file containing class belonging to
	 * this {@code StackTraceElement}.
	 *
	 * @return the name of the file, or {@code null} if this information is not
	 *         available.
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * Returns the line number in the source for the class belonging to this
	 * {@code StackTraceElement}.
	 *
	 * @return the line number, or a negative number if this information is not
	 *         available.
	 */
	public int getLineNumber()
	{
		return lineNumber;
	}

	/**
	 * Returns the name of the method belonging to this {@code
	 * StackTraceElement}.
	 *
	 * @return the name of the method, or "<unknown method>" if this information
	 *         is not available.
	 */
	public String getMethodName()
	{
		return methodName;
	}

	@Override
	public int hashCode()
	{
		return declaringClass.hashCode() ^ methodName.hashCode();
	}

	/**
	 * Indicates if the method name returned by {@link #getMethodName()} is
	 * implemented as a native method.
	 *
	 * @return {@code true} if the method in which this stack trace element is
	 *         executing is a native method; {@code false} otherwise.
	 */
	public boolean isNativeMethod()
	{
		return lineNumber == -2;
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(declaringClass).append('.').append(methodName);
		if(fileName == null)
		{
			sb.append(lineNumber == -2 ? "(Native Method)" : "(Unknown Source)");
		}
		else
		{
			sb.append('(').append(fileName);
			if(lineNumber >= 0)
			{
				sb.append(':').append(lineNumber);
			}
			sb.append(')');
		}
		return sb.toString();
	}
}
