package org.omg.CSI;


/**
* org/omg/CSI/IdentityExtensionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CSI.idl
* 14 Август 2012 г. 21:55:46 EEST
*/

public final class IdentityExtensionHolder implements org.omg.CORBA.portable.Streamable
{
  public byte value[] = null;

  public IdentityExtensionHolder ()
  {
  }

  public IdentityExtensionHolder (byte[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.IdentityExtensionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.IdentityExtensionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.IdentityExtensionHelper.type ();
  }

}
