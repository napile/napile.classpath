package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/NoTransactionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CosTransactions.idl
* 14 Август 2012 г. 21:55:46 EEST
*/

public final class NoTransactionHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.NoTransaction value = null;

  public NoTransactionHolder ()
  {
  }

  public NoTransactionHolder (org.omg.CosTransactions.NoTransaction initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.NoTransactionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.NoTransactionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.NoTransactionHelper.type ();
  }

}