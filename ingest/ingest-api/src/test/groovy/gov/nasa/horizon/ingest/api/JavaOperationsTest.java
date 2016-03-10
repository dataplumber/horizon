package gov.nasa.horizon.ingest.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.Ignore;

import java.util.List;

import gov.nasa.horizon.ingest.api.protocol.ProtocolException;


/**
 * Created by IntelliJ IDEA. User: thuang Date: Sep 14, 2008 Time: 11:43:53 PM To change this template use File |
 * Settings | File Templates.
 */

public class JavaOperationsTest {
   private static Log log = LogFactory.getLog(JavaOperationsTest.class);

   @Ignore @Test public void testAdd() {
      Session session = null;
      try {
         String productType = "MORCR143LLDY_SRC]"; //"GHRSST_L2P_GOES11";
         String domainFile =
               System.getProperty(Constants.getPROP_DOMAIN_FILE());
         session = new Session(domainFile);
         session.open();
         session.setCurrentDir("/Users/thuang/Development/work/shared/gibs/horizon/ingest/ingest-api/src/test/resources/MORCR143LLDY_SRC");
         session.add(productType, "RRGlobal_r01c34.2013251.aqua.250m.xml");

         while (session.getTransactionCount() > 0) {
            log.info ("transaction count: " + session.getTransactionCount());
            Result result = session.result(0);
            if (result != null) {
               log.info(result.getDescription());
            }
         }

      } catch (SessionException e) {
         log.error(e.getMessage(), e);
      } finally {
         if (session != null) {
            try {
               session.close();
            } catch (SessionException e) {
               log.error(e.getMessage(), e);
            }
         }
      }
   }


   @Test public void testList() {
      Session session = null;
      try {
         String productType = "MYRCR143LLDY_SRC";
         String domainFile =
               System.getProperty(Constants.getPROP_DOMAIN_FILE());
         session = new Session(domainFile);
         session.open();
         session.list(productType, new String[]{"RRGlobal_r01c09.2013251.terra.250m", "RRGlobal_r01c12.2013251.terra.250m", "RRGlobal_r01c16.2013251.terra.250m"});

         while (session.getTransactionCount() > 0) {
            log.info ("transaction count: " + session.getTransactionCount());
            Result result = session.result(0);
            if (result != null) {
               log.info("Errno=" + result.getErrno() + "  Description:" + result.getDescription());
               List<Product> products = (List<Product>)result.getProducts();
               for (Product product : products) {
                  log.info(product.toString());
               }
            }
         }
      } catch (ProtocolException e) {

      } catch (SessionException e) {
         log.error(e.getMessage(), e);
      } finally {
         if (session != null) {
            try {
               session.close();
            } catch (SessionException e) {
               log.error(e.getMessage(), e);
            }
         }
      }
   }
}
