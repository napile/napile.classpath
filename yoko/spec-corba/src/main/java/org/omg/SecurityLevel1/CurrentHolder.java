package org.omg.SecurityLevel1;

/**
* org/omg/SecurityLevel1/CurrentHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/SecurityLevel1.idl
* 14 Август 2012 г. 21:55:46 EEST
*/


/* */
public final class CurrentHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.SecurityLevel1.Current value = null;

  public CurrentHolder ()
  {
  }

  public CurrentHolder (org.omg.SecurityLevel1.Current initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.SecurityLevel1.CurrentHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.SecurityLevel1.CurrentHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.SecurityLevel1.CurrentHelper.type ();
  }

}
