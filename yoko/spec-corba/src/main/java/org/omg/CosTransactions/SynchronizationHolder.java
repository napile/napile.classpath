package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/SynchronizationHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CosTransactions.idl
* 14 Август 2012 г. 21:55:46 EEST
*/


// Inheritance from TransactionalObject is for backward compatability //
public final class SynchronizationHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.Synchronization value = null;

  public SynchronizationHolder ()
  {
  }

  public SynchronizationHolder (org.omg.CosTransactions.Synchronization initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.SynchronizationHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.SynchronizationHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.SynchronizationHelper.type ();
  }

}
