/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.remote.rmi;

import java.io.IOException;
import java.io.NotSerializableException;
import java.rmi.MarshalledObject;
import java.util.Set;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.remote.rmi.RMIConnection;
import javax.security.auth.Subject;

import mx4j.remote.NotificationTuple;
import mx4j.remote.RemoteNotificationClientHandler;

/**
 * An MBeanServerConnection that "converts" the MBeanServerConnection calls to {@link RMIConnection} calls,
 * performing wrapping of parameters and/or the needed actions.
 *
 * @version $Revision: 1.10 $
 * @see mx4j.remote.rmi.RMIConnectionInvoker
 */
public class ClientInvoker implements MBeanServerConnection
{
   private final RMIConnection connection;
   private final Subject delegate;
   private final RemoteNotificationClientHandler notificationHandler;

   public ClientInvoker(RMIConnection rmiConnection, RemoteNotificationClientHandler notificationHandler, Subject delegate)
   {
      this.connection = rmiConnection;
      this.delegate = delegate;
      this.notificationHandler = notificationHandler;
   }

   public void addNotificationListener(ObjectName observed, NotificationListener listener, NotificationFilter filter, Object handback)
           throws InstanceNotFoundException, IOException
   {
      NotificationTuple tuple = new NotificationTuple(observed, listener, filter, handback);
      if (notificationHandler.contains(tuple)) return;

      MarshalledObject f = null;
      try
      {
         f = RMIMarshaller.marshal(filter);
      }
      catch (NotSerializableException x)
      {
         // Invoke the filter on client side
         tuple.setInvokeFilter(true);
      }
      Integer[] ids = connection.addNotificationListeners(new ObjectName[]{observed}, new MarshalledObject[]{f}, new Subject[]{delegate});
      notificationHandler.addNotificationListener(ids[0], tuple);
   }

