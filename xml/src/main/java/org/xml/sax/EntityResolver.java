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

// SAX entity resolver.
// http://www.saxproject.org
// No warranty; no copyright -- use this as you will.
// $Id: EntityResolver.java,v 1.4 2010-11-01 04:36:19 joehw Exp $

package org.xml.sax;

import java.io.IOException;


/**
 * Basic interface for resolving entities.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * See <a href='http://www.saxproject.org'>http://www.saxproject.org</a>
 * for further information.
 * </blockquote>
 *
 * <p>If a SAX application needs to implement customized handling
 * for external entities, it must implement this interface and
 * register an instance with the SAX driver using the
 * {@link org.xml.sax.XMLReader#setEntityResolver setEntityResolver}
 * method.</p>
 *
 * <p>The XML reader will then allow the application to intercept any
 * external entities (including the external DTD subset and external
 * parameter entities, if any) before including them.</p>
 *
 * <p>Many SAX applications will not need to implement this interface,
 * but it will be especially useful for applications that build
 * XML documents from databases or other specialised input sources,
 * or for applications that use URI types other than URLs.</p>
 *
 * <p>The following resolver would provide the application
 * with a special character stream for the entity with the system
 * identifier "http://www.myhost.com/today":</p>
 *
 * <pre>
 * import org.xml.sax.EntityResolver;
 * import org.xml.sax.InputSource;
 *
 * public class MyResolver implements EntityResolver {
 *   public InputSource resolveEntity (String publicId, String systemId)
 *   {
 *     if (systemId.equals("http://www.myhost.com/today")) {
 *              // return a special input source
 *       MyReader reader = new MyReader();
 *       return new InputSource(reader);
 *     } else {
 *              // use the default behaviour
 *       return null;
 *     }
 *   }
 * }
 * </pre>
 *
 * <p>The application can also use this interface to redirect system
 * identifiers to local URIs or to look up replacements in a catalog
 * (possibly by using the public identifier).</p>
 *
 * @since SAX 1.0
 * @author David Megginson
 * @version 2.0.1 (sax2r2)
 * @see org.xml.sax.XMLReader#setEntityResolver
 * @see org.xml.sax.InputSource
 */
public interface EntityResolver {


    /**
     * Allow the application to resolve external entities.
     *
     * <p>The parser will call this method before opening any external
     * entity except the top-level document entity.  Such entities include
     * the external DTD subset and external parameter entities referenced
     * within the DTD (in either case, only if the parser reads external
     * parameter entities), and external general entities referenced
     * within the document element (if the parser reads external general
     * entities).  The application may request that the parser locate
     * the entity itself, that it use an alternative URI, or that it
     * use data provided by the application (as a character or byte
     * input stream).</p>
     *
     * <p>Application writers can use this method to redirect external
     * system identifiers to secure and/or local URIs, to look up
     * public identifiers in a catalogue, or to read an entity from a
     * database or other input source (including, for example, a dialog
     * box).  Neither XML nor SAX specifies a preferred policy for using
     * public or system IDs to resolve resources.  However, SAX specifies
     * how to interpret any InputSource returned by this method, and that
     * if none is returned, then the system ID will be dereferenced as
     * a URL.  </p>
     *
     * <p>If the system identifier is a URL, the SAX parser must
     * resolve it fully before reporting it to the application.</p>
     *
     * @param publicId The public identifier of the external entity
     *        being referenced, or null if none was supplied.
     * @param systemId The system identifier of the external entity
     *        being referenced.
     * @return An InputSource object describing the new input source,
     *         or null to request that the parser open a regular
     *         URI connection to the system identifier.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException A Java-specific IO exception,
     *            possibly the result of creating a new InputStream
     *            or Reader for the InputSource.
     * @see org.xml.sax.InputSource
     */
    public abstract InputSource resolveEntity (String publicId,
                                               String systemId)
        throws SAXException, IOException;

}

// end of EntityResolver.java
