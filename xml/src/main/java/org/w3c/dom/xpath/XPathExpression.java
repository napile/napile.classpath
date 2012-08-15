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

package org.w3c.dom.xpath;


import org.w3c.dom.Node;
import org.w3c.dom.DOMException;

/**
 * The <code>XPathExpression</code> interface represents a parsed and resolved
 * XPath expression.
 * <p>See also the <a href='http://www.w3.org/2002/08/WD-DOM-Level-3-XPath-20020820'>Document Object Model (DOM) Level 3 XPath Specification</a>.
 */
public interface XPathExpression {
    /**
     * Evaluates this XPath expression and returns a result.
     * @param contextNode The <code>context</code> is context node for the
     *   evaluation of this XPath expression.If the XPathEvaluator was
     *   obtained by casting the <code>Document</code> then this must be
     *   owned by the same document and must be a <code>Document</code>,
     *   <code>Element</code>, <code>Attribute</code>, <code>Text</code>,
     *   <code>CDATASection</code>, <code>Comment</code>,
     *   <code>ProcessingInstruction</code>, or <code>XPathNamespace</code>
     *   node.If the context node is a <code>Text</code> or a
     *   <code>CDATASection</code>, then the context is interpreted as the
     *   whole logical text node as seen by XPath, unless the node is empty
     *   in which case it may not serve as the XPath context.
     * @param type If a specific <code>type</code> is specified, then the
     *   result will be coerced to return the specified type relying on
     *   XPath conversions and fail if the desired coercion is not possible.
     *   This must be one of the type codes of <code>XPathResult</code>.
     * @param result The <code>result</code> specifies a specific result
     *   object which may be reused and returned by this method. If this is
     *   specified as <code>null</code>or the implementation does not reuse
     *   the specified result, a new result object will be constructed and
     *   returned.For XPath 1.0 results, this object will be of type
     *   <code>XPathResult</code>.
     * @return The result of the evaluation of the XPath expression.For XPath
     *   1.0 results, this object will be of type <code>XPathResult</code>.
     * @exception XPathException
     *   TYPE_ERR: Raised if the result cannot be converted to return the
     *   specified type.
     * @exception DOMException
     *   WRONG_DOCUMENT_ERR: The Node is from a document that is not supported
     *   by the XPathEvaluator that created this <code>XPathExpression</code>
     *   .
     *   <br>NOT_SUPPORTED_ERR: The Node is not a type permitted as an XPath
     *   context node or the request type is not permitted by this
     *   <code>XPathExpression</code>.
     */
    public Object evaluate(Node contextNode,
                           short type,
                           Object result)
                           throws XPathException, DOMException;

}
