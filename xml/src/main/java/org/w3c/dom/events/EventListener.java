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

/**
 *  The <code>EventListener</code> interface is the primary method for
 * handling events. Users implement the <code>EventListener</code> interface
 * and register their listener on an <code>EventTarget</code> using the
 * <code>AddEventListener</code> method. The users should also remove their
 * <code>EventListener</code> from its <code>EventTarget</code> after they
 * have completed using the listener.
 * <p> When a <code>Node</code> is copied using the <code>cloneNode</code>
 * method the <code>EventListener</code>s attached to the source
 * <code>Node</code> are not attached to the copied <code>Node</code>. If
 * the user wishes the same <code>EventListener</code>s to be added to the
 * newly created copy the user must add them manually.
 * <p>See also the <a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Events-20001113'>Document Object Model (DOM) Level 2 Events Specification</a>.
 * @since DOM Level 2
 */
public interface EventListener {
    /**
     *  This method is called whenever an event occurs of the type for which
     * the <code> EventListener</code> interface was registered.
     * @param evt  The <code>Event</code> contains contextual information
     *   about the event. It also contains the <code>stopPropagation</code>
     *   and <code>preventDefault</code> methods which are used in
     *   determining the event's flow and default action.
     */
    public void handleEvent(Event evt);

}
