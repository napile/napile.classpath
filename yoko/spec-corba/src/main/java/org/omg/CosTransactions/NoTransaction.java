package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/NoTransaction.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CosTransactions.idl
* 14 Август 2012 г. 21:55:46 EEST
*/

public final class NoTransaction extends org.omg.CORBA.UserException
{

  public NoTransaction ()
  {
    super(NoTransactionHelper.id());
  } // ctor


  public NoTransaction (String $reason)
  {
    super(NoTransactionHelper.id() + "  " + $reason);
  } // ctor

} // class NoTransaction
