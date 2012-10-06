/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.lang.ref;

/**
 * The {@code ReferenceQueue} is the container on which reference objects are
 * enqueued when the garbage collector detects the reachability type specified
 * for the referent.
 *
 * @since 1.2
 */
public class ReferenceQueue<T>
{
	private Reference<? extends T> firstReference;

	/**
	 * Constructs a new instance of this class.
	 */
	public ReferenceQueue()
	{
	}

	/**
	 * Returns the next available reference from the queue, removing it in the
	 * process. Does not wait for a reference to become available.
	 *
	 * @return the next available reference, or {@code null} if no reference is
	 *         immediately available
	 */
	@SuppressWarnings("unchecked")
	public synchronized Reference<? extends T> poll()
	{
		if(firstReference == null)
			return null;
		Reference<? extends T> ref = firstReference;
		firstReference = (firstReference.next == firstReference ? null : firstReference.next);
		ref.next = null;
		return ref;
	}

	/**
	 * Returns the next available reference from the queue, removing it in the
	 * process. Waits indefinitely for a reference to become available.
	 *
	 * @return the next available reference
	 * @throws InterruptedException if the blocking call was interrupted for some reason
	 */
	public Reference<? extends T> remove() throws InterruptedException
	{
		return remove(0L);
	}

	/**
	 * Returns the next available reference from the queue, removing it in the
	 * process. Waits for a reference to become available or the given timeout
	 * period to elapse, whichever happens first.
	 *
	 * @param timeout maximum time (in ms) to spend waiting for a reference object
	 *                to become available. A value of zero results in the method
	 *                waiting indefinitely.
	 * @return the next available reference, or {@code null} if no reference
	 *         becomes available within the timeout period
	 * @throws IllegalArgumentException if the wait period is negative.
	 * @throws InterruptedException     if the blocking call was interrupted for some reason
	 */
	@SuppressWarnings("unchecked")
	public synchronized Reference<? extends T> remove(long timeout) throws IllegalArgumentException, InterruptedException
	{
		if(firstReference == null)
			wait(timeout);
		if(firstReference == null)
			return null;
		Reference<? extends T> ref = firstReference;
		firstReference = (firstReference.next == firstReference ? null : firstReference.next);
		ref.next = null;
		return ref;
	}


	/**
	 * Enqueue the reference object on the receiver.
	 *
	 * @param ref reference object to be enqueued.
	 * @return boolean true if reference is enqueued. false if reference failed
	 *         to enqueue.
	 */
	synchronized boolean enqueue(Reference<? extends T> ref)
	{
		ref.next = (firstReference == null ? ref : firstReference);
		firstReference = ref;
		notify();
		return true;
	}
}
