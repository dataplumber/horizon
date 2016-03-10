/*****************************************************************************
 * Copyright (c) 2010 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.common.httpfetch.api;

import gov.nasa.horizon.common.api.file.FileProduct;
import gov.nasa.horizon.common.api.util.ChecksumUtility.DigestAlgorithm;
import gov.nasa.horizon.common.api.file.FileProductHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Set;

/**
 * This is the HttpFileHandler interface that is dispatched by the OceanDataWalker
 * object.
 *
 * @author Thomas Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public abstract class HttpFileHandler implements FileProductHandler {

   private static Log _logger = LogFactory.getLog(HttpFileHandler.class);

   private HttpFetcher _fetcher;

   public void setHttpFetcher(HttpFetcher fetcher) {
      this._fetcher = fetcher;
   }

   @Override
   public void preprocess() {
      HttpFileHandler._logger.debug("HttpFileHandler initialized.");
   }

   @Override
   public void postprocess() {
      if (this._fetcher != null) {
         this._fetcher.shutdown();
      }
   }

   @Override
   public void onError(Throwable e) {
      HttpFileHandler._logger.error(e.getMessage(), e);
   }
}
