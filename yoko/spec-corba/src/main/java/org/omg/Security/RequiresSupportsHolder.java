package org.omg.Security;

/**
* org/omg/Security/RequiresSupportsHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/Security.idl
* 14 Август 2012 г. 21:55:45 EEST
*/


// administered are the "required" or "supported" set
public final class RequiresSupportsHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.RequiresSupports value = null;

  public RequiresSupportsHolder ()
  {
  }

  public RequiresSupportsHolder (org.omg.Security.RequiresSupports initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.RequiresSupportsHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.RequiresSupportsHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.RequiresSupportsHelper.type ();
  }

}
