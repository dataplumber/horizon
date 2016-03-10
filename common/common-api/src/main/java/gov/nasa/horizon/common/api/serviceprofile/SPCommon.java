/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.serviceprofile;

public interface SPCommon {
   enum SPAccessRole {
      PRIVATE, RESTRICTED, PUBLIC
   }

   ;

   enum SPChecksumAlgorithm {
      MD2, MD5, SHA1, SHA256, SHA384, SHA512
   }

   ;

   enum SPCompressionAlgorithm {
      BZIP2, GZIP, ZIP, NONE
   }

   ;

   enum SPDataFormat {
      ASCII, GEOTIFF, GIF, GRIB, HDF, HDF5, HDFEOS, HTML, JPEG, JPG, JGW, JSON,
      KML, LOD, NETCDF, NETCDF4, PGW, PNG, RAW, TEXT, TIFF, XML
   }

   ;

   enum SPDataPass {
      ASCENDING, DESCENDING
   }

   ;

   enum SPDayNight {
      DAY, NIGHT
   }

   ;

   enum SPFileClass {
      DATA, METADATA, CHECKSUM, THUMBNAIL, IMAGE, GEOMETADATA
   }

   enum SPMessageLevel {
      VERBOSE, ERRORONLY, SILENT;

      public static boolean toNotify(SPMessageLevel level, boolean hasError) {
         if (level == VERBOSE)
            return true;
         if (level == ERRORONLY && hasError)
            return true;
         return false;
      }
   }

   ;

   enum SPSubmissionStatus {
      READY, STAGED, REGISTERED, ARCHIVED, ERROR, REJECTED
   }

   ;
}
