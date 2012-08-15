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

package org.w3c.dom;

/**
 * <code>DocumentFragment</code> is a "lightweight" or "minimal"
 * <code>Document</code> object. It is very common to want to be able to
 * extract a portion of a document's tree or to create a new fragment of a
 * document. Imagine implementing a user command like cut or rearranging a
 * document by moving fragments around. It is desirable to have an object
 * which can hold such fragments and it is quite natural to use a Node for
 * this purpose. While it is true that a <code>Document</code> object could
 * fulfill this role, a <code>Document</code> object can potentially be a
 * heavyweight object, depending on the underlying implementation. What is
 * really needed for this is a very lightweight object.
 * <code>DocumentFragment</code> is such an object.
 * <p>Furthermore, various operations -- such as inserting nodes as children
 * of another <code>Node</code> -- may take <code>DocumentFragment</code>
 * objects as arguments; this results in all the child nodes of the
 * <code>DocumentFragment</code> being moved to the child list of this node.
 * <p>The children of a <code>DocumentFragment</code> node are zero or more
 * nodes representing the tops of any sub-trees defining the structure of
 * the document. <code>DocumentFragment</code> nodes do not need to be
 * well-formed XML documents (although they do need to follow the rules
 * imposed upon well-formed XML parsed entities, which can have multiple top
 * nodes). For example, a <code>DocumentFragment</code> might have only one
 * child and that child node could be a <code>Text</code> node. Such a
 * structure model represents neither an HTML document nor a well-formed XML
 * document.
 * <p>When a <code>DocumentFragment</code> is inserted into a
 * <code>Document</code> (or indeed any other <code>Node</code> that may
 * take children) the children of the <code>DocumentFragment</code> and not
 * the <code>DocumentFragment</code> itself are inserted into the
 * <code>Node</code>. This makes the <code>DocumentFragment</code> very
 * useful when the user wishes to create nodes that are siblings; the
 * <code>DocumentFragment</code> acts as the parent of these nodes so that
 * the user can use the standard methods from the <code>Node</code>
 * interface, such as <code>Node.insertBefore</code> and
 * <code>Node.appendChild</code>.
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407'>Document Object Model (DOM) Level 3 Core Specification</a>.
 */
public interface DocumentFragment extends Node {
}
