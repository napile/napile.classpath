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

// DefaultHandler2.java - extended DefaultHandler
// http://www.saxproject.org
// Public Domain: no warranty.
// $Id: DefaultHandler2.java,v 1.4 2010-11-01 04:36:20 joehw Exp $

package org.xml.sax.ext;

import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * This class extends the SAX2 base handler class to support the
 * SAX2 {@link LexicalHandler}, {@link DeclHandler}, and
 * {@link EntityResolver2} extensions.  Except for overriding the
 * original SAX1 {@link DefaultHandler#resolveEntity resolveEntity()}
 * method the added handler methods just return.  Subclassers may
 * override everything on a method-by-method basis.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 *
 * <p> <em>Note:</em> this class might yet learn that the
 * <em>ContentHandler.setDocumentLocator()</em> call might be passed a
 * {@link Locator2} object, and that the
 * <em>ContentHandler.startElement()</em> call might be passed a
 * {@link Attributes2} object.
 *
 * @since SAX 2.0 (extensions 1.1 alpha)
 * @author David Brownell
 * @version TBS
 */
public class DefaultHandler2 extends DefaultHandler
    implements LexicalHandler, DeclHandler, EntityResolver2
{
    /** Constructs a handler which ignores all parsing events. */
    public DefaultHandler2 () { }


    // SAX2 ext-1.0 LexicalHandler

    public void startCDATA ()
    throws SAXException
        {}

    public void endCDATA ()
    throws SAXException
        {}

    public void startDTD (String name, String publicId, String systemId)
    throws SAXException
        {}

    public void endDTD ()
    throws SAXException
        {}

    public void startEntity (String name)
    throws SAXException
        {}

    public void endEntity (String name)
    throws SAXException
        {}

    public void comment (char ch [], int start, int length)
    throws SAXException
        { }


    // SAX2 ext-1.0 DeclHandler

    public void attributeDecl (String eName, String aName,
            String type, String mode, String value)
    throws SAXException
        {}

    public void elementDecl (String name, String model)
    throws SAXException
        {}

    public void externalEntityDecl (String name,
        String publicId, String systemId)
    throws SAXException
        {}

    public void internalEntityDecl (String name, String value)
    throws SAXException
        {}

    // SAX2 ext-1.1 EntityResolver2

    /**
     * Tells the parser that if no external subset has been declared
     * in the document text, none should be used.
     */
    public InputSource getExternalSubset (String name, String baseURI)
    throws SAXException, IOException
        { return null; }

    /**
     * Tells the parser to resolve the systemId against the baseURI
     * and read the entity text from that resulting absolute URI.
     * Note that because the older
     * {@link DefaultHandler#resolveEntity DefaultHandler.resolveEntity()},
     * method is overridden to call this one, this method may sometimes
     * be invoked with null <em>name</em> and <em>baseURI</em>, and
     * with the <em>systemId</em> already absolutized.
     */
    public InputSource resolveEntity (String name, String publicId,
            String baseURI, String systemId)
    throws SAXException, IOException
        { return null; }

    // SAX1 EntityResolver

    /**
     * Invokes
     * {@link EntityResolver2#resolveEntity EntityResolver2.resolveEntity()}
     * with null entity name and base URI.
     * You only need to override that method to use this class.
     */
    public InputSource resolveEntity (String publicId, String systemId)
    throws SAXException, IOException
        { return resolveEntity (null, publicId, null, systemId); }
}
