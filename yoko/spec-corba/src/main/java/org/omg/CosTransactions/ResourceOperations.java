package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/ResourceOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CosTransactions.idl
* 14 Август 2012 г. 21:55:46 EEST
*/

public interface ResourceOperations 
{
  org.omg.CosTransactions.Vote prepare () throws org.omg.CosTransactions.HeuristicMixed, org.omg.CosTransactions.HeuristicHazard;
  void rollback () throws org.omg.CosTransactions.HeuristicCommit, org.omg.CosTransactions.HeuristicMixed, org.omg.CosTransactions.HeuristicHazard;
  void commit () throws org.omg.CosTransactions.NotPrepared, org.omg.CosTransactions.HeuristicRollback, org.omg.CosTransactions.HeuristicMixed, org.omg.CosTransactions.HeuristicHazard;
  void commit_one_phase () throws org.omg.CosTransactions.HeuristicHazard;
  void forget ();
} // interface ResourceOperations
