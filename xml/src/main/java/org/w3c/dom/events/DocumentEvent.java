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

package org.w3c.dom.events;

import org.w3c.dom.DOMException;

/**
 *  The <code>DocumentEvent</code> interface provides a mechanism by which the
 * user can create an Event of a type supported by the implementation. It is
 * expected that the <code>DocumentEvent</code> interface will be
 * implemented on the same object which implements the <code>Document</code>
 * interface in an implementation which supports the Event model.
 * <p>See also the <a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Events-20001113'>Document Object Model (DOM) Level 2 Events Specification</a>.
 * @since DOM Level 2
 */
public interface DocumentEvent {
    /**
     *
     * @param eventType The <code>eventType</code> parameter specifies the
     *   type of <code>Event</code> interface to be created. If the
     *   <code>Event</code> interface specified is supported by the
     *   implementation this method will return a new <code>Event</code> of
     *   the interface type requested. If the <code>Event</code> is to be
     *   dispatched via the <code>dispatchEvent</code> method the
     *   appropriate event init method must be called after creation in
     *   order to initialize the <code>Event</code>'s values. As an example,
     *   a user wishing to synthesize some kind of <code>UIEvent</code>
     *   would call <code>createEvent</code> with the parameter "UIEvents".
     *   The <code>initUIEvent</code> method could then be called on the
     *   newly created <code>UIEvent</code> to set the specific type of
     *   UIEvent to be dispatched and set its context information.The
     *   <code>createEvent</code> method is used in creating
     *   <code>Event</code>s when it is either inconvenient or unnecessary
     *   for the user to create an <code>Event</code> themselves. In cases
     *   where the implementation provided <code>Event</code> is
     *   insufficient, users may supply their own <code>Event</code>
     *   implementations for use with the <code>dispatchEvent</code> method.
     * @return The newly created <code>Event</code>
     * @exception DOMException
     *   NOT_SUPPORTED_ERR: Raised if the implementation does not support the
     *   type of <code>Event</code> interface requested
     */
    public Event createEvent(String eventType)
                             throws DOMException;

}
