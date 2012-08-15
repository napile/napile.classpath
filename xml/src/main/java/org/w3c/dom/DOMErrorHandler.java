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
 *  <code>DOMErrorHandler</code> is a callback interface that the DOM
 * implementation can call when reporting errors that happens while
 * processing XML data, or when doing some other processing (e.g. validating
 * a document). A <code>DOMErrorHandler</code> object can be attached to a
 * <code>Document</code> using the "error-handler" on the
 * <code>DOMConfiguration</code> interface. If more than one error needs to
 * be reported during an operation, the sequence and numbers of the errors
 * passed to the error handler are implementation dependent.
 * <p> The application that is using the DOM implementation is expected to
 * implement this interface.
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407'>Document Object Model (DOM) Level 3 Core Specification</a>.
 * @since DOM Level 3
 */
public interface DOMErrorHandler {
    /**
     * This method is called on the error handler when an error occurs.
     * <br> If an exception is thrown from this method, it is considered to be
     * equivalent of returning <code>true</code>.
     * @param error  The error object that describes the error. This object
     *   may be reused by the DOM implementation across multiple calls to
     *   the <code>handleError</code> method.
     * @return  If the <code>handleError</code> method returns
     *   <code>false</code>, the DOM implementation should stop the current
     *   processing when possible. If the method returns <code>true</code>,
     *   the processing may continue depending on
     *   <code>DOMError.severity</code>.
     */
    public boolean handleError(DOMError error);

}
