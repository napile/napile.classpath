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

package java.util;

/**
 * An Enumeration is used to sequence over a collection of objects.
 * <p>
 * Preferably an {@link Iterator} should be used. {@code Iterator} replaces the
 * enumeration interface and adds a way to remove elements from a collection.
 *
 * @see Hashtable
 * @see Properties
 * @see Vector
 * @version 1.0
 */
@Deprecated
public interface Enumeration<E> {

    /**
     * Returns whether this {@code Enumeration} has more elements.
     * 
     * @return {@code true} if there are more elements, {@code false} otherwise.
     * @see #nextElement
     */
    public boolean hasMoreElements();

    /**
     * Returns the next element in this {@code Enumeration}.
     * 
     * @return the next element..
     * @throws NoSuchElementException
     *             if there are no more elements.
     * @see #hasMoreElements
     */
    public E nextElement();
}
