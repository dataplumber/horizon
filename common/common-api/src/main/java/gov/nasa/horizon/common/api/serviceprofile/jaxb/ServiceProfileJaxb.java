/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.Agent;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.Message;
import gov.nasa.horizon.common.api.jaxb.serviceprofile.Submission;
import gov.nasa.horizon.common.api.serviceprofile.*;
import gov.nasa.horizon.common.api.util.Binder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

public class ServiceProfileJaxb extends Binder<Message> implements Accessor,
      ServiceProfile {

   private static Log _logger = LogFactory.getLog(ServiceProfileJaxb.class);
   private static final String SCHEMA_ENV = "common.config.schema";
   private static final String SIP_SCHEMA = "horizon_message.xsd";
   private static final String JAXB_CONTEXT =
         "gov.nasa.horizon.common.api.jaxb.serviceprofile";
   private static URL _schemaUrl;

   static {
      if (System.getProperty(ServiceProfileJaxb.SCHEMA_ENV) != null) {
         File schemaFile =
               new File(System.getProperty(ServiceProfileJaxb.SCHEMA_ENV)
                     + File.separator + ServiceProfileJaxb.SIP_SCHEMA);
         if (schemaFile.exists()) {
            try {
               ServiceProfileJaxb._schemaUrl = schemaFile.toURL();
            } catch (MalformedURLException e) {
               if (ServiceProfileJaxb._logger.isDebugEnabled())
                  e.printStackTrace();
            }
         }
      }

      if (ServiceProfileJaxb._schemaUrl == null) {
         ServiceProfileJaxb._schemaUrl =
               ServiceProfileJaxb.class.getResource("/META-INF/schemas/"
                     + ServiceProfileJaxb.SIP_SCHEMA);
      }
   }

   public static synchronized ServiceProfile createServiceProfile()
         throws ServiceProfileException {
      ServiceProfileJaxb profile = null;

      try {
         profile = new ServiceProfileJaxb();
         profile._setJaxbObj(new Message());
      } catch (JAXBException e) {
         if (ServiceProfileJaxb._logger.isDebugEnabled())
            e.printStackTrace();
         throw new ServiceProfileException(e.getMessage());
      }
      return profile;
   }

   public static synchronized ServiceProfile loadServiceProfile(Reader reader)
         throws ServiceProfileException {
      ServiceProfileJaxb profile = null;
      try {
         profile =
               new ServiceProfileJaxb(ServiceProfileJaxb.JAXB_CONTEXT,
                     ServiceProfileJaxb._schemaUrl, reader);
      } catch (JAXBException e) {
         ServiceProfileJaxb._logger.error(e.getMessage());
         ServiceProfileJaxb._logger.debug(e.getMessage(), e);
         ServiceProfileJaxb._logger.error(e.getCause().getMessage());
         throw new ServiceProfileException(e.getMessage());
      } catch (SAXException e) {
         ServiceProfileJaxb._logger.error(e.getMessage());
         ServiceProfileJaxb._logger.debug(e.getMessage(), e);
         throw new ServiceProfileException(e.getMessage());
      } catch (ParserConfigurationException e) {
         ServiceProfileJaxb._logger.error(e.getMessage());
         ServiceProfileJaxb._logger.debug(e.getMessage(), e);
         throw new ServiceProfileException(e.getMessage());
      }
      return profile;
   }

   private Object _owner = null;

   protected ServiceProfileJaxb() throws JAXBException {
      super(ServiceProfileJaxb.JAXB_CONTEXT);
   }

   protected ServiceProfileJaxb(String contextpath, URL schemaUrl, Reader reader)
         throws JAXBException, SAXException, ParserConfigurationException {
      super(contextpath, schemaUrl, reader, true);
   }

   public SPAgent createAgent() {
      return new AgentJaxb();
   }


   @Override
   public SPSubmission createSubmission() {
      return new SubmissionJaxb();
   }

   @Override
   public void setSubmisson(SPSubmission submission) {
      if (submission == null) return;
      if (submission.getImplObj() instanceof Submission) {
         this._getJaxbObj().setSubmission((Submission) submission.getImplObj());
      } else {
         SubmissionJaxb s = new SubmissionJaxb(submission);
         this._getJaxbObj().setSubmission((Submission) submission.getImplObj());
      }
   }

   @Override
   public SPSubmission getSubmission() {
      SPSubmission result = null;
      if (this._getJaxbObj().getSubmission() != null) {
         result = new SubmissionJaxb((Submission) (this._getJaxbObj()
               .getSubmission()));
         result.setOwner(this);
      }
      return result;
   }

   public Object getImplObj() {
      return this._getJaxbObj();
   }

   public SPAgent getMessageOriginAgent() {
      SPAgent result = null;
      if (this._getJaxbObj().getOrigin() != null) {
         result = new AgentJaxb(this._getJaxbObj().getOrigin());
         result.setOwner(this);
      }
      return result;
   }

   public SPAgent getMessageTargetAgent() {
      SPAgent result = null;
      if (this._getJaxbObj().getOrigin() != null) {
         result = new AgentJaxb(this._getJaxbObj().getTarget());
         result.setOwner(this);
      }
      return result;

   }

   public Object getOwner() {
      return this._owner;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_owner == null) ? 0 : _owner.hashCode());
      return result;
   }

   public void setMessageOriginAgent(SPAgent agent) {
      if (agent == null) return;
      SPAgent temp = agent;
      if (!(temp.getImplObj() instanceof Agent)) {
         temp = new AgentJaxb(agent);
      }
      this._getJaxbObj().setOrigin((Agent) temp.getImplObj());
   }

   public void setMessageTargetAgent(SPAgent agent) {
      if (agent == null) return;
      SPAgent temp = agent;
      if (!(temp.getImplObj() instanceof Agent)) {
         temp = new AgentJaxb(agent);
      }
      this._getJaxbObj().setTarget((Agent) temp.getImplObj());
   }

   public void setOwner(Object owner) {
      this._owner = owner;
   }

   public void toFile(String filename) throws ServiceProfileException,
         IOException {
      try {
         Binder.toFile(this._getJaxbObj(), this._getMarshaller(), filename);
      } catch (JAXBException e) {
         throw new ServiceProfileException(e);
      } catch (SAXException e) {
         throw new ServiceProfileException(e);
      }
   }

   @Override
   public String toString() {
      String result = null;
      try {
         result = Binder.toString(this._getJaxbObj(), this._getMarshaller());
      } catch (Exception e) {
         ServiceProfileJaxb._logger.error("Unable to transform object to XML.",
               e);
      }
      return result;
   }
}
