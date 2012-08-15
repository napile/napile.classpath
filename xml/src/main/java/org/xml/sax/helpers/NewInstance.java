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

// NewInstance.java - create a new instance of a class by name.
// http://www.saxproject.org
// Written by Edwin Goei, edwingo@apache.org
// and by David Brownell, dbrownell@users.sourceforge.net
// NO WARRANTY!  This class is in the Public Domain.
// $Id: NewInstance.java,v 1.5 2010-11-01 04:36:20 joehw Exp $

package org.xml.sax.helpers;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * Create a new instance of a class by name.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * See <a href='http://www.saxproject.org'>http://www.saxproject.org</a>
 * for further information.
 * </blockquote>
 *
 * <p>This class contains a static method for creating an instance of a
 * class from an explicit class name.  It tries to use the thread's context
 * ClassLoader if possible and falls back to using
 * Class.forName(String).</p>
 *
 * <p>This code is designed to compile and run on JDK version 1.1 and later
 * including versions of Java 2.</p>
 *
 * @author Edwin Goei, David Brownell
 * @version 2.0.1 (sax2r2)
 */
class NewInstance {

    /**
     * Creates a new instance of the specified class name
     *
     * Package private so this code is not exposed at the API level.
     */
    static Object newInstance (ClassLoader classLoader, String className)
        throws ClassNotFoundException, IllegalAccessException,
            InstantiationException
    {
        Class driverClass;
        if (classLoader == null) {
            driverClass = Class.forName(className);
        } else {
            driverClass = classLoader.loadClass(className);
        }
        return driverClass.newInstance();
    }

    /**
     * Figure out which ClassLoader to use.  For JDK 1.2 and later use
     * the context ClassLoader.
     */
    static ClassLoader getClassLoader ()
    {
        Method m = null;

        try {
            m = Thread.class.getMethod("getContextClassLoader", (Class[]) null);
        } catch (NoSuchMethodException e) {
            // Assume that we are running JDK 1.1, use the current ClassLoader
            return NewInstance.class.getClassLoader();
        }

        try {
            return (ClassLoader) m.invoke(Thread.currentThread(), (Object[]) null);
        } catch (IllegalAccessException e) {
            // assert(false)
            throw new UnknownError(e.getMessage());
        } catch (InvocationTargetException e) {
            // assert(e.getTargetException() instanceof SecurityException)
            throw new UnknownError(e.getMessage());
        }
    }
}
