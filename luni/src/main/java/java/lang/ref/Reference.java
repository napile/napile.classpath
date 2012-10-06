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

package java.lang.ref;

/**
 * Provides an abstract class which describes behavior common to all reference
 * objects. It is not possible to create immediate subclasses of
 * {@code Reference} in addition to the ones provided by this package. It is
 * also not desirable to do so, since references require very close cooperation
 * with the system's garbage collector. The existing, specialized reference
 * classes should be used instead.
 *
 * @since 1.2
 */
public abstract class Reference<T>
{

	private volatile T referent;

	ReferenceQueue<? super T> queue;

	Reference next;

	/**
	 * Constructs a new instance of this class.
	 */
	Reference(T referent)
	{
		this.referent = referent;
	}

	Reference(T referent, ReferenceQueue<? super T> q)
	{
		this.queue = q;
		this.referent = referent;
	}

	/**
	 * Makes the referent {@code null}. This does not force the reference
	 * object to be enqueued.
	 */
	public void clear()
	{
		referent = null;
	}

	/**
	 * Forces the reference object to be enqueued if it has been associated with
	 * a queue.
	 *
	 * @return {@code true} if this call has caused the {@code Reference} to
	 *         become enqueued, or {@code false} otherwise
	 */
	public boolean enqueue()
	{
		if(next == null && queue != null)
		{
			queue.enqueue(this);
			queue = null;
			return true;
		}
		return false;
	}

	/**
	 * Returns the referent of the reference object.
	 *
	 * @return the referent to which reference refers, or {@code null} if the
	 *         object has been cleared.
	 */
	public T get()
	{
		return referent;
	}

	/**
	 * Checks whether the reference object has been enqueued.
	 *
	 * @return {@code true} if the {@code Reference} has been enqueued, {@code
	 *         false} otherwise
	 */
	public boolean isEnqueued()
	{
		return (next != null);
	}
}
