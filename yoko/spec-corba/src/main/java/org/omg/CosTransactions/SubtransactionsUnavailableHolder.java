package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/SubtransactionsUnavailableHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CosTransactions.idl
* 14 Август 2012 г. 21:55:46 EEST
*/

public final class SubtransactionsUnavailableHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.SubtransactionsUnavailable value = null;

  public SubtransactionsUnavailableHolder ()
  {
  }

  public SubtransactionsUnavailableHolder (org.omg.CosTransactions.SubtransactionsUnavailable initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.SubtransactionsUnavailableHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.SubtransactionsUnavailableHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.SubtransactionsUnavailableHelper.type ();
  }

}