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

import org.w3c.dom.DOMException;

/**
 *  The <code>CSSCharsetRule</code> interface represents a @charset rule in a
 * CSS style sheet. The value of the <code>encoding</code> attribute does
 * not affect the encoding of text data in the DOM objects; this encoding is
 * always UTF-16. After a stylesheet is loaded, the value of the
 * <code>encoding</code> attribute is the value found in the
 * <code>@charset</code> rule. If there was no <code>@charset</code> in the
 * original document, then no <code>CSSCharsetRule</code> is created. The
 * value of the <code>encoding</code> attribute may also be used as a hint
 * for the encoding used on serialization of the style sheet.
 * <p> The value of the @charset rule (and therefore of the
 * <code>CSSCharsetRule</code>) may not correspond to the encoding the
 * document actually came in; character encoding information e.g. in an HTTP
 * header, has priority (see CSS document representation) but this is not
 * reflected in the <code>CSSCharsetRule</code>.
 * <p>See also the <a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Style-20001113'>Document Object Model (DOM) Level 2 Style Specification</a>.
 * @since DOM Level 2
 */
public interface CSSCharsetRule extends CSSRule {
    /**
     *  The encoding information used in this <code>@charset</code> rule.
     */
    public String getEncoding();
    /**
     *  The encoding information used in this <code>@charset</code> rule.
     * @exception DOMException
     *   SYNTAX_ERR: Raised if the specified encoding value has a syntax error
     *   and is unparsable.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this encoding rule is
     *   readonly.
     */
    public void setEncoding(String encoding)
                           throws DOMException;

}