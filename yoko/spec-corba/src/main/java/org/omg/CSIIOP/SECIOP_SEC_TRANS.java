package org.omg.CSIIOP;


/**
* org/omg/CSIIOP/SECIOP_SEC_TRANS.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CSIIOP.idl
* 14 Август 2012 г. 21:55:46 EEST
*/

public final class SECIOP_SEC_TRANS implements org.omg.CORBA.portable.IDLEntity
{
  public short target_supports = (short)0;
  public short target_requires = (short)0;
  public byte mech_oid[] = null;
  public byte target_name[] = null;
  public org.omg.CSIIOP.TransportAddress addresses[] = null;

  public SECIOP_SEC_TRANS ()
  {
  } // ctor

  public SECIOP_SEC_TRANS (short _target_supports, short _target_requires, byte[] _mech_oid, byte[] _target_name, org.omg.CSIIOP.TransportAddress[] _addresses)
  {
    target_supports = _target_supports;
    target_requires = _target_requires;
    mech_oid = _mech_oid;
    target_name = _target_name;
    addresses = _addresses;
  } // ctor

} // class SECIOP_SEC_TRANS