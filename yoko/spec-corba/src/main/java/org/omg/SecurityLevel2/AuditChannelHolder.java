package org.omg.SecurityLevel2;

/**
* org/omg/SecurityLevel2/AuditChannelHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/SecurityLevel2.idl
* 14 Август 2012 г. 21:55:46 EEST
*/


/* */
public final class AuditChannelHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SecurityLevel2.AuditChannel value = null;

  public AuditChannelHolder ()
  {
  }

  public AuditChannelHolder (org.omg.SecurityLevel2.AuditChannel initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SecurityLevel2.AuditChannelHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SecurityLevel2.AuditChannelHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SecurityLevel2.AuditChannelHelper.type ();
  }

}
