package org.omg.CSI;

/**
* org/omg/CSI/ContextErrorHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CSI.idl
* 14 Август 2012 г. 21:55:46 EEST
*/

public final class ContextErrorHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSI.ContextError value = null;

  public ContextErrorHolder ()
  {
  }

  public ContextErrorHolder (org.omg.CSI.ContextError initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSI.ContextErrorHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSI.ContextErrorHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSI.ContextErrorHelper.type ();
  }

}