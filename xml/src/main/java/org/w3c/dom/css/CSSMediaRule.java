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
import org.w3c.dom.stylesheets.MediaList;

/**
 *  The <code>CSSMediaRule</code> interface represents a @media rule in a CSS
 * style sheet. A <code>@media</code> rule can be used to delimit style
 * rules for specific media types.
 * <p>See also the <a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Style-20001113'>Document Object Model (DOM) Level 2 Style Specification</a>.
 * @since DOM Level 2
 */
public interface CSSMediaRule extends CSSRule {
    /**
     *  A list of media types for this rule.
     */
    public MediaList getMedia();

    /**
     *  A list of all CSS rules contained within the media block.
     */
    public CSSRuleList getCssRules();

    /**
     *  Used to insert a new rule into the media block.
     * @param rule  The parsable text representing the rule. For rule sets
     *   this contains both the selector and the style declaration. For
     *   at-rules, this specifies both the at-identifier and the rule
     *   content.
     * @param index  The index within the media block's rule collection of
     *   the rule before which to insert the specified rule. If the
     *   specified index is equal to the length of the media blocks's rule
     *   collection, the rule will be added to the end of the media block.
     * @return  The index within the media block's rule collection of the
     *   newly inserted rule.
     * @exception DOMException
     *   HIERARCHY_REQUEST_ERR: Raised if the rule cannot be inserted at the
     *   specified index, e.g., if an <code>@import</code> rule is inserted
     *   after a standard rule set or other at-rule.
     *   <br>INDEX_SIZE_ERR: Raised if the specified index is not a valid
     *   insertion point.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this media rule is
     *   readonly.
     *   <br>SYNTAX_ERR: Raised if the specified rule has a syntax error and
     *   is unparsable.
     */
    public int insertRule(String rule,
                          int index)
                          throws DOMException;

    /**
     *  Used to delete a rule from the media block.
     * @param index  The index within the media block's rule collection of
     *   the rule to remove.
     * @exception DOMException
     *   INDEX_SIZE_ERR: Raised if the specified index does not correspond to
     *   a rule in the media rule list.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this media rule is
     *   readonly.
     */
    public void deleteRule(int index)
                           throws DOMException;

}
