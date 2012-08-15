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

/**
 * Filters are objects that know how to "filter out" nodes. If a
 * <code>NodeIterator</code> or <code>TreeWalker</code> is given a
 * <code>NodeFilter</code>, it applies the filter before it returns the next
 * node. If the filter says to accept the node, the traversal logic returns
 * it; otherwise, traversal looks for the next node and pretends that the
 * node that was rejected was not there.
 * <p>The DOM does not provide any filters. <code>NodeFilter</code> is just an
 * interface that users can implement to provide their own filters.
 * <p><code>NodeFilters</code> do not need to know how to traverse from node
 * to node, nor do they need to know anything about the data structure that
 * is being traversed. This makes it very easy to write filters, since the
 * only thing they have to know how to do is evaluate a single node. One
 * filter may be used with a number of different kinds of traversals,
 * encouraging code reuse.
 * <p>See also the <a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Traversal-Range-20001113'>Document Object Model (DOM) Level 2 Traversal and Range Specification</a>.
 * @since DOM Level 2
 */
public interface NodeFilter {
    // Constants returned by acceptNode
    /**
     * Accept the node. Navigation methods defined for
     * <code>NodeIterator</code> or <code>TreeWalker</code> will return this
     * node.
     */
    public static final short FILTER_ACCEPT             = 1;
    /**
     * Reject the node. Navigation methods defined for
     * <code>NodeIterator</code> or <code>TreeWalker</code> will not return
     * this node. For <code>TreeWalker</code>, the children of this node
     * will also be rejected. <code>NodeIterators</code> treat this as a
     * synonym for <code>FILTER_SKIP</code>.
     */
    public static final short FILTER_REJECT             = 2;
    /**
     * Skip this single node. Navigation methods defined for
     * <code>NodeIterator</code> or <code>TreeWalker</code> will not return
     * this node. For both <code>NodeIterator</code> and
     * <code>TreeWalker</code>, the children of this node will still be
     * considered.
     */
    public static final short FILTER_SKIP               = 3;

    // Constants for whatToShow
    /**
     * Show all <code>Nodes</code>.
     */
    public static final int SHOW_ALL                  = 0xFFFFFFFF;
    /**
     * Show <code>Element</code> nodes.
     */
    public static final int SHOW_ELEMENT              = 0x00000001;
    /**
     * Show <code>Attr</code> nodes. This is meaningful only when creating an
     * <code>NodeIterator</code> or <code>TreeWalker</code> with an
     * attribute node as its <code>root</code>; in this case, it means that
     * the attribute node will appear in the first position of the iteration
     * or traversal. Since attributes are never children of other nodes,
     * they do not appear when traversing over the document tree.
     */
    public static final int SHOW_ATTRIBUTE            = 0x00000002;
    /**
     * Show <code>Text</code> nodes.
     */
    public static final int SHOW_TEXT                 = 0x00000004;
    /**
     * Show <code>CDATASection</code> nodes.
     */
    public static final int SHOW_CDATA_SECTION        = 0x00000008;
    /**
     * Show <code>EntityReference</code> nodes.
     */
    public static final int SHOW_ENTITY_REFERENCE     = 0x00000010;
    /**
     * Show <code>Entity</code> nodes. This is meaningful only when creating
     * an <code>NodeIterator</code> or <code>TreeWalker</code> with an
     * <code>Entity</code> node as its <code>root</code>; in this case, it
     * means that the <code>Entity</code> node will appear in the first
     * position of the traversal. Since entities are not part of the
     * document tree, they do not appear when traversing over the document
     * tree.
     */
    public static final int SHOW_ENTITY               = 0x00000020;
    /**
     * Show <code>ProcessingInstruction</code> nodes.
     */
    public static final int SHOW_PROCESSING_INSTRUCTION = 0x00000040;
    /**
     * Show <code>Comment</code> nodes.
     */
    public static final int SHOW_COMMENT              = 0x00000080;
    /**
     * Show <code>Document</code> nodes.
     */
    public static final int SHOW_DOCUMENT             = 0x00000100;
    /**
     * Show <code>DocumentType</code> nodes.
     */
    public static final int SHOW_DOCUMENT_TYPE        = 0x00000200;
    /**
     * Show <code>DocumentFragment</code> nodes.
     */
    public static final int SHOW_DOCUMENT_FRAGMENT    = 0x00000400;
    /**
     * Show <code>Notation</code> nodes. This is meaningful only when creating
     * an <code>NodeIterator</code> or <code>TreeWalker</code> with a
     * <code>Notation</code> node as its <code>root</code>; in this case, it
     * means that the <code>Notation</code> node will appear in the first
     * position of the traversal. Since notations are not part of the
     * document tree, they do not appear when traversing over the document
     * tree.
     */
    public static final int SHOW_NOTATION             = 0x00000800;

    /**
     * Test whether a specified node is visible in the logical view of a
     * <code>TreeWalker</code> or <code>NodeIterator</code>. This function
     * will be called by the implementation of <code>TreeWalker</code> and
     * <code>NodeIterator</code>; it is not normally called directly from
     * user code. (Though you could do so if you wanted to use the same
     * filter to guide your own application logic.)
     * @param n The node to check to see if it passes the filter or not.
     * @return A constant to determine whether the node is accepted,
     *   rejected, or skipped, as defined above.
     */
    public short acceptNode(Node n);

}