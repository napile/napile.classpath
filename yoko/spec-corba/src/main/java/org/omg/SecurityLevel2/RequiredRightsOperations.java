package org.omg.SecurityLevel2;


/**
* org/omg/SecurityLevel2/RequiredRightsOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/SecurityLevel2.idl
* 14 Август 2012 г. 21:55:46 EEST
*/


// RequiredRights Interface
public interface RequiredRightsOperations 
{
  void get_required_rights (org.omg.CORBA.Object obj, String operation_name, String interface_name, org.omg.Security.RightsListHolder rights, org.omg.Security.RightsCombinatorHolder rights_combinator);
  void set_required_rights (String operation_name, String interface_name, org.omg.Security.Right[] rights, org.omg.Security.RightsCombinator rights_combinator);
} // interface RequiredRightsOperations