   public void removeNotificationListener(ObjectName observed, NotificationListener listener)
           throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      Integer[] ids = notificationHandler.getNotificationListeners(new NotificationTuple(observed, listener));
      if (ids == null) throw new ListenerNotFoundException("Could not find listener " + listener);
      try
      {
         connection.removeNotificationListeners(observed, ids, delegate);
         notificationHandler.removeNotificationListeners(ids);
      }
      catch (InstanceNotFoundException x)
      {
         notificationHandler.removeNotificationListeners(ids);
         throw x;
      }
      catch (ListenerNotFoundException x)
      {
         notificationHandler.removeNotificationListeners(ids);
         throw x;
      }
   }

   public void removeNotificationListener(ObjectName observed, NotificationListener listener, NotificationFilter filter, Object handback)
           throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      Integer id = notificationHandler.getNotificationListener(new NotificationTuple(observed, listener, filter, handback));
      if (id == null) throw new ListenerNotFoundException("Could not find listener " + listener + " with filter " + filter + " and handback " + handback);
      Integer[] ids = new Integer[]{id};
      try
      {
         connection.removeNotificationListeners(observed, ids, delegate);
         notificationHandler.removeNotificationListeners(ids);
      }
      catch (InstanceNotFoundException x)
      {
         notificationHandler.removeNotificationListeners(ids);
         throw x;
      }
      catch (ListenerNotFoundException x)
      {
         notificationHandler.removeNotificationListeners(ids);
         throw x;
      }
   }

   public void addNotificationListener(ObjectName observed, ObjectName listener, NotificationFilter filter, Object handback)
           throws InstanceNotFoundException, IOException
   {
      MarshalledObject f = RMIMarshaller.marshal(filter);
      MarshalledObject h = RMIMarshaller.marshal(handback);
      connection.addNotificationListener(observed, listener, f, h, delegate);
   }

   public void removeNotificationListener(ObjectName observed, ObjectName listener)
           throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      connection.removeNotificationListener(observed, listener, delegate);
   }

   public void removeNotificationListener(ObjectName observed, ObjectName listener, NotificationFilter filter, Object handback)
           throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      MarshalledObject f = RMIMarshaller.marshal(filter);
      MarshalledObject h = RMIMarshaller.marshal(handback);
      connection.removeNotificationListener(observed, listener, f, h, delegate);
   }

   public MBeanInfo getMBeanInfo(ObjectName objectName)
           throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException
   {
      return connection.getMBeanInfo(objectName, delegate);
   }

   public boolean isInstanceOf(ObjectName objectName, String className)
           throws InstanceNotFoundException, IOException
   {
      return connection.isInstanceOf(objectName, className, delegate);
   }

   public String[] getDomains()
           throws IOException
   {
      return connection.getDomains(delegate);
   }

   public String getDefaultDomain()
           throws IOException
   {
      return connection.getDefaultDomain(delegate);
   }

   public ObjectInstance createMBean(String className, ObjectName objectName)
           throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
   {
      return connection.createMBean(className, objectName, delegate);
   }

   public ObjectInstance createMBean(String className, ObjectName objectName, Object[] args, String[] parameters)
           throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
   {
      MarshalledObject arguments = RMIMarshaller.marshal(args);
      return connection.createMBean(className, objectName, arguments, parameters, delegate);
   }

   public ObjectInstance createMBean(String className, ObjectName objectName, ObjectName loaderName)
           throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
   {
      return connection.createMBean(className, objectName, loaderName, delegate);
   }

   public ObjectInstance createMBean(String className, ObjectName objectName, ObjectName loaderName, Object[] args, String[] parameters)
           throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
   {
      MarshalledObject arguments = RMIMarshaller.marshal(args);
      return connection.createMBean(className, objectName, loaderName, arguments, parameters, delegate);
   }

   public void unregisterMBean(ObjectName objectName)
           throws InstanceNotFoundException, MBeanRegistrationException, IOException
   {
      connection.unregisterMBean(objectName, delegate);
   }

   public Object getAttribute(ObjectName objectName, String attribute)
           throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException
   {
      return connection.getAttribute(objectName, attribute, delegate);
   }

   public void setAttribute(ObjectName objectName, Attribute attribute)
           throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException
   {
      MarshalledObject attrib = RMIMarshaller.marshal(attribute);
      connection.setAttribute(objectName, attrib, delegate);
   }

   public AttributeList getAttributes(ObjectName objectName, String[] attributes)
           throws InstanceNotFoundException, ReflectionException, IOException
   {
      return connection.getAttributes(objectName, attributes, delegate);
   }

   public AttributeList setAttributes(ObjectName objectName, AttributeList attributes)
           throws InstanceNotFoundException, ReflectionException, IOException
   {
      MarshalledObject attribs = RMIMarshaller.marshal(attributes);
      return connection.setAttributes(objectName, attribs, delegate);
   }

   public Object invoke(ObjectName objectName, String methodName, Object[] args, String[] parameters)
           throws InstanceNotFoundException, MBeanException, ReflectionException, IOException
   {
      MarshalledObject arguments = RMIMarshaller.marshal(args);
      return connection.invoke(objectName, methodName, arguments, parameters, delegate);
   }

   public Integer getMBeanCount()
           throws IOException
   {
      return connection.getMBeanCount(delegate);
   }

   public boolean isRegistered(ObjectName objectName)
           throws IOException
   {
      return connection.isRegistered(objectName, delegate);
   }

   public ObjectInstance getObjectInstance(ObjectName objectName)
           throws InstanceNotFoundException, IOException
   {
      return connection.getObjectInstance(objectName, delegate);
   }

   public Set queryMBeans(ObjectName patternName, QueryExp filter)
           throws IOException
   {
      MarshalledObject query = RMIMarshaller.marshal(filter);
      return connection.queryMBeans(patternName, query, delegate);
   }

   public Set queryNames(ObjectName patternName, QueryExp filter)
           throws IOException
   {
      MarshalledObject query = RMIMarshaller.marshal(filter);
      return connection.queryNames(patternName, query, delegate);
   }
}
