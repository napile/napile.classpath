package org.omg.Security;

/**
* org/omg/Security/SecurityContextStateHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/Security.idl
* 14 Август 2012 г. 21:55:45 EEST
*/


// Operational State of a Security Context
public final class SecurityContextStateHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.SecurityContextState value = null;

  public SecurityContextStateHolder ()
  {
  }

  public SecurityContextStateHolder (org.omg.Security.SecurityContextState initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.SecurityContextStateHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.SecurityContextStateHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.SecurityContextStateHelper.type ();
  }

}
