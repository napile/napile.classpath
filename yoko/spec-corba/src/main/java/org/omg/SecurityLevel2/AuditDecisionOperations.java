package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/AuditDecisionOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/SecurityLevel2.idl
* 14 Август 2012 г. 21:55:46 EEST
*/


/* */
public interface AuditDecisionOperations 
{
  boolean audit_needed (org.omg.Security.AuditEventType event_type, org.omg.Security.SelectorValue[] value_list);
  org.omg.SecurityLevel2.AuditChannel audit_channel ();
} // interface AuditDecisionOperations