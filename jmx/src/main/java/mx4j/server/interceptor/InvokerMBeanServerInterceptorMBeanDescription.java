/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.server.interceptor;

import mx4j.MBeanDescriptionAdapter;

/**
 * Management interface description for the InvokerMBeanServerInterceptor MBean.
 *
 * @version $Revision: 1.3 $
 */
public class InvokerMBeanServerInterceptorMBeanDescription extends MBeanDescriptionAdapter
{
   public String getMBeanDescription()
   {
      return "The interceptor that invokes on the MBean instance";
   }

   public String getAttributeDescription(String attribute)
   {
      if (attribute.equals("Type"))
      {
         return "The type of this interceptor";
      }
      if (attribute.equals("Enabled"))
      {
         return "This interceptor is always enabled and cannot be disabled";
      }
      return super.getAttributeDescription(attribute);
   }
}
