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

package org.w3c.dom.css;

/**
 * The <code>CSSValueList</code> interface provides the abstraction of an
 * ordered collection of CSS values.
 * <p> Some properties allow an empty list into their syntax. In that case,
 * these properties take the <code>none</code> identifier. So, an empty list
 * means that the property has the value <code>none</code>.
 * <p> The items in the <code>CSSValueList</code> are accessible via an
 * integral index, starting from 0.
 * <p>See also the <a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Style-20001113'>Document Object Model (DOM) Level 2 Style Specification</a>.
 * @since DOM Level 2
 */
public interface CSSValueList extends CSSValue {
    /**
     * The number of <code>CSSValues</code> in the list. The range of valid
     * values of the indices is <code>0</code> to <code>length-1</code>
     * inclusive.
     */
    public int getLength();

    /**
     * Used to retrieve a <code>CSSValue</code> by ordinal index. The order in
     * this collection represents the order of the values in the CSS style
     * property. If index is greater than or equal to the number of values
     * in the list, this returns <code>null</code>.
     * @param index Index into the collection.
     * @return The <code>CSSValue</code> at the <code>index</code> position
     *   in the <code>CSSValueList</code>, or <code>null</code> if that is
     *   not a valid index.
     */
    public CSSValue item(int index);

}
