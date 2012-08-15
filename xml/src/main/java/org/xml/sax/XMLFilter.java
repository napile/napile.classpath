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

// XMLFilter.java - filter SAX2 events.
// http://www.saxproject.org
// Written by David Megginson
// NO WARRANTY!  This class is in the Public Domain.
// $Id: XMLFilter.java,v 1.4 2010-11-01 04:36:19 joehw Exp $

package org.xml.sax;


/**
 * Interface for an XML filter.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * See <a href='http://www.saxproject.org'>http://www.saxproject.org</a>
 * for further information.
 * </blockquote>
 *
 * <p>An XML filter is like an XML reader, except that it obtains its
 * events from another XML reader rather than a primary source like
 * an XML document or database.  Filters can modify a stream of
 * events as they pass on to the final application.</p>
 *
 * <p>The XMLFilterImpl helper class provides a convenient base
 * for creating SAX2 filters, by passing on all {@link org.xml.sax.EntityResolver
 * EntityResolver}, {@link org.xml.sax.DTDHandler DTDHandler},
 * {@link org.xml.sax.ContentHandler ContentHandler} and {@link org.xml.sax.ErrorHandler
 * ErrorHandler} events automatically.</p>
 *
 * @since SAX 2.0
 * @author David Megginson
 * @version 2.0.1 (sax2r2)
 * @see org.xml.sax.helpers.XMLFilterImpl
 */
public interface XMLFilter extends XMLReader
{

    /**
     * Set the parent reader.
     *
     * <p>This method allows the application to link the filter to
     * a parent reader (which may be another filter).  The argument
     * may not be null.</p>
     *
     * @param parent The parent reader.
     */
    public abstract void setParent (XMLReader parent);


    /**
     * Get the parent reader.
     *
     * <p>This method allows the application to query the parent
     * reader (which may be another filter).  It is generally a
     * bad idea to perform any operations on the parent reader
     * directly: they should all pass through this filter.</p>
     *
     * @return The parent filter, or null if none has been set.
     */
    public abstract XMLReader getParent ();

}

// end of XMLFilter.java
