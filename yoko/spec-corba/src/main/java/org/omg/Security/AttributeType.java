package org.omg.Security;


/**
* org/omg/Security/AttributeType.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/Security.idl
* 14 Август 2012 г. 21:55:45 EEST
*/

public final class AttributeType implements org.omg.CORBA.portable.IDLEntity
{
  public org.omg.Security.ExtensibleFamily attribute_family = null;
  public int attribute_type = (int)0;

  public AttributeType ()
  {
  } // ctor

  public AttributeType (org.omg.Security.ExtensibleFamily _attribute_family, int _attribute_type)
  {
    attribute_family = _attribute_family;
    attribute_type = _attribute_type;
  } // ctor

} // class AttributeType
