package org.omg.Security;


/**
* org/omg/Security/MechandOptions.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/Security.idl
* 14 Август 2012 г. 21:55:45 EEST
*/

public final class MechandOptions implements org.omg.CORBA.portable.IDLEntity
{
  public String mechanism_type = null;
  public short options_supported = (short)0;

  public MechandOptions ()
  {
  } // ctor

  public MechandOptions (String _mechanism_type, short _options_supported)
  {
    mechanism_type = _mechanism_type;
    options_supported = _options_supported;
  } // ctor

} // class MechandOptions
