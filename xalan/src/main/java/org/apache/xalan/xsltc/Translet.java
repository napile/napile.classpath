/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: Translet.java 468648 2006-10-28 07:00:06Z minchau $
 */

package org.apache.xalan.xsltc;

import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.serializer.SerializationHandler;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
public interface Translet {

    public void transform(DOM document, SerializationHandler handler)
	throws TransletException;
    public void transform(DOM document, SerializationHandler[] handlers)
	throws TransletException;
    public void transform(DOM document, DTMAxisIterator iterator,
			  SerializationHandler handler)
	throws TransletException;

    public Object addParameter(String name, Object value);

    public void buildKeys(DOM document, DTMAxisIterator iterator,
			  SerializationHandler handler, int root)
	throws TransletException;
    public void addAuxiliaryClass(Class auxClass);
    public Class getAuxiliaryClass(String className);
    public String[] getNamesArray();
    public String[] getUrisArray();
    public int[]    getTypesArray();
    public String[] getNamespaceArray();
}
