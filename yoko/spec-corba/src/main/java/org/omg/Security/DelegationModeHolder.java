package org.omg.Security;

/**
* org/omg/Security/DelegationModeHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/Security.idl
* 14 Август 2012 г. 21:55:45 EEST
*/


// Delegation mode which can be administered
public final class DelegationModeHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.DelegationMode value = null;

  public DelegationModeHolder ()
  {
  }

  public DelegationModeHolder (org.omg.Security.DelegationMode initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.DelegationModeHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.DelegationModeHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.DelegationModeHelper.type ();
  }

}