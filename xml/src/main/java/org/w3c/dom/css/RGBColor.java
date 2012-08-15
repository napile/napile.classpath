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
 *  The <code>RGBColor</code> interface is used to represent any RGB color
 * value. This interface reflects the values in the underlying style
 * property. Hence, modifications made to the <code>CSSPrimitiveValue</code>
 * objects modify the style property.
 * <p> A specified RGB color is not clipped (even if the number is outside the
 * range 0-255 or 0%-100%). A computed RGB color is clipped depending on the
 * device.
 * <p> Even if a style sheet can only contain an integer for a color value,
 * the internal storage of this integer is a float, and this can be used as
 * a float in the specified or the computed style.
 * <p> A color percentage value can always be converted to a number and vice
 * versa.
 * <p>See also the <a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Style-20001113'>Document Object Model (DOM) Level 2 Style Specification</a>.
 * @since DOM Level 2
 */
public interface RGBColor {
    /**
     *  This attribute is used for the red value of the RGB color.
     */
    public CSSPrimitiveValue getRed();

    /**
     *  This attribute is used for the green value of the RGB color.
     */
    public CSSPrimitiveValue getGreen();

    /**
     *  This attribute is used for the blue value of the RGB color.
     */
    public CSSPrimitiveValue getBlue();

}
