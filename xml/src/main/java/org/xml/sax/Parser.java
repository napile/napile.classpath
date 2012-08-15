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

// SAX parser interface.
// http://www.saxproject.org
// No warranty; no copyright -- use this as you will.
// $Id: Parser.java,v 1.4 2010-11-01 04:36:19 joehw Exp $

package org.xml.sax;

import java.io.IOException;
import java.util.Locale;


/**
 * Basic interface for SAX (Simple API for XML) parsers.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * See <a href='http://www.saxproject.org'>http://www.saxproject.org</a>
 * for further information.
 * </blockquote>
 *
 * <p>This was the main event supplier interface for SAX1; it has
 * been replaced in SAX2 by {@link org.xml.sax.XMLReader XMLReader},
 * which includes Namespace support and sophisticated configurability
 * and extensibility.</p>
 *
 * <p>All SAX1 parsers must implement this basic interface: it allows
 * applications to register handlers for different types of events
 * and to initiate a parse from a URI, or a character stream.</p>
 *
 * <p>All SAX1 parsers must also implement a zero-argument constructor
 * (though other constructors are also allowed).</p>
 *
 * <p>SAX1 parsers are reusable but not re-entrant: the application
 * may reuse a parser object (possibly with a different input source)
 * once the first parse has completed successfully, but it may not
 * invoke the parse() methods recursively within a parse.</p>
 *
 * @deprecated This interface has been replaced by the SAX2
 *             {@link org.xml.sax.XMLReader XMLReader}
 *             interface, which includes Namespace support.
 * @since SAX 1.0
 * @author David Megginson
 * @version 2.0.1 (sax2r2)
 * @see org.xml.sax.EntityResolver
 * @see org.xml.sax.DTDHandler
 * @see org.xml.sax.DocumentHandler
 * @see org.xml.sax.ErrorHandler
 * @see org.xml.sax.HandlerBase
 * @see org.xml.sax.InputSource
 */
public interface Parser
{

    /**
     * Allow an application to request a locale for errors and warnings.
     *
     * <p>SAX parsers are not required to provide localisation for errors
     * and warnings; if they cannot support the requested locale,
     * however, they must throw a SAX exception.  Applications may
     * not request a locale change in the middle of a parse.</p>
     *
     * @param locale A Java Locale object.
     * @exception org.xml.sax.SAXException Throws an exception
     *            (using the previous or default locale) if the
     *            requested locale is not supported.
     * @see org.xml.sax.SAXException
     * @see org.xml.sax.SAXParseException
     */
    public abstract void setLocale (Locale locale)
        throws SAXException;


    /**
     * Allow an application to register a custom entity resolver.
     *
     * <p>If the application does not register an entity resolver, the
     * SAX parser will resolve system identifiers and open connections
     * to entities itself (this is the default behaviour implemented in
     * HandlerBase).</p>
     *
     * <p>Applications may register a new or different entity resolver
     * in the middle of a parse, and the SAX parser must begin using
     * the new resolver immediately.</p>
     *
     * @param resolver The object for resolving entities.
     * @see EntityResolver
     * @see HandlerBase
     */
    public abstract void setEntityResolver (EntityResolver resolver);


    /**
     * Allow an application to register a DTD event handler.
     *
     * <p>If the application does not register a DTD handler, all DTD
     * events reported by the SAX parser will be silently
     * ignored (this is the default behaviour implemented by
     * HandlerBase).</p>
     *
     * <p>Applications may register a new or different
     * handler in the middle of a parse, and the SAX parser must
     * begin using the new handler immediately.</p>
     *
     * @param handler The DTD handler.
     * @see DTDHandler
     * @see HandlerBase
     */
    public abstract void setDTDHandler (DTDHandler handler);


    /**
     * Allow an application to register a document event handler.
     *
     * <p>If the application does not register a document handler, all
     * document events reported by the SAX parser will be silently
     * ignored (this is the default behaviour implemented by
     * HandlerBase).</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the SAX parser must begin using the new
     * handler immediately.</p>
     *
     * @param handler The document handler.
     * @see DocumentHandler
     * @see HandlerBase
     */
    public abstract void setDocumentHandler (DocumentHandler handler);


    /**
     * Allow an application to register an error event handler.
     *
     * <p>If the application does not register an error event handler,
     * all error events reported by the SAX parser will be silently
     * ignored, except for fatalError, which will throw a SAXException
     * (this is the default behaviour implemented by HandlerBase).</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the SAX parser must begin using the new
     * handler immediately.</p>
     *
     * @param handler The error handler.
     * @see ErrorHandler
     * @see SAXException
     * @see HandlerBase
     */
    public abstract void setErrorHandler (ErrorHandler handler);


    /**
     * Parse an XML document.
     *
     * <p>The application can use this method to instruct the SAX parser
     * to begin parsing an XML document from any valid input
     * source (a character stream, a byte stream, or a URI).</p>
     *
     * <p>Applications may not invoke this method while a parse is in
     * progress (they should create a new Parser instead for each
     * additional XML document).  Once a parse is complete, an
     * application may reuse the same Parser object, possibly with a
     * different input source.</p>
     *
     * @param source The input source for the top-level of the
     *        XML document.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     * @see org.xml.sax.InputSource
     * @see #parse(java.lang.String)
     * @see #setEntityResolver
     * @see #setDTDHandler
     * @see #setDocumentHandler
     * @see #setErrorHandler
     */
    public abstract void parse (InputSource source)
        throws SAXException, IOException;


    /**
     * Parse an XML document from a system identifier (URI).
     *
     * <p>This method is a shortcut for the common case of reading a
     * document from a system identifier.  It is the exact
     * equivalent of the following:</p>
     *
     * <pre>
     * parse(new InputSource(systemId));
     * </pre>
     *
     * <p>If the system identifier is a URL, it must be fully resolved
     * by the application before it is passed to the parser.</p>
     *
     * @param systemId The system identifier (URI).
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     * @see #parse(org.xml.sax.InputSource)
     */
    public abstract void parse (String systemId)
        throws SAXException, IOException;

}

// end of Parser.java
