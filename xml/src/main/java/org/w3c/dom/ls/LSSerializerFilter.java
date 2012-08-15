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

import org.w3c.dom.traversal.NodeFilter;

/**
 *  <code>LSSerializerFilter</code>s provide applications the ability to
 * examine nodes as they are being serialized and decide what nodes should
 * be serialized or not. The <code>LSSerializerFilter</code> interface is
 * based on the <code>NodeFilter</code> interface defined in [<a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Traversal-Range-20001113'>DOM Level 2 Traversal and      Range</a>]
 * .
 * <p> <code>Document</code>, <code>DocumentType</code>,
 * <code>DocumentFragment</code>, <code>Notation</code>, <code>Entity</code>
 * , and children of <code>Attr</code> nodes are not passed to the filter.
 * The child nodes of an <code>EntityReference</code> node are only passed
 * to the filter if the <code>EntityReference</code> node is skipped by the
 * method <code>LSParserFilter.acceptNode()</code>.
 * <p> When serializing an <code>Element</code>, the element is passed to the
 * filter before any of its attributes are passed to the filter. Namespace
 * declaration attributes, and default attributes (except in the case when "
 * discard-default-content" is set to <code>false</code>), are never passed
 * to the filter.
 * <p> The result of any attempt to modify a node passed to a
 * <code>LSSerializerFilter</code> is implementation dependent.
 * <p> DOM applications must not raise exceptions in a filter. The effect of
 * throwing exceptions from a filter is DOM implementation dependent.
 * <p> For efficiency, a node passed to the filter may not be the same as the
 * one that is actually in the tree. And the actual node (node object
 * identity) may be reused during the process of filtering and serializing a
 * document.
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-LS-20040407'>Document Object Model (DOM) Level 3 Load
and Save Specification</a>.
 */
public interface LSSerializerFilter extends NodeFilter {
    /**
     *  Tells the <code>LSSerializer</code> what types of nodes to show to the
     * filter. If a node is not shown to the filter using this attribute, it
     * is automatically serialized. See <code>NodeFilter</code> for
     * definition of the constants. The constants <code>SHOW_DOCUMENT</code>
     * , <code>SHOW_DOCUMENT_TYPE</code>, <code>SHOW_DOCUMENT_FRAGMENT</code>
     * , <code>SHOW_NOTATION</code>, and <code>SHOW_ENTITY</code> are
     * meaningless here, such nodes will never be passed to a
     * <code>LSSerializerFilter</code>.
     * <br> Unlike [<a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Traversal-Range-20001113'>DOM Level 2 Traversal and      Range</a>]
     * , the <code>SHOW_ATTRIBUTE</code> constant indicates that the
     * <code>Attr</code> nodes are shown and passed to the filter.
     * <br> The constants used here are defined in [<a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Traversal-Range-20001113'>DOM Level 2 Traversal and      Range</a>]
     * .
     */
    public int getWhatToShow();

}
