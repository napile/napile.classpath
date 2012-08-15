package org.omg.Security;


/**
* org/omg/Security/DelegationStateHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/Security.idl
* 14 Август 2012 г. 21:55:45 EEST
*/


// Delegation related
abstract public class DelegationStateHelper
{
  private static String  _id = "IDL:omg.org/Security/DelegationState:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.Security.DelegationState that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.Security.DelegationState extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_enum_tc (org.omg.Security.DelegationStateHelper.id (), "DelegationState", new String[] { "SecInitiator", "SecDelegate"} );
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.omg.Security.DelegationState read (org.omg.CORBA.portable.InputStream istream)
  {
    return org.omg.Security.DelegationState.from_int (istream.read_long ());
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.Security.DelegationState value)
  {
    ostream.write_long (value.value ());
  }

}
