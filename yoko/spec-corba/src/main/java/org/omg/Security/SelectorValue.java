package org.omg.Security;


/**
* org/omg/Security/SelectorValue.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/Security.idl
* 14 Август 2012 г. 21:55:45 EEST
*/

public final class SelectorValue implements org.omg.CORBA.portable.IDLEntity
{
  public int selector = (int)0;
  public org.omg.CORBA.Any value = null;

  public SelectorValue ()
  {
  } // ctor

  public SelectorValue (int _selector, org.omg.CORBA.Any _value)
  {
    selector = _selector;
    value = _value;
  } // ctor

} // class SelectorValue
