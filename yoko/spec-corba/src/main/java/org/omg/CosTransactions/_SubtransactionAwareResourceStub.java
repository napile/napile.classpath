package org.omg.CosTransactions;


/**
* org/omg/CosTransactions/_SubtransactionAwareResourceStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/yoko-1.0/yoko-spec-corba/src/main/idl/CosTransactions.idl
* 14 Август 2012 г. 21:55:46 EEST
*/

public class _SubtransactionAwareResourceStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.CosTransactions.SubtransactionAwareResource
{

  public void commit_subtransaction (org.omg.CosTransactions.Coordinator parent)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("commit_subtransaction", true);
                org.omg.CosTransactions.CoordinatorHelper.write ($out, parent);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                commit_subtransaction (parent        );
            } finally {
                _releaseReply ($in);
            }
  } // commit_subtransaction

  public void rollback_subtransaction ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("rollback_subtransaction", true);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                rollback_subtransaction (        );
            } finally {
                _releaseReply ($in);
            }
  } // rollback_subtransaction

  public org.omg.CosTransactions.Vote prepare () throws org.omg.CosTransactions.HeuristicMixed, org.omg.CosTransactions.HeuristicHazard
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("prepare", true);
                $in = _invoke ($out);
                org.omg.CosTransactions.Vote $result = org.omg.CosTransactions.VoteHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:CosTransactions/HeuristicMixed:1.0"))
                    throw org.omg.CosTransactions.HeuristicMixedHelper.read ($in);
                else if (_id.equals ("IDL:CosTransactions/HeuristicHazard:1.0"))
                    throw org.omg.CosTransactions.HeuristicHazardHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return prepare (        );
            } finally {
                _releaseReply ($in);
            }
  } // prepare

  public void rollback () throws org.omg.CosTransactions.HeuristicCommit, org.omg.CosTransactions.HeuristicMixed, org.omg.CosTransactions.HeuristicHazard
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("rollback", true);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:CosTransactions/HeuristicCommit:1.0"))
                    throw org.omg.CosTransactions.HeuristicCommitHelper.read ($in);
                else if (_id.equals ("IDL:CosTransactions/HeuristicMixed:1.0"))
                    throw org.omg.CosTransactions.HeuristicMixedHelper.read ($in);
                else if (_id.equals ("IDL:CosTransactions/HeuristicHazard:1.0"))
                    throw org.omg.CosTransactions.HeuristicHazardHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                rollback (        );
            } finally {
                _releaseReply ($in);
            }
  } // rollback

  public void commit () throws org.omg.CosTransactions.NotPrepared, org.omg.CosTransactions.HeuristicRollback, org.omg.CosTransactions.HeuristicMixed, org.omg.CosTransactions.HeuristicHazard
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("commit", true);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:CosTransactions/NotPrepared:1.0"))
                    throw org.omg.CosTransactions.NotPreparedHelper.read ($in);
                else if (_id.equals ("IDL:CosTransactions/HeuristicRollback:1.0"))
                    throw org.omg.CosTransactions.HeuristicRollbackHelper.read ($in);
                else if (_id.equals ("IDL:CosTransactions/HeuristicMixed:1.0"))
                    throw org.omg.CosTransactions.HeuristicMixedHelper.read ($in);
                else if (_id.equals ("IDL:CosTransactions/HeuristicHazard:1.0"))
                    throw org.omg.CosTransactions.HeuristicHazardHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                commit (        );
            } finally {
                _releaseReply ($in);
            }
  } // commit

  public void commit_one_phase () throws org.omg.CosTransactions.HeuristicHazard
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("commit_one_phase", true);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:CosTransactions/HeuristicHazard:1.0"))
                    throw org.omg.CosTransactions.HeuristicHazardHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                commit_one_phase (        );
            } finally {
                _releaseReply ($in);
            }
  } // commit_one_phase

  public void forget ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("forget", true);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                forget (        );
            } finally {
                _releaseReply ($in);
            }
  } // forget

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:CosTransactions/SubtransactionAwareResource:1.0", 
    "IDL:CosTransactions/Resource:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _SubtransactionAwareResourceStub
