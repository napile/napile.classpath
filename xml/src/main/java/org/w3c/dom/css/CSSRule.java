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
 *  The <code>CSSRule</code> interface is the abstract base interface for any
 * type of CSS statement. This includes both rule sets and at-rules. An
 * implementation is expected to preserve all rules specified in a CSS style
 * sheet, even if the rule is not recognized by the parser. Unrecognized
 * rules are represented using the <code>CSSUnknownRule</code> interface.
 * <p>See also the <a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Style-20001113'>Document Object Model (DOM) Level 2 Style Specification</a>.
 * @since DOM Level 2
 */
public interface CSSRule {
    // RuleType
    /**
     * The rule is a <code>CSSUnknownRule</code>.
     */
    public static final short UNKNOWN_RULE              = 0;
    /**
     * The rule is a <code>CSSStyleRule</code>.
     */
    public static final short STYLE_RULE                = 1;
    /**
     * The rule is a <code>CSSCharsetRule</code>.
     */
    public static final short CHARSET_RULE              = 2;
    /**
     * The rule is a <code>CSSImportRule</code>.
     */
    public static final short IMPORT_RULE               = 3;
    /**
     * The rule is a <code>CSSMediaRule</code>.
     */
    public static final short MEDIA_RULE                = 4;
    /**
     * The rule is a <code>CSSFontFaceRule</code>.
     */
    public static final short FONT_FACE_RULE            = 5;
    /**
     * The rule is a <code>CSSPageRule</code>.
     */
    public static final short PAGE_RULE                 = 6;

    /**
     *  The type of the rule, as defined above. The expectation is that
     * binding-specific casting methods can be used to cast down from an
     * instance of the <code>CSSRule</code> interface to the specific
     * derived interface implied by the <code>type</code>.
     */
    public short getType();

    /**
     *  The parsable textual representation of the rule. This reflects the
     * current state of the rule and not its initial value.
     */
    public String getCssText();
    /**
     *  The parsable textual representation of the rule. This reflects the
     * current state of the rule and not its initial value.
     * @exception DOMException
     *   SYNTAX_ERR: Raised if the specified CSS string value has a syntax
     *   error and is unparsable.
     *   <br>INVALID_MODIFICATION_ERR: Raised if the specified CSS string
     *   value represents a different type of rule than the current one.
     *   <br>HIERARCHY_REQUEST_ERR: Raised if the rule cannot be inserted at
     *   this point in the style sheet.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if the rule is readonly.
     */
    public void setCssText(String cssText)
                        throws DOMException;

    /**
     *  The style sheet that contains this rule.
     */
    public CSSStyleSheet getParentStyleSheet();

    /**
     *  If this rule is contained inside another rule (e.g. a style rule
     * inside an @media block), this is the containing rule. If this rule is
     * not nested inside any other rules, this returns <code>null</code>.
     */
    public CSSRule getParentRule();

}
