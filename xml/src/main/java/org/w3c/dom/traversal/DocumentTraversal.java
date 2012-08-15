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

package org.w3c.dom.traversal;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;

/**
 * <code>DocumentTraversal</code> contains methods that create
 * <code>NodeIterators</code> and <code>TreeWalkers</code> to traverse a
 * node and its children in document order (depth first, pre-order
 * traversal, which is equivalent to the order in which the start tags occur
 * in the text representation of the document). In DOMs which support the
 * Traversal feature, <code>DocumentTraversal</code> will be implemented by
 * the same objects that implement the Document interface.
 * <p>See also the <a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Traversal-Range-20001113'>Document Object Model (DOM) Level 2 Traversal and Range Specification</a>.
 * @since DOM Level 2
 */
public interface DocumentTraversal {
    /**
     * Create a new <code>NodeIterator</code> over the subtree rooted at the
     * specified node.
     * @param root The node which will be iterated together with its
     *   children. The <code>NodeIterator</code> is initially positioned
     *   just before this node. The <code>whatToShow</code> flags and the
     *   filter, if any, are not considered when setting this position. The
     *   root must not be <code>null</code>.
     * @param whatToShow This flag specifies which node types may appear in
     *   the logical view of the tree presented by the
     *   <code>NodeIterator</code>. See the description of
     *   <code>NodeFilter</code> for the set of possible <code>SHOW_</code>
     *   values.These flags can be combined using <code>OR</code>.
     * @param filter The <code>NodeFilter</code> to be used with this
     *   <code>NodeIterator</code>, or <code>null</code> to indicate no
     *   filter.
     * @param entityReferenceExpansion The value of this flag determines
     *   whether entity reference nodes are expanded.
     * @return The newly created <code>NodeIterator</code>.
     * @exception DOMException
     *   NOT_SUPPORTED_ERR: Raised if the specified <code>root</code> is
     *   <code>null</code>.
     */
    public NodeIterator createNodeIterator(Node root,
                                           int whatToShow,
                                           NodeFilter filter,
                                           boolean entityReferenceExpansion)
                                           throws DOMException;

    /**
     * Create a new <code>TreeWalker</code> over the subtree rooted at the
     * specified node.
     * @param root The node which will serve as the <code>root</code> for the
     *   <code>TreeWalker</code>. The <code>whatToShow</code> flags and the
     *   <code>NodeFilter</code> are not considered when setting this value;
     *   any node type will be accepted as the <code>root</code>. The
     *   <code>currentNode</code> of the <code>TreeWalker</code> is
     *   initialized to this node, whether or not it is visible. The
     *   <code>root</code> functions as a stopping point for traversal
     *   methods that look upward in the document structure, such as
     *   <code>parentNode</code> and nextNode. The <code>root</code> must
     *   not be <code>null</code>.
     * @param whatToShow This flag specifies which node types may appear in
     *   the logical view of the tree presented by the
     *   <code>TreeWalker</code>. See the description of
     *   <code>NodeFilter</code> for the set of possible <code>SHOW_</code>
     *   values.These flags can be combined using <code>OR</code>.
     * @param filter The <code>NodeFilter</code> to be used with this
     *   <code>TreeWalker</code>, or <code>null</code> to indicate no filter.
     * @param entityReferenceExpansion If this flag is false, the contents of
     *   <code>EntityReference</code> nodes are not presented in the logical
     *   view.
     * @return The newly created <code>TreeWalker</code>.
     * @exception DOMException
     *    NOT_SUPPORTED_ERR: Raised if the specified <code>root</code> is
     *   <code>null</code>.
     */
    public TreeWalker createTreeWalker(Node root,
                                       int whatToShow,
                                       NodeFilter filter,
                                       boolean entityReferenceExpansion)
                                       throws DOMException;

}
