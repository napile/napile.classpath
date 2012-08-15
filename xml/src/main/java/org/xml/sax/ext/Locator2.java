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

// Locator2.java - extended Locator
// http://www.saxproject.org
// Public Domain: no warranty.
// $Id: Locator2.java,v 1.4 2010-11-01 04:36:20 joehw Exp $

package org.xml.sax.ext;

import org.xml.sax.Locator;


/**
 * SAX2 extension to augment the entity information provided
 * though a {@link Locator}.
 * If an implementation supports this extension, the Locator
 * provided in {@link org.xml.sax.ContentHandler#setDocumentLocator
 * ContentHandler.setDocumentLocator() } will implement this
 * interface, and the
 * <em>http://xml.org/sax/features/use-locator2</em> feature
 * flag will have the value <em>true</em>.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 *
 * <p> XMLReader implementations are not required to support this
 * information, and it is not part of core-only SAX2 distributions.</p>
 *
 * @since SAX 2.0 (extensions 1.1 alpha)
 * @author David Brownell
 * @version TBS
 */
public interface Locator2 extends Locator
{
    /**
     * Returns the version of XML used for the entity.  This will
     * normally be the identifier from the current entity's
     * <em>&lt;?xml&nbsp;version='...'&nbsp;...?&gt;</em> declaration,
     * or be defaulted by the parser.
     *
     * @return Identifier for the XML version being used to interpret
     * the entity's text, or null if that information is not yet
     * available in the current parsing state.
     */
    public String getXMLVersion ();

    /**
     * Returns the name of the character encoding for the entity.
     * If the encoding was declared externally (for example, in a MIME
     * Content-Type header), that will be the name returned.  Else if there
     * was an <em>&lt;?xml&nbsp;...encoding='...'?&gt;</em> declaration at
     * the start of the document, that encoding name will be returned.
     * Otherwise the encoding will been inferred (normally to be UTF-8, or
     * some UTF-16 variant), and that inferred name will be returned.
     *
     * <p>When an {@link org.xml.sax.InputSource InputSource} is used
     * to provide an entity's character stream, this method returns the
     * encoding provided in that input stream.
     *
     * <p> Note that some recent W3C specifications require that text
     * in some encodings be normalized, using Unicode Normalization
     * Form C, before processing.  Such normalization must be performed
     * by applications, and would normally be triggered based on the
     * value returned by this method.
     *
     * <p> Encoding names may be those used by the underlying JVM,
     * and comparisons should be case-insensitive.
     *
     * @return Name of the character encoding being used to interpret
     * * the entity's text, or null if this was not provided for a *
     * character stream passed through an InputSource or is otherwise
     * not yet available in the current parsing state.
     */
    public String getEncoding ();
}
