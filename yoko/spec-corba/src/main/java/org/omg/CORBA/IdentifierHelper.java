/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
*  contributor license agreements.  See the NOTICE file distributed with
*  this work for additional information regarding copyright ownership.
*  The ASF licenses this file to You under the Apache License, Version 2.0
*  (the "License"); you may not use this file except in compliance with
*  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.omg.CORBA;

//
// IDL:omg.org/CORBA/Identifier:1.0
//
public abstract class IdentifierHelper
{
    public static void
    insert(org.omg.CORBA.Any any, String val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static String
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
            return read(any.create_input_stream());
        else
            throw new org.omg.CORBA.BAD_OPERATION();
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode
    type()
    {
        if(typeCode_ == null)
        {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            typeCode_ = orb.create_alias_tc(id(), "Identifier", orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string));
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/CORBA/Identifier:1.0";
    }

    public static String
    read(org.omg.CORBA.portable.InputStream in)
    {
        String _ob_v;
        _ob_v = in.read_string();
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, String val)
    {
        out.write_string(val);
    }
}