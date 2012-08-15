/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

// SAX parser factory.
// http://www.saxproject.org
// No warranty; no copyright -- use this as you will.
// $Id: ParserFactory.java,v 1.4 2010-11-01 04:36:20 joehw Exp $

package org.xml.sax.helpers;

import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.lang.SecurityException;
import java.lang.ClassCastException;

import org.xml.sax.Parser;


/**
 * Java-specific class for dynamically loading SAX parsers.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * See <a href='http://www.saxproject.org'>http://www.saxproject.org</a>
 * for further information.
 * </blockquote>
 *
 * <p><strong>Note:</strong> This class is designed to work with the now-deprecated
 * SAX1 {@link org.xml.sax.Parser Parser} class.  SAX2 applications should use
 * {@link org.xml.sax.helpers.XMLReaderFactory XMLReaderFactory} instead.</p>
 *
 * <p>ParserFactory is not part of the platform-independent definition
 * of SAX; it is an additional convenience class designed
 * specifically for Java XML application writers.  SAX applications
 * can use the static methods in this class to allocate a SAX parser
 * dynamically at run-time based either on the value of the
 * `org.xml.sax.parser' system property or on a string containing the class
 * name.</p>
 *
 * <p>Note that the application still requires an XML parser that
 * implements SAX1.</p>
 *
 * @deprecated This class works with the deprecated
 *             {@link org.xml.sax.Parser Parser}
 *             interface.
 * @since SAX 1.0
 * @author David Megginson
 * @version 2.0.1 (sax2r2)
 */
public class ParserFactory {


    /**
     * Private null constructor.
     */
    private ParserFactory ()
    {
    }


    /**
     * Create a new SAX parser using the `org.xml.sax.parser' system property.
     *
     * <p>The named class must exist and must implement the
     * {@link org.xml.sax.Parser Parser} interface.</p>
     *
     * @exception java.lang.NullPointerException There is no value
     *            for the `org.xml.sax.parser' system property.
     * @exception java.lang.ClassNotFoundException The SAX parser
     *            class was not found (check your CLASSPATH).
     * @exception IllegalAccessException The SAX parser class was
     *            found, but you do not have permission to load
     *            it.
     * @exception InstantiationException The SAX parser class was
     *            found but could not be instantiated.
     * @exception java.lang.ClassCastException The SAX parser class
     *            was found and instantiated, but does not implement
     *            org.xml.sax.Parser.
     * @see #makeParser(java.lang.String)
     * @see org.xml.sax.Parser
     */
    public static Parser makeParser ()
        throws ClassNotFoundException,
        IllegalAccessException,
        InstantiationException,
        NullPointerException,
        ClassCastException
    {
        String className = System.getProperty("org.xml.sax.parser");
        if (className == null) {
            throw new NullPointerException("No value for sax.parser property");
        } else {
            return makeParser(className);
        }
    }


    /**
     * Create a new SAX parser object using the class name provided.
     *
     * <p>The named class must exist and must implement the
     * {@link org.xml.sax.Parser Parser} interface.</p>
     *
     * @param className A string containing the name of the
     *                  SAX parser class.
     * @exception java.lang.ClassNotFoundException The SAX parser
     *            class was not found (check your CLASSPATH).
     * @exception IllegalAccessException The SAX parser class was
     *            found, but you do not have permission to load
     *            it.
     * @exception InstantiationException The SAX parser class was
     *            found but could not be instantiated.
     * @exception java.lang.ClassCastException The SAX parser class
     *            was found and instantiated, but does not implement
     *            org.xml.sax.Parser.
     * @see #makeParser()
     * @see org.xml.sax.Parser
     */
    public static Parser makeParser (String className)
        throws ClassNotFoundException,
        IllegalAccessException,
        InstantiationException,
        ClassCastException
    {
        return (Parser) NewInstance.newInstance (
                NewInstance.getClassLoader (), className);
    }

}
