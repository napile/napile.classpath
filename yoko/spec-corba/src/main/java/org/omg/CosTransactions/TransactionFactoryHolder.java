package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/TransactionFactoryHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CosTransactions.idl
* 14 Август 2012 г. 21:55:46 EEST
*/

public final class TransactionFactoryHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.TransactionFactory value = null;

  public TransactionFactoryHolder ()
  {
  }

  public TransactionFactoryHolder (org.omg.CosTransactions.TransactionFactory initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.TransactionFactoryHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.TransactionFactoryHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.TransactionFactoryHelper.type ();
  }

}