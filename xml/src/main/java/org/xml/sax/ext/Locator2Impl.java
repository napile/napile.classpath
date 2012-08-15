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

// Locator2Impl.java - extended LocatorImpl
// http://www.saxproject.org
// Public Domain: no warranty.
// $Id: Locator2Impl.java,v 1.4 2010-11-01 04:36:20 joehw Exp $

package org.xml.sax.ext;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;


/**
 * SAX2 extension helper for holding additional Entity information,
 * implementing the {@link Locator2} interface.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 *
 * <p> This is not part of core-only SAX2 distributions.</p>
 *
 * @since SAX 2.0.2
 * @author David Brownell
 * @version TBS
 */
public class Locator2Impl extends LocatorImpl implements Locator2
{
    private String      encoding;
    private String      version;


    /**
     * Construct a new, empty Locator2Impl object.
     * This will not normally be useful, since the main purpose
     * of this class is to make a snapshot of an existing Locator.
     */
    public Locator2Impl () { }

    /**
     * Copy an existing Locator or Locator2 object.
     * If the object implements Locator2, values of the
     * <em>encoding</em> and <em>version</em>strings are copied,
     * otherwise they set to <em>null</em>.
     *
     * @param locator The existing Locator object.
     */
    public Locator2Impl (Locator locator)
    {
        super (locator);
        if (locator instanceof Locator2) {
            Locator2    l2 = (Locator2) locator;

            version = l2.getXMLVersion ();
            encoding = l2.getEncoding ();
        }
    }

    ////////////////////////////////////////////////////////////////////
    // Locator2 method implementations
    ////////////////////////////////////////////////////////////////////

    /**
     * Returns the current value of the version property.
     *
     * @see #setXMLVersion
     */
    public String getXMLVersion ()
        { return version; }

    /**
     * Returns the current value of the encoding property.
     *
     * @see #setEncoding
     */
    public String getEncoding ()
        { return encoding; }


    ////////////////////////////////////////////////////////////////////
    // Setters
    ////////////////////////////////////////////////////////////////////

    /**
     * Assigns the current value of the version property.
     *
     * @param version the new "version" value
     * @see #getXMLVersion
     */
    public void setXMLVersion (String version)
        { this.version = version; }

    /**
     * Assigns the current value of the encoding property.
     *
     * @param encoding the new "encoding" value
     * @see #getEncoding
     */
    public void setEncoding (String encoding)
        { this.encoding = encoding; }
}
