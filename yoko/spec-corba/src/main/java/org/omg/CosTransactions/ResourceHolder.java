package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/ResourceHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CosTransactions.idl
* 14 Август 2012 г. 21:55:46 EEST
*/

public final class ResourceHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.Resource value = null;

  public ResourceHolder ()
  {
  }

  public ResourceHolder (org.omg.CosTransactions.Resource initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.ResourceHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.ResourceHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.ResourceHelper.type ();
  }

}
