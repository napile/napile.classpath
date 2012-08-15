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
 * CDATA sections are used to escape blocks of text containing characters that
 * would otherwise be regarded as markup. The only delimiter that is
 * recognized in a CDATA section is the "]]&gt;" string that ends the CDATA
 * section. CDATA sections cannot be nested. Their primary purpose is for
 * including material such as XML fragments, without needing to escape all
 * the delimiters.
 * <p>The <code>CharacterData.data</code> attribute holds the text that is
 * contained by the CDATA section. Note that this <em>may</em> contain characters that need to be escaped outside of CDATA sections and
 * that, depending on the character encoding ("charset") chosen for
 * serialization, it may be impossible to write out some characters as part
 * of a CDATA section.
 * <p>The <code>CDATASection</code> interface inherits from the
 * <code>CharacterData</code> interface through the <code>Text</code>
 * interface. Adjacent <code>CDATASection</code> nodes are not merged by use
 * of the <code>normalize</code> method of the <code>Node</code> interface.
 * <p> No lexical check is done on the content of a CDATA section and it is
 * therefore possible to have the character sequence <code>"]]&gt;"</code>
 * in the content, which is illegal in a CDATA section per section 2.7 of [<a href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>]. The
 * presence of this character sequence must generate a fatal error during
 * serialization or the cdata section must be splitted before the
 * serialization (see also the parameter <code>"split-cdata-sections"</code>
 * in the <code>DOMConfiguration</code> interface).
 * <p ><b>Note:</b> Because no markup is recognized within a
 * <code>CDATASection</code>, character numeric references cannot be used as
 * an escape mechanism when serializing. Therefore, action needs to be taken
 * when serializing a <code>CDATASection</code> with a character encoding
 * where some of the contained characters cannot be represented. Failure to
 * do so would not produce well-formed XML.
 * <p ><b>Note:</b> One potential solution in the serialization process is to
 * end the CDATA section before the character, output the character using a
 * character reference or entity reference, and open a new CDATA section for
 * any further characters in the text node. Note, however, that some code
 * conversion libraries at the time of writing do not return an error or
 * exception when a character is missing from the encoding, making the task
 * of ensuring that data is not corrupted on serialization more difficult.
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407'>Document Object Model (DOM) Level 3 Core Specification</a>.
 */
public interface CDATASection extends Text {
}
