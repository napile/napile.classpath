package org.omg.Security;


/**
* org/omg/Security/Right.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/Security.idl
* 14 Август 2012 г. 21:55:45 EEST
*/

public final class Right implements org.omg.CORBA.portable.IDLEntity
{
  public org.omg.Security.ExtensibleFamily rights_family = null;
  public String the_right = null;

  public Right ()
  {
  } // ctor

  public Right (org.omg.Security.ExtensibleFamily _rights_family, String _the_right)
  {
    rights_family = _rights_family;
    the_right = _the_right;
  } // ctor

} // class Right
