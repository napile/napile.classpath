package org.omg.SecurityLevel2;

/**
* org/omg/SecurityLevel2/InvocationCredentialsPolicyHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/SecurityLevel2.idl
* 14 Август 2012 г. 21:55:46 EEST
*/


/* */
public final class InvocationCredentialsPolicyHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SecurityLevel2.InvocationCredentialsPolicy value = null;

  public InvocationCredentialsPolicyHolder ()
  {
  }

  public InvocationCredentialsPolicyHolder (org.omg.SecurityLevel2.InvocationCredentialsPolicy initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SecurityLevel2.InvocationCredentialsPolicyHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SecurityLevel2.InvocationCredentialsPolicyHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SecurityLevel2.InvocationCredentialsPolicyHelper.type ();
  }

}
