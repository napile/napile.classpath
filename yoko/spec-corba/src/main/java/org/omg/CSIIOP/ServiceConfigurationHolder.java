package org.omg.CSIIOP;

/**
* org/omg/CSIIOP/ServiceConfigurationHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CSIIOP.idl
* 14 Август 2012 г. 21:55:46 EEST
*/

public final class ServiceConfigurationHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CSIIOP.ServiceConfiguration value = null;

  public ServiceConfigurationHolder ()
  {
  }

  public ServiceConfigurationHolder (org.omg.CSIIOP.ServiceConfiguration initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CSIIOP.ServiceConfigurationHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CSIIOP.ServiceConfigurationHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CSIIOP.ServiceConfigurationHelper.type ();
  }

}
