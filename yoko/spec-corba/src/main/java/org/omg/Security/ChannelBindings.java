package org.omg.Security;


/**
* org/omg/Security/ChannelBindings.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/Security.idl
* 14 Август 2012 г. 21:55:45 EEST
*/

public final class ChannelBindings implements org.omg.CORBA.portable.IDLEntity
{
  public int initiator_addrtype = (int)0;
  public byte initiator_address[] = null;
  public int acceptor_addrtype = (int)0;
  public byte acceptor_address[] = null;
  public byte application_data[] = null;

  public ChannelBindings ()
  {
  } // ctor

  public ChannelBindings (int _initiator_addrtype, byte[] _initiator_address, int _acceptor_addrtype, byte[] _acceptor_address, byte[] _application_data)
  {
    initiator_addrtype = _initiator_addrtype;
    initiator_address = _initiator_address;
    acceptor_addrtype = _acceptor_addrtype;
    acceptor_address = _acceptor_address;
    application_data = _application_data;
  } // ctor

} // class ChannelBindings
