package org.omg.Security;

/**
* org/omg/Security/RightsCombinatorHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/Security.idl
* 14 Август 2012 г. 21:55:45 EEST
*/

public final class RightsCombinatorHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.Security.RightsCombinator value = null;

  public RightsCombinatorHolder ()
  {
  }

  public RightsCombinatorHolder (org.omg.Security.RightsCombinator initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.Security.RightsCombinatorHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.Security.RightsCombinatorHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.Security.RightsCombinatorHelper.type ();
  }

}