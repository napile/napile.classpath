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

package org.w3c.dom.ls;

/**
 *  This interface represents an output destination for data.
 * <p> This interface allows an application to encapsulate information about
 * an output destination in a single object, which may include a URI, a byte
 * stream (possibly with a specified encoding), a base URI, and/or a
 * character stream.
 * <p> The exact definitions of a byte stream and a character stream are
 * binding dependent.
 * <p> The application is expected to provide objects that implement this
 * interface whenever such objects are needed. The application can either
 * provide its own objects that implement this interface, or it can use the
 * generic factory method <code>DOMImplementationLS.createLSOutput()</code>
 * to create objects that implement this interface.
 * <p> The <code>LSSerializer</code> will use the <code>LSOutput</code> object
 * to determine where to serialize the output to. The
 * <code>LSSerializer</code> will look at the different outputs specified in
 * the <code>LSOutput</code> in the following order to know which one to
 * output to, the first one that is not null and not an empty string will be
 * used:
 * <ol>
 * <li> <code>LSOutput.characterStream</code>
 * </li>
 * <li>
 * <code>LSOutput.byteStream</code>
 * </li>
 * <li> <code>LSOutput.systemId</code>
 * </li>
 * </ol>
 * <p> <code>LSOutput</code> objects belong to the application. The DOM
 * implementation will never modify them (though it may make copies and
 * modify the copies, if necessary).
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-LS-20040407'>Document Object Model (DOM) Level 3 Load
and Save Specification</a>.
 */
public interface LSOutput {
    /**
     *  An attribute of a language and binding dependent type that represents
     * a writable stream to which 16-bit units can be output.
     */
    public java.io.Writer getCharacterStream();
    /**
     *  An attribute of a language and binding dependent type that represents
     * a writable stream to which 16-bit units can be output.
     */
    public void setCharacterStream(java.io.Writer characterStream);

    /**
     *  An attribute of a language and binding dependent type that represents
     * a writable stream of bytes.
     */
    public java.io.OutputStream getByteStream();
    /**
     *  An attribute of a language and binding dependent type that represents
     * a writable stream of bytes.
     */
    public void setByteStream(java.io.OutputStream byteStream);

    /**
     *  The system identifier, a URI reference [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>], for this
     * output destination.
     * <br> If the system ID is a relative URI reference (see section 5 in [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>]), the
     * behavior is implementation dependent.
     */
    public String getSystemId();
    /**
     *  The system identifier, a URI reference [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>], for this
     * output destination.
     * <br> If the system ID is a relative URI reference (see section 5 in [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>]), the
     * behavior is implementation dependent.
     */
    public void setSystemId(String systemId);

    /**
     *  The character encoding to use for the output. The encoding must be a
     * string acceptable for an XML encoding declaration ([<a href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>] section
     * 4.3.3 "Character Encoding in Entities"), it is recommended that
     * character encodings registered (as charsets) with the Internet
     * Assigned Numbers Authority [<a href='ftp://ftp.isi.edu/in-notes/iana/assignments/character-sets'>IANA-CHARSETS</a>]
     *  should be referred to using their registered names.
     */
    public String getEncoding();
    /**
     *  The character encoding to use for the output. The encoding must be a
     * string acceptable for an XML encoding declaration ([<a href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>] section
     * 4.3.3 "Character Encoding in Entities"), it is recommended that
     * character encodings registered (as charsets) with the Internet
     * Assigned Numbers Authority [<a href='ftp://ftp.isi.edu/in-notes/iana/assignments/character-sets'>IANA-CHARSETS</a>]
     *  should be referred to using their registered names.
     */
    public void setEncoding(String encoding);

}
