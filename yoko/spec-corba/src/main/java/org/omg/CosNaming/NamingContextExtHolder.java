package org.omg.CosNaming;

/**
* org/omg/CosNaming/NamingContextExtHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CosNaming.idl
* 14 Август 2012 г. 21:55:46 EEST
*/


/**
   * A naming context extension is an extension to naming context that contains a set of name bindings in 
   * which each name is unique. Different names can be bound to an object 
   * in the same or different contexts at the same time. <p>
   * 
   * See <a href=" http://www.omg.org/corba/sectrans.htm#nam">CORBA COS 
   * Naming Specification.</a>
   */
public final class NamingContextExtHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosNaming.NamingContextExt value = null;

  public NamingContextExtHolder ()
  {
  }

  public NamingContextExtHolder (org.omg.CosNaming.NamingContextExt initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosNaming.NamingContextExtHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosNaming.NamingContextExtHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosNaming.NamingContextExtHelper.type ();
  }

}
