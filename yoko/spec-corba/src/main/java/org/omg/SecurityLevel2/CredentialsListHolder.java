package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/CredentialsListHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/SecurityLevel2.idl
* 14 Август 2012 г. 21:55:46 EEST
*/

public final class CredentialsListHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SecurityLevel2.Credentials value[] = null;

  public CredentialsListHolder ()
  {
  }

  public CredentialsListHolder (org.omg.SecurityLevel2.Credentials[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SecurityLevel2.CredentialsListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SecurityLevel2.CredentialsListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SecurityLevel2.CredentialsListHelper.type ();
  }

}
