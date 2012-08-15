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

import org.w3c.dom.Element;
import org.w3c.dom.stylesheets.DocumentStyle;

/**
 * This interface represents a document with a CSS view.
 * <p> The <code>getOverrideStyle</code> method provides a mechanism through
 * which a DOM author could effect immediate change to the style of an
 * element without modifying the explicitly linked style sheets of a
 * document or the inline style of elements in the style sheets. This style
 * sheet comes after the author style sheet in the cascade algorithm and is
 * called override style sheet. The override style sheet takes precedence
 * over author style sheets. An "!important" declaration still takes
 * precedence over a normal declaration. Override, author, and user style
 * sheets all may contain "!important" declarations. User "!important" rules
 * take precedence over both override and author "!important" rules, and
 * override "!important" rules take precedence over author "!important"
 * rules.
 * <p> The expectation is that an instance of the <code>DocumentCSS</code>
 * interface can be obtained by using binding-specific casting methods on an
 * instance of the <code>Document</code> interface.
 * <p>See also the <a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Style-20001113'>Document Object Model (DOM) Level 2 Style Specification</a>.
 * @since DOM Level 2
 */
public interface DocumentCSS extends DocumentStyle {
    /**
     *  This method is used to retrieve the override style declaration for a
     * specified element and a specified pseudo-element.
     * @param elt  The element whose style is to be modified. This parameter
     *   cannot be null.
     * @param pseudoElt  The pseudo-element or <code>null</code> if none.
     * @return  The override style declaration.
     */
    public CSSStyleDeclaration getOverrideStyle(Element elt,
                                                String pseudoElt);

}
