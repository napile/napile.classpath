package org.omg.CosTransactions;

/**
* org/omg/CosTransactions/OTSPolicyHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CosTransactions.idl
* 14 Август 2012 г. 21:55:46 EEST
*/

public final class OTSPolicyHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosTransactions.OTSPolicy value = null;

  public OTSPolicyHolder ()
  {
  }

  public OTSPolicyHolder (org.omg.CosTransactions.OTSPolicy initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosTransactions.OTSPolicyHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosTransactions.OTSPolicyHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosTransactions.OTSPolicyHelper.type ();
  }

}
