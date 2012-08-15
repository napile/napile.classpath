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
 * Each <code>Document</code> has a <code>doctype</code> attribute whose value
 * is either <code>null</code> or a <code>DocumentType</code> object. The
 * <code>DocumentType</code> interface in the DOM Core provides an interface
 * to the list of entities that are defined for the document, and little
 * else because the effect of namespaces and the various XML schema efforts
 * on DTD representation are not clearly understood as of this writing.
 * <p>DOM Level 3 doesn't support editing <code>DocumentType</code> nodes.
 * <code>DocumentType</code> nodes are read-only.
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407'>Document Object Model (DOM) Level 3 Core Specification</a>.
 */
public interface DocumentType extends Node {
    /**
     * The name of DTD; i.e., the name immediately following the
     * <code>DOCTYPE</code> keyword.
     */
    public String getName();

    /**
     * A <code>NamedNodeMap</code> containing the general entities, both
     * external and internal, declared in the DTD. Parameter entities are
     * not contained. Duplicates are discarded. For example in:
     * <pre>&lt;!DOCTYPE
     * ex SYSTEM "ex.dtd" [ &lt;!ENTITY foo "foo"&gt; &lt;!ENTITY bar
     * "bar"&gt; &lt;!ENTITY bar "bar2"&gt; &lt;!ENTITY % baz "baz"&gt;
     * ]&gt; &lt;ex/&gt;</pre>
     *  the interface provides access to <code>foo</code>
     * and the first declaration of <code>bar</code> but not the second
     * declaration of <code>bar</code> or <code>baz</code>. Every node in
     * this map also implements the <code>Entity</code> interface.
     * <br>The DOM Level 2 does not support editing entities, therefore
     * <code>entities</code> cannot be altered in any way.
     */
    public NamedNodeMap getEntities();

    /**
     * A <code>NamedNodeMap</code> containing the notations declared in the
     * DTD. Duplicates are discarded. Every node in this map also implements
     * the <code>Notation</code> interface.
     * <br>The DOM Level 2 does not support editing notations, therefore
     * <code>notations</code> cannot be altered in any way.
     */
    public NamedNodeMap getNotations();

    /**
     * The public identifier of the external subset.
     * @since DOM Level 2
     */
    public String getPublicId();

    /**
     * The system identifier of the external subset. This may be an absolute
     * URI or not.
     * @since DOM Level 2
     */
    public String getSystemId();

    /**
     * The internal subset as a string, or <code>null</code> if there is none.
     * This is does not contain the delimiting square brackets.
     * <p ><b>Note:</b> The actual content returned depends on how much
     * information is available to the implementation. This may vary
     * depending on various parameters, including the XML processor used to
     * build the document.
     * @since DOM Level 2
     */
    public String getInternalSubset();

}
