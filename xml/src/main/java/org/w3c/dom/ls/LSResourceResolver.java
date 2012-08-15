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
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright (c) 2004 World Wide Web Consortium,
 *
 * (Massachusetts Institute of Technology, European Research Consortium for
 * Informatics and Mathematics, Keio University). All Rights Reserved. This
 * work is distributed under the W3C(r) Software License [1] in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231
 */

package org.w3c.dom.ls;

/**
 *  <code>LSResourceResolver</code> provides a way for applications to
 * redirect references to external resources.
 * <p> Applications needing to implement custom handling for external
 * resources can implement this interface and register their implementation
 * by setting the "resource-resolver" parameter of
 * <code>DOMConfiguration</code> objects attached to <code>LSParser</code>
 * and <code>LSSerializer</code>. It can also be register on
 * <code>DOMConfiguration</code> objects attached to <code>Document</code>
 * if the "LS" feature is supported.
 * <p> The <code>LSParser</code> will then allow the application to intercept
 * any external entities, including the external DTD subset and external
 * parameter entities, before including them. The top-level document entity
 * is never passed to the <code>resolveResource</code> method.
 * <p> Many DOM applications will not need to implement this interface, but it
 * will be especially useful for applications that build XML documents from
 * databases or other specialized input sources, or for applications that
 * use URNs.
 * <p ><b>Note:</b>  <code>LSResourceResolver</code> is based on the SAX2 [<a href='http://www.saxproject.org/'>SAX</a>] <code>EntityResolver</code>
 * interface.
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-LS-20040407'>Document Object Model (DOM) Level 3 Load
and Save Specification</a>.
 */
public interface LSResourceResolver {
    /**
     *  Allow the application to resolve external resources.
     * <br> The <code>LSParser</code> will call this method before opening any
     * external resource, including the external DTD subset, external
     * entities referenced within the DTD, and external entities referenced
     * within the document element (however, the top-level document entity
     * is not passed to this method). The application may then request that
     * the <code>LSParser</code> resolve the external resource itself, that
     * it use an alternative URI, or that it use an entirely different input
     * source.
     * <br> Application writers can use this method to redirect external
     * system identifiers to secure and/or local URI, to look up public
     * identifiers in a catalogue, or to read an entity from a database or
     * other input source (including, for example, a dialog box).
     * @param type  The type of the resource being resolved. For XML [<a href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>] resources
     *   (i.e. entities), applications must use the value
     *   <code>"http://www.w3.org/TR/REC-xml"</code>. For XML Schema [<a href='http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/'>XML Schema Part 1</a>]
     *   , applications must use the value
     *   <code>"http://www.w3.org/2001/XMLSchema"</code>. Other types of
     *   resources are outside the scope of this specification and therefore
     *   should recommend an absolute URI in order to use this method.
     * @param namespaceURI  The namespace of the resource being resolved,
     *   e.g. the target namespace of the XML Schema [<a href='http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/'>XML Schema Part 1</a>]
     *    when resolving XML Schema resources.
     * @param publicId  The public identifier of the external entity being
     *   referenced, or <code>null</code> if no public identifier was
     *   supplied or if the resource is not an entity.
     * @param systemId  The system identifier, a URI reference [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>], of the
     *   external resource being referenced, or <code>null</code> if no
     *   system identifier was supplied.
     * @param baseURI  The absolute base URI of the resource being parsed, or
     *   <code>null</code> if there is no base URI.
     * @return  A <code>LSInput</code> object describing the new input
     *   source, or <code>null</code> to request that the parser open a
     *   regular URI connection to the resource.
     */
    public LSInput resolveResource(String type,
                                   String namespaceURI,
                                   String publicId,
                                   String systemId,
                                   String baseURI);

}
