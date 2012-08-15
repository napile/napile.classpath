/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
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
package org.apache.geronimo.interop;


/**
 * @version $Revision: 491396 $ $Date: 2006-12-30 22:06:13 -0800 (Sat, 30 Dec 2006) $
 */
public interface ApacheVMCID {

    /**
     * The vendor minor code ID reserved for Apache. This value is or'd with
     * the high order 20 bits of the minor code to produce the minor value
     * in a system exception.
     */
    public static final int value = 0x41534000;
}
