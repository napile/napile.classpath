package org.omg.Security;


/**
* org/omg/Security/AuditEventType.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/Security.idl
* 14 Август 2012 г. 21:55:45 EEST
*/

public final class AuditEventType implements org.omg.CORBA.portable.IDLEntity
{
  public org.omg.Security.ExtensibleFamily event_family = null;
  public short event_type = (short)0;

  public AuditEventType ()
  {
  } // ctor

  public AuditEventType (org.omg.Security.ExtensibleFamily _event_family, short _event_type)
  {
    event_family = _event_family;
    event_type = _event_type;
  } // ctor

} // class AuditEventType
