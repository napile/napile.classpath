package org.omg.CSIIOP;

/**
* org/omg/CSIIOP/CompoundSecMechListHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CSIIOP.idl
* 14 Август 2012 г. 21:55:46 EEST
*/

public final class CompoundSecMechListHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSIIOP.CompoundSecMechList value = null;

  public CompoundSecMechListHolder ()
  {
  }

  public CompoundSecMechListHolder (org.omg.CSIIOP.CompoundSecMechList initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSIIOP.CompoundSecMechListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSIIOP.CompoundSecMechListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSIIOP.CompoundSecMechListHelper.type ();
  }

}