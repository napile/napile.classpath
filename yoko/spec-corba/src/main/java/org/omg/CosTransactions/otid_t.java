package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/otid_t.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CosTransactions.idl
* 14 Август 2012 г. 21:55:46 EEST
*/

public final class otid_t implements org.omg.CORBA.portable.IDLEntity
{
  public int formatID = (int)0;

  /*format identifier. 0 is OSI TP */
  public int bqual_length = (int)0;
  public byte tid[] = null;

  public otid_t ()
  {
  } // ctor

  public otid_t (int _formatID, int _bqual_length, byte[] _tid)
  {
    formatID = _formatID;
    bqual_length = _bqual_length;
    tid = _tid;
  } // ctor

} // class otid_t
