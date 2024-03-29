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
 * <code>NodeIterators</code> are used to step through a set of nodes, e.g.
 * the set of nodes in a <code>NodeList</code>, the document subtree
 * governed by a particular <code>Node</code>, the results of a query, or
 * any other set of nodes. The set of nodes to be iterated is determined by
 * the implementation of the <code>NodeIterator</code>. DOM Level 2
 * specifies a single <code>NodeIterator</code> implementation for
 * document-order traversal of a document subtree. Instances of these
 * <code>NodeIterators</code> are created by calling
 * <code>DocumentTraversal</code><code>.createNodeIterator()</code>.
 * <p>See also the <a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Traversal-Range-20001113'>Document Object Model (DOM) Level 2 Traversal and Range Specification</a>.
 * @since DOM Level 2
 */
public interface NodeIterator {
    /**
     * The root node of the <code>NodeIterator</code>, as specified when it
     * was created.
     */
    public Node getRoot();

    /**
     * This attribute determines which node types are presented via the
     * <code>NodeIterator</code>. The available set of constants is defined
     * in the <code>NodeFilter</code> interface.  Nodes not accepted by
     * <code>whatToShow</code> will be skipped, but their children may still
     * be considered. Note that this skip takes precedence over the filter,
     * if any.
     */
    public int getWhatToShow();

    /**
     * The <code>NodeFilter</code> used to screen nodes.
     */
    public NodeFilter getFilter();

    /**
     *  The value of this flag determines whether the children of entity
     * reference nodes are visible to the <code>NodeIterator</code>. If
     * false, these children  and their descendants will be rejected. Note
     * that this rejection takes precedence over <code>whatToShow</code> and
     * the filter. Also note that this is currently the only situation where
     * <code>NodeIterators</code> may reject a complete subtree rather than
     * skipping individual nodes.
     * <br>
     * <br> To produce a view of the document that has entity references
     * expanded and does not expose the entity reference node itself, use
     * the <code>whatToShow</code> flags to hide the entity reference node
     * and set <code>expandEntityReferences</code> to true when creating the
     * <code>NodeIterator</code>. To produce a view of the document that has
     * entity reference nodes but no entity expansion, use the
     * <code>whatToShow</code> flags to show the entity reference node and
     * set <code>expandEntityReferences</code> to false.
     */
    public boolean getExpandEntityReferences();

    /**
     * Returns the next node in the set and advances the position of the
     * <code>NodeIterator</code> in the set. After a
     * <code>NodeIterator</code> is created, the first call to
     * <code>nextNode()</code> returns the first node in the set.
     * @return The next <code>Node</code> in the set being iterated over, or
     *   <code>null</code> if there are no more members in that set.
     * @exception DOMException
     *   INVALID_STATE_ERR: Raised if this method is called after the
     *   <code>detach</code> method was invoked.
     */
    public Node nextNode()
                         throws DOMException;

    /**
     * Returns the previous node in the set and moves the position of the
     * <code>NodeIterator</code> backwards in the set.
     * @return The previous <code>Node</code> in the set being iterated over,
     *   or <code>null</code> if there are no more members in that set.
     * @exception DOMException
     *   INVALID_STATE_ERR: Raised if this method is called after the
     *   <code>detach</code> method was invoked.
     */
    public Node previousNode()
                             throws DOMException;

    /**
     * Detaches the <code>NodeIterator</code> from the set which it iterated
     * over, releasing any computational resources and placing the
     * <code>NodeIterator</code> in the INVALID state. After
     * <code>detach</code> has been invoked, calls to <code>nextNode</code>
     * or <code>previousNode</code> will raise the exception
     * INVALID_STATE_ERR.
     */
    public void detach();

}
