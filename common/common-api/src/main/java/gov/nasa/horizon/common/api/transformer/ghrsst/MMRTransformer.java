/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.transformer.ghrsst;

import gov.nasa.horizon.common.api.jaxb.mmr.MMRFR;
import gov.nasa.horizon.common.api.jaxb.mmr.MMRFR.Personnel;
import gov.nasa.horizon.common.api.serviceprofile.*;
import gov.nasa.horizon.common.api.serviceprofile.SPCommon.*;
import gov.nasa.horizon.common.api.transformer.Transformer;
import gov.nasa.horizon.common.api.transformer.TransformerAux;
import gov.nasa.horizon.common.api.util.Binder;
import gov.nasa.horizon.common.api.util.URIPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.cache.DefaultFilesCache;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.TrustEveryoneUserInfo;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Implementation of MMR to ServiceProfile transformer.
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public class MMRTransformer extends Binder<MMRFR> implements Transformer,
      TransformerAux {

   enum FILE_ENTRY {
      NAME, USER, PASSWORD;
   }

   public enum FILE_TYPE {
      RAW, MMR, RAW_CHECKSUM, MMR_CHECKSUM
   }

   /**
    * Internal class use for 'crawling' the target file system to look for files that matches the MMR requirement.
    *
    * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
    * @version $Id:$
    */
   protected class MMRFileSearcher {
      private String[] _searchPaths;

      public MMRFileSearcher(String[] searchPaths) {
         this._searchPaths = searchPaths;
      }

      /**
       * Method to 'crawl' the target search paths to look for the input possible names.
       *
       * @param possibleFilenames list of possible names
       * @return a URI to the file found.
       */
      public synchronized String findFile(final String[] possibleFilenames) {
         String result = null;

         List<FileObject> paths = new LinkedList<FileObject>();

         for (String searchPath : this._searchPaths) {
            try {
               FileObject fo = MMRTransformer._getFileObject(searchPath);
               paths.add(fo);
            } catch (FileSystemException e) {
               if (MMRTransformer._logger.isDebugEnabled())
                  e.printStackTrace();
               MMRTransformer._logger.error(e.getMessage());
            }
         }

         for (FileObject dirObj : paths) {

            MMRTransformer._logger.trace("looking at dir: "
                  + dirObj.getName().getFriendlyURI() + ".");

            FileObject[] files = null;
            try {
               files = dirObj.findFiles(new FileSelector() {

                  private boolean _found = false;

                  public boolean includeFile(FileSelectInfo fileInfo)
                        throws Exception {
                     if (this._found)
                        return false;
                     FileObject fileObj = fileInfo.getFile();
                     if (fileObj.getType() != FileType.FILE)
                        return false;
                     MMRTransformer._logger.trace("Looking at file: "
                           + fileObj.getName().getFriendlyURI());
                     if (!fileObj.isReadable()) {
                        MMRTransformer._logger
                              .trace("File is not readable.  Reject this file.");
                        return false;
                     }

                     boolean match = false;
                     for (String possible : possibleFilenames) {
                        MMRTransformer._logger.trace("Compare file: "
                              + fileObj.getName().getBaseName() + " against "
                              + possible);
                        if (fileObj.getName().getBaseName().matches(possible)) {
                           match = true;
                           break;
                        }
                     }
                     this._found = match;
                     return match;
                  }

                  public boolean traverseDescendents(FileSelectInfo fileInfo)
                        throws Exception {
                     if (this._found)
                        return false;
                     FileObject fileObj = fileInfo.getFile();
                     if (fileObj.getType() == FileType.FILE)
                        return false;
                     else if (fileObj.getType() == FileType.FOLDER)
                        return true;
                     else if (fileObj.getType() == FileType.IMAGINARY)
                        return false;
                     else
                        return false;
                  }
               });
               MMRTransformer._logger.trace("Done filtering");
            } catch (FileSystemException e) {
               if (MMRTransformer._logger.isDebugEnabled())
                  e.printStackTrace();
               MMRTransformer._logger.debug("Unable to search for file sets.  "
                     + e.getMessage());
            }

            if (files != null && files.length > 0) {
               String fileuri = files[0].getName().getFriendlyURI();
               try {
                  URIPath uPath = URIPath.createURIPath(fileuri);
                  result = uPath.getURI();
                  MMRTransformer._logger.trace("file URI: " + result);
               } catch (URISyntaxException e) {
                  if (MMRTransformer._logger.isDebugEnabled()) {
                     e.printStackTrace();
                  }
                  MMRTransformer._logger.error("Unabled to register file URI: "
                        + fileuri + ".");
               }
               break;
            }
         }

         for (FileObject dirObj : paths) {
            try {
               dirObj.close();
            } catch (FileSystemException e) {
               if (MMRTransformer._logger.isDebugEnabled())
                  e.printStackTrace();
               MMRTransformer._logger.error("Unable to close connection to "
                     + dirObj.getName().getFriendlyURI());
            }
         }
         return result;
      }

   }

   private static Log _logger = LogFactory.getLog(MMRTransformer.class);
   private static StandardFileSystemManager _manager =
         new StandardFileSystemManager();
   private static final String MMR_FR_SCHEMA = "mmr_fr.xsd";

   private static final String SCHEMA_ENV = "common.config.schema";
   private static final String JAXB_CONTEXT =
         "gov.nasa.horizon.common.api.jaxb.mmr";
   private static URL _schemaUrl = null;
   private static Hashtable<String, URIPath> _auth =
         new Hashtable<String, URIPath>();

   static {
      if (System.getProperty(MMRTransformer.SCHEMA_ENV) != null) {
         File schemaFile =
               new File(System.getProperty(MMRTransformer.SCHEMA_ENV)
                     + File.separator + MMRTransformer.MMR_FR_SCHEMA);
         if (schemaFile.exists()) {
            try {
               MMRTransformer._schemaUrl = schemaFile.toURL();
            } catch (MalformedURLException e) {
               if (MMRTransformer._logger.isDebugEnabled())
                  e.printStackTrace();
            }
         }
      }

      if (MMRTransformer._schemaUrl == null) {
         MMRTransformer._schemaUrl =
               MMRTransformer.class.getResource("/META-INF/schemas/"
                     + MMRTransformer.MMR_FR_SCHEMA);
      }

      MMRTransformer._manager = new StandardFileSystemManager();
      try {
         MMRTransformer._manager.setFilesCache(new DefaultFilesCache());
         MMRTransformer._manager.init();
      } catch (FileSystemException e) {
         if (MMRTransformer._logger.isDebugEnabled())
            e.printStackTrace();
         MMRTransformer._logger
               .error("Unable to initialize file system manager.");
      }
   }

   /**
    * Utility internal method to establish physical connection to the target URI.
    *
    * @param uri the input URI to the file
    * @return the FileObject represent a physical connection to the file
    * @throws FileSystemException when unable to establish connection.
    */
   private static FileObject _getFileObject(String uri)
         throws FileSystemException {
      FileObject result = null;
      URIPath authToken = null;

      URIPath path = null;
      try {
         path = URIPath.createURIPath(uri);
      } catch (URISyntaxException e) {
         if (MMRTransformer._logger.isDebugEnabled())
            e.printStackTrace();
         MMRTransformer._logger.error(e.getMessage());
      }
      String hostUri = path.getHostURI();
      if (hostUri != null) {
         authToken = MMRTransformer._auth.get(hostUri);
      }

      if (authToken != null) {
         MMRTransformer._logger.trace("Found auth info: "
               + authToken.getHostURI() + " " + authToken.getUser() + " "
               + authToken.getPassword());
         FileSystemOptions opts = new FileSystemOptions();
         if (uri.startsWith("sftp")) {
            try {
               SftpFileSystemConfigBuilder.getInstance()
                     .setStrictHostKeyChecking(opts, "no");
               SftpFileSystemConfigBuilder.getInstance().setUserInfo(opts,
                     new TrustEveryoneUserInfo());
            } catch (FileSystemException e) {
               MMRTransformer._logger.error(e.getMessage());
               if (MMRTransformer._logger.isDebugEnabled()) {
                  e.printStackTrace();
               }
               throw e;
            }
         } else if (uri.startsWith("ftp")) {
            FtpFileSystemConfigBuilder.getInstance().setPassiveMode(opts, true);
         }

         StaticUserAuthenticator auth =
               new StaticUserAuthenticator(null, authToken.getUser(), authToken
                     .getPassword());
         DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(
               opts, auth);
         result = MMRTransformer._manager.resolveFile(uri, opts);
      } else {
         result = MMRTransformer._manager.resolveFile(uri);
      }
      return result;
   }

   private String _contributorEmailAddress = null;
   private SPMessageLevel _contributorMessageLevel = SPMessageLevel.VERBOSE;
   private String _rootURI = null;

   // default search path is the user's current working directory
   private String[] _searchPaths =
         {"file://" + System.getProperty("user.dir")};

   // flag to keep the file path in the result URI.
   private boolean _keepPath = false;

   // flag to indicate the user has decided to build their own file set list.
   // if this flag is true, then the cralwer will not be used.
   private boolean _manualFileSet = false;

   // table to keep track of the file sets. For MMR, this file should have 4
   // entries.
   private Hashtable<FILE_TYPE, URIPath> _fileSet =
         new Hashtable<FILE_TYPE, URIPath>();

   protected MMRTransformer() {
      super(MMRTransformer.JAXB_CONTEXT, MMRTransformer._schemaUrl, true);
   }

   /**
    * Constructor to set the contributor of the final SIP created by the transformer.
    *
    * @param contributor the contributor email address
    * @throws MMRException when unable to initialize XML binding for the MMR schema.
    */
   public MMRTransformer(String contributor) throws MMRException {
      this();
      this.setContributorEmailAddress(contributor);
   }

   /**
    * Constructor to set the contributor of the final SIP created by the transformer and the input root URI will be used
    * to replace any file:// scheme file URI.
    *
    * @param contributor the contributor email address
    * @param rootURI     the root URI used to replace any file:// scheme file URI.
    * @throws MMRException when unable to initialize XML binding for the MMR schema.
    */
   public MMRTransformer(String contributor, String rootURI)
         throws MMRException {
      this();
      this.setContributorEmailAddress(contributor);
      this.setRootURI(rootURI);
   }

   /**
    * Internal method to build the file set using the input granule name. This method is designed according to GHRSST
    * file naming conventions.
    *
    * @param granulename the input granule name
    * @throws MMRException when unable to build file set.
    */
   protected synchronized void _buildFileSet(String granulename)
         throws MMRException {
      MMRFileSearcher searcher = new MMRFileSearcher(this._searchPaths);
      String fq_rawFilename =
            searcher.findFile(new String[]{granulename + ".gz",
                  granulename + ".bz2"});
      if (fq_rawFilename == null) {
         throw new MMRException(
               "Unable to locate RAW data file for granule name: "
                     + granulename + ".  Please check your search path: "
                     + this._searchPaths[0]);
      }

      try {
         this._fileSet
               .put(FILE_TYPE.RAW, URIPath.createURIPath(fq_rawFilename));
      } catch (URISyntaxException e) {
         if (MMRTransformer._logger.isDebugEnabled()) {
            e.printStackTrace();
         }
         throw new MMRException("Unable to register RAW file: "
               + fq_rawFilename + ".");
      }

      String rawFilename =
            fq_rawFilename.substring(
                  fq_rawFilename.lastIndexOf(File.separator) + 1,
                  fq_rawFilename.length());

      String rawChecksumFilename =
            searcher.findFile(new String[]{rawFilename + ".md5"});
      if (rawChecksumFilename == null) {
         throw new MMRException(
               "Unable to locate checksum file for RAW data file: "
                     + rawFilename + ".  Please check your search path.");
      }

      MMRTransformer._logger.trace("Found MD5 file: " + rawChecksumFilename);

      try {
         this._fileSet.put(FILE_TYPE.RAW_CHECKSUM, URIPath
               .createURIPath(rawChecksumFilename));
      } catch (URISyntaxException e) {
         if (MMRTransformer._logger.isDebugEnabled()) {
            e.printStackTrace();
         }
         throw new MMRException("Unable to register RAW_CHECKSUM file: "
               + rawChecksumFilename + ".");
      }

      // the .nc extension is part of the RAW file name, but it is dropped
      // when naming the checksum files
      String new_granulename = granulename;
      if (granulename.endsWith(".nc"))
         new_granulename =
               granulename.substring(0, granulename.lastIndexOf(".nc"));

      String fq_mmrFilename =
            searcher
                  .findFile(new String[]{"FR-" + new_granulename + ".xml"});
      if (fq_mmrFilename == null) {
         throw new MMRException("Unable to locate MMR file for granule name: "
               + granulename + ".  Please check your search path.");
      }

      try {
         this._fileSet
               .put(FILE_TYPE.MMR, URIPath.createURIPath(fq_mmrFilename));
      } catch (URISyntaxException e) {
         if (MMRTransformer._logger.isDebugEnabled()) {
            e.printStackTrace();
         }
         throw new MMRException("Unable to register MMR file: "
               + fq_mmrFilename + ".");
      }

      String mmrFilename =
            fq_mmrFilename.substring(
                  fq_mmrFilename.lastIndexOf(File.separator) + 1,
                  fq_mmrFilename.length());
      String mmrChecksumFilename =
            searcher.findFile(new String[]{mmrFilename + ".md5"});
      if (mmrChecksumFilename == null) {
         throw new MMRException(
               "Unable to locate MMR checksum file for granule name: "
                     + granulename + ".  Please check your search path.");
      }

      try {
         this._fileSet.put(FILE_TYPE.MMR_CHECKSUM, URIPath
               .createURIPath(mmrChecksumFilename));
      } catch (URISyntaxException e) {
         if (MMRTransformer._logger.isDebugEnabled()) {
            e.printStackTrace();
         }
         throw new MMRException("Unable to register MMR_CHECKSUM file: "
               + mmrChecksumFilename + ".");
      }
   }

   /**
    * Method to determine the file compression according to the input file name extension.
    *
    * @param filename the file name to be examined
    * @return the compression algorithm
    */
   protected synchronized SPCompressionAlgorithm _getCompression(String
                                                                       filename) {

      Hashtable<String, SPCompressionAlgorithm> table =
            new Hashtable<String, SPCompressionAlgorithm>();
      table.put("GZIP", SPCompressionAlgorithm.GZIP);
      table.put("BZIP2", SPCompressionAlgorithm.BZIP2);
      table.put("ZIP", SPCompressionAlgorithm.ZIP);

      // for GHRSST - the file extension is used to decide the compression type
      return table.get(filename.substring(filename.lastIndexOf('.') + 1,
            filename.length()).toUpperCase());
   }

   /**
    * Method to retrieve file content into a String. This method is mainly used for retrieving the checksum data from
    * the checksum file.
    *
    * @param fileentry the URIPath object for the target file.
    * @return the content of the input file entry.
    * @throws IOException when unable to retrieve data from the file entry.
    */
   protected synchronized String _getContents(URIPath fileentry)
         throws IOException {
      String content = null;
      FileObject file = null;
      BufferedInputStream bis = null;

      try {
         file = MMRTransformer._getFileObject(fileentry.getURI());
         bis =
               new BufferedInputStream(file.getContent().getInputStream());
         byte[] buffer = new byte[4092];
         int bytesRead = 0;
         StringBuffer sbuf = new StringBuffer();
         while ((bytesRead = bis.read(buffer, 0, 4092)) > -1) {
            sbuf.append(new String(buffer, 0, bytesRead));
         }
         content = sbuf.toString();
      } catch (FileSystemException e) {
         if (MMRTransformer._logger.isDebugEnabled())
            e.printStackTrace();
         throw new IOException("Unabled to connection file: "
               + fileentry.getURI() + ".");
      } catch (IOException e) {
         if (MMRTransformer._logger.isDebugEnabled())
            e.printStackTrace();
         throw new IOException("Unabled to read file: " + fileentry.getURI()
               + ".");
      } finally {
         if (bis != null) {
            try {
               bis.close();
            } catch (IOException e) {
               MMRTransformer._logger.debug(e);
               MMRTransformer._logger.error("Unable to close stream for file: " + fileentry.getURI() + ".");
            }
         }
         if (file != null) {
            try {
               file.close();
            } catch (FileSystemException e) {
               MMRTransformer._logger.debug(e);
               MMRTransformer._logger.error("Unable to close file: "
                     + fileentry.getURI() + ".");
            }
         }
      }
      return content;
   }

   /**
    * Method to parse MMR date string into Java Data object
    *
    * @param mmrDate MMR date string
    * @return Java Date object or null
    */
   private Date _getDate(String mmrDate) {
      Date date = null;

      try {
         SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
         date = sdf.parse(mmrDate);
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      return date;
   }

   /**
    * Method to process GranuleFile object. It is a utility method to help build the internals of a GranuleFile.
    *
    * @param productFile  the GranuleFile to be updated
    * @param dataFile     the data file
    * @param checksumFile the associated checksum file
    * @throws MMRException when unable to update the GranuleFile object
    */
   protected void _processGranuleFile(SPProductFile productFile,
                                      URIPath dataFile,
                                      URIPath checksumFile) throws MMRException {
      if (dataFile == null)
         return;

      String link = dataFile.getURI();
      FileObject fileObj = null;
      long size = 0;
      MMRTransformer._logger.debug("Trying to determine the size of: " + link);
      try {
         fileObj = this._getFileObject(link);
         size = fileObj.getContent().getSize();
      } catch (FileSystemException e) {
         MMRTransformer._logger.debug(e.getMessage(), e);
         throw new MMRException(e);
      } finally {
         if (fileObj != null)
            try {
               fileObj.close();
            } catch (FileSystemException e) {
               MMRTransformer._logger.debug(e.getMessage(), e);
            }
      }

      URIPath uriPath;
      try {
         uriPath = URIPath.createURIPath(link);
      } catch (URISyntaxException e) {
         MMRTransformer._logger.error("Invalid URI found: " + link + ".", e);
         throw new MMRException(e);
      }
      if (this._keepPath) {
         link =
               this._rootURI
                     + FileName.SEPARATOR
                     + uriPath.getPath();
      } else {
         link =
               this._rootURI
                     + FileName.SEPARATOR
                     + uriPath.getFilename();
      }

      MMRTransformer._logger.trace("Register link: " + link);
      productFile.getFile().addLink(link);

      productFile.getFile().setSize(size);

      SPCompressionAlgorithm comp = this._getCompression(dataFile.getURI());
      if (comp != null) {
         productFile.getFile().setCompressionType(comp);
      }

      if (checksumFile != null) {
         try {
            String checksum = this._getContents(checksumFile);
            SPChecksumAlgorithm alg =
                  SPChecksumAlgorithm.valueOf(checksumFile.getURI().substring(
                        checksumFile.getURI().lastIndexOf('.') + 1,
                        checksumFile.getURI().length()).toUpperCase());
            if (checksum != null && alg != null) {
               productFile.getFile().setChecksumValue(checksum.substring(0,
                     checksum.indexOf(' ')));
               productFile.getFile().setChecksumType(alg);
            }
         } catch (IOException e) {
            if (MMRTransformer._logger.isDebugEnabled())
               e.printStackTrace();
            throw new MMRException(
                  "Unable to read checksum file data from file: "
                        + checksumFile + ".");
         }
      }
   }

   /**
    * Internal transform method working with the internal JAXB object.
    *
    * @param jaxbObj the JAXB object
    * @return the ServerProfile translated from the input JAXB object.
    * @throws MMRException when unable to perform the translation.
    */
   protected synchronized ServiceProfile _transform(MMRFR jaxbObj)
         throws MMRException {
      ServiceProfile profile = null;
      try {
         profile = ServiceProfileFactory.getInstance().createServiceProfile();

         SPSubmission submission = profile.createSubmission();
         profile.setSubmisson(submission);

         SPAgent origin = profile.createAgent();
         origin.setName(this.getClass().getName());

         SPAgent target = profile.createAgent();
         target.setName("ALL");

         profile.setMessageOriginAgent(origin);
         profile.setMessageTargetAgent(target);

         List<Personnel> personnel = jaxbObj.getPersonnel();
         for (Personnel person : personnel) {
            SPNotification notify = submission.createNotification();
            notify.setRole(person.getRole());
            notify.setAddress(person.getAddress());
            notify.setEmail(person.getEmail());
            notify.setFax(person.getFax());
            notify.setFirstName(person.getFirstName());
            notify.setLastName(person.getLastName());
            notify.setMessageLevel(SPMessageLevel.SILENT);
            notify.setPhone(person.getPhone());

            submission.addNotification(notify);
         }

         SPIngest ingestProfile = submission.createIngest();
         submission.setIngest(ingestProfile);

         SPHeader header = submission.createHeader();
         submission.setHeader(header);

         SPMetadata metadata = submission.createMetadata();
         submission.setMetadata(metadata);


         metadata.setBatch(jaxbObj.getEntryID() + "_"
               + System.currentTimeMillis());

         SPOperation operation = header.createOperation();
         header.addOperation(operation);
         operation.setAgent("GHRSST_HANDLER");
         operation.setOperation("REQUEST");
         operation.setOperationStartTime(System.currentTimeMillis());

         //header.setRequested(System.currentTimeMillis());
         header.setStatus(SPSubmissionStatus.READY);

         String gname = jaxbObj.getFileName().trim();
         header.setProductType(jaxbObj.getEntryID().trim());
         header.setProductName(gname);
         header.setVersion(jaxbObj.getFileVersion().trim());
         header.setCreateTime(this._getDate(jaxbObj.getFileReleaseDate()));

         metadata.setProductStartTime(this._getDate(jaxbObj
               .getTemporalCoverage().getStartDate()).getTime());
         metadata.setProductStopTime(this._getDate(jaxbObj
               .getTemporalCoverage().getStopDate()).getTime());
         if (metadata.getProductStartTime().getTime() < 0 ||
               metadata.getProductStartTime().getTime() > metadata
                     .getProductStopTime().getTime()) {
            throw new MMRException("Invalid temporal coverage values (" + jaxbObj
                  .getTemporalCoverage().getStartDate() + " - " + jaxbObj
                  .getTemporalCoverage().getStopDate() + ").");
         }

         SPBoundingRectangle boundingRectangle =
               metadata.createBoundingRectangle();
         metadata.addBoundingRectangle(boundingRectangle);
         boundingRectangle.setSouthLatitude(Float.parseFloat(jaxbObj
               .getSpatialCoverage().getSouthernmostLatitude().trim()));
         boundingRectangle.setNorthLatitude(Float.parseFloat(jaxbObj
               .getSpatialCoverage().getNorthernmostLatitude().trim()));
         boundingRectangle.setWestLongitude(Float.parseFloat(jaxbObj
               .getSpatialCoverage().getWesternmostLongitude().trim()));
         boundingRectangle.setEastLongitude(Float.parseFloat(jaxbObj
               .getSpatialCoverage().getEasternmostLongitude().trim()));

         MMRFR.MetadataHistory frHistory = jaxbObj.getMetadataHistory();
         if (frHistory != null) {
            SPProductHistory history = metadata.createProductHistory();
            metadata.setProductHistory(history);

            history.setCreateDate(this._getDate(frHistory.getFRCreationDate()
            ).getTime());
            history.setLastRevisionDate(this._getDate(frHistory
                  .getFRLastRevisionDate()).getTime());
            history.setRevisionHistory(frHistory.getFRRevisionHistory());
            history.setVersion(frHistory.getFRFileVersion());

         }

         if (!this._manualFileSet) {
            this._buildFileSet(jaxbObj.getFileName());
         }

         SPIngestProductFile gfile = ingestProfile.createIngestProductFile();
         ingestProfile.addIngestProductFile(gfile);
         SPProductFile productFile = gfile.createProductFile();
         gfile.setProductFile(productFile);

         // process RAW file
         URIPath rawFile = this._fileSet.get(FILE_TYPE.RAW);
         if (rawFile == null) {
            throw new MMRException("RAW data file is missing.");
         }
         URIPath checksumFile = this._fileSet.get(FILE_TYPE.RAW_CHECKSUM);
         if (checksumFile == null) {
            throw new MMRException("CHECKSUM file is missing for RAW file: "
                  + rawFile.getURI() + ".");
         }

         SPFile file = productFile.createFile();
         productFile.setFile(file);
         file.setName(jaxbObj.getFileName());
         this._processGranuleFile(productFile, rawFile, checksumFile);
         productFile.setFileType(SPFileClass.DATA);


         // process RAW checksum file
         if (checksumFile.getFilename() == null) {
            throw new MMRException("URI contains no file name.");
         }
         gfile = ingestProfile.createIngestProductFile();
         ingestProfile.addIngestProductFile(gfile);
         productFile = gfile.createProductFile();
         gfile.setProductFile(productFile);
         file = productFile.createFile();
         productFile.setFile(file);
         file.setName(checksumFile.getFilename());
         this._processGranuleFile(productFile, checksumFile, null);
         productFile.setFileType(SPFileClass.CHECKSUM);


         //process MMR file
         URIPath mmrFile = this._fileSet.get(FILE_TYPE.MMR);
         if (mmrFile == null) {
            throw new MMRException("MMR file is missing.");
         }
         URIPath mmrChecksumFile = this._fileSet.get(FILE_TYPE.MMR_CHECKSUM);
         if (mmrChecksumFile == null) {
            throw new MMRException("CHECKSUM file is missing for MMR file: "
                  + mmrFile.getURI() + ".");
         }
         if (mmrFile.getFilename() == null) {
            throw new MMRException("URI contains no file name.");
         }
         gfile = ingestProfile.createIngestProductFile();
         ingestProfile.addIngestProductFile(gfile);
         productFile = gfile.createProductFile();
         gfile.setProductFile(productFile);
         file = productFile.createFile();
         productFile.setFile(file);
         file.setName(mmrFile.getFilename());
         this._processGranuleFile(productFile, mmrFile, mmrChecksumFile);
         productFile.setFileType(SPFileClass.METADATA);

         // process MMR checksum file
         if (mmrChecksumFile.getFilename() == null) {
            throw new MMRException("URI contains no file name.");
         }
         gfile = ingestProfile.createIngestProductFile();
         ingestProfile.addIngestProductFile(gfile);
         productFile = gfile.createProductFile();
         gfile.setProductFile(productFile);
         file = productFile.createFile();
         productFile.setFile(file);
         file.setName(mmrChecksumFile.getFilename());
         this._processGranuleFile(productFile, mmrChecksumFile, null);
         productFile.setFileType(SPFileClass.CHECKSUM);
      } catch (ServiceProfileException e) {
         throw new MMRException(e.getMessage());
      }

      return profile;
   }

   protected ServiceProfile _transform(Reader reader)
         throws ServiceProfileException {
      MMRFR jaxbObj = null;

      try {
         jaxbObj =
               Binder._load(this._getXMLReader(), this._getUnmarshaller(),
                     reader);
      } catch (JAXBException e) {
         if (MMRTransformer._logger.isDebugEnabled())
            e.printStackTrace();
         jaxbObj = null;
      } catch (SAXException e) {
         if (MMRTransformer._logger.isDebugEnabled())
            e.printStackTrace();
         String msg = e.getMessage();
         jaxbObj = null;
      } finally {
         try {
            reader.close();
         } catch (IOException e) {
         }
      }
      if (jaxbObj == null)
         throw new ServiceProfileException("Failed to unmarshall input source.");
      ServiceProfile profile = null;
      try {
         profile = this._transform(jaxbObj);
      } catch (MMRException e) {
         if (MMRTransformer._logger.isDebugEnabled())
            e.printStackTrace();
         throw new ServiceProfileException(e.getMessage());
      }
      return profile;
   }

   /**
    * Method to return the contributor email address.
    */
   public String getContributorEmailAddress() {
      return this._contributorEmailAddress;
   }

   /**
    * Method to return the contributor message level
    */
   public SPMessageLevel getContributorMessageLevel() {
      return this._contributorMessageLevel;
   }

   /**
    * Method to return the default root URI
    */
   public String getRootURI() {
      return this._rootURI;
   }

   /**
    * Method to register authentication information for a specific URI.
    *
    * @param uri  the remote URI
    * @param user the user name
    * @param pass the user password
    * @throws MMRException when unable to register
    */
   public synchronized void registerAuthentication(String uri, String user,
                                                   String pass) throws MMRException {
      MMRTransformer._logger.trace("Try to register autnetication for " + uri
            + " " + user + " " + pass);
      URIPath path = null;

      try {
         path = URIPath.createURIPath(uri, user, pass);
      } catch (URISyntaxException e) {
         throw new MMRException(uri + " is not a valid URI.");
      }

      if (!path.isLocal() && path.getUser() != null) {
         if (!MMRTransformer._auth.contains(path.getHostURI())) {
            MMRTransformer._logger.trace("Authentication " + path.getHostURI()
                  + " " + path.getURI() + " " + path.getUser() + " "
                  + path.getPassword());
            MMRTransformer._auth.put(path.getHostURI(), path);
         }
      }
   }

   /**
    * Method to associate the MMR file type with its actual URI name.
    *
    * @param type the MMR file type
    * @param uri  the URI to the file
    * @throws MMRException when unable to register.
    */
   public synchronized void registerFile(FILE_TYPE type, String uri)
         throws MMRException {
      URIPath path = null;

      try {
         path = URIPath.createURIPath(uri);
      } catch (URISyntaxException e) {
         if (MMRTransformer._logger.isDebugEnabled()) {
            e.printStackTrace();
         }
         throw new MMRException("Unable to register file: " + uri + ".");
      }

      if (path.getHostURI() != null) {
         URIPath auth = MMRTransformer._auth.get(path.getHostURI());
         if (auth != null) {
            path.setUser(auth.getUser());
            path.setPassword(auth.getPassword());
         }
      }

      this._fileSet.put(type, path);
      this.setManualFileSet(true);
   }

   /**
    * Method to associate the MMR file type with its actual URI name. Autentication is used by the input user name and
    * password.
    *
    * @param type the MMR file type
    * @param uri  the URI to the file
    * @param user the user name to connect to URI
    * @param pass the user password to connect to URI.
    * @throws MMRException when unable to register.
    */
   public synchronized void registerFile(FILE_TYPE type, String uri,
                                         String user, String pass) throws MMRException {
      this.registerAuthentication(uri, user, pass);
      this.registerFile(type, uri);
   }

   /**
    * Method to set the contributor email address
    *
    * @param emailAddress the email address
    */
   public void setContributorEmailAddress(String emailAddress) {
      this._contributorEmailAddress = emailAddress;
   }

   /**
    * Method to set the contributor message level
    *
    * @param messageLevel the message level
    */
   public void setContributorMessageLevel(SPMessageLevel messageLevel) {
      this._contributorMessageLevel = messageLevel;
   }

   /**
    * Method to set the keep path flag.
    *
    * @param keepPath the keep path flag
    */
   public void setKeepPath(boolean keepPath) {
      this._keepPath = keepPath;
   }

   /**
    * Method to enable manual file set building.
    *
    * @param flag true for enabling file set building
    */
   public void setManualFileSet(boolean flag) {
      this._manualFileSet = flag;
   }

   /**
    * Method to set the root URI
    *
    * @param rootURI the root URI
    */
   public void setRootURI(String rootURI) {
      this._rootURI = rootURI;
   }

   /**
    * Method to set the search paths for file discovery. Each path should be separated by ';', and each path should be a
    * standard URI.
    *
    * @param paths the search path value
    */
   public synchronized void setSearchPath(String paths) {
      this._searchPaths = paths.split(";");
      for (int i = 0; i < this._searchPaths.length; ++i) {
         MMRTransformer._logger.trace("Search path |" + this._searchPaths[i]
               + "|.");
         try {
            URIPath path = URIPath.createURIPath(this._searchPaths[i]);

            if (!path.isLocal()) {
               try {
                  this.registerAuthentication(path.getURI(), path.getUser(),
                        path.getPassword());
               } catch (MMRException e) {
                  if (MMRTransformer._logger.isDebugEnabled()) {
                     e.printStackTrace();
                  }
                  MMRTransformer._logger
                        .error("Unable to register authentication for URI: "
                              + path.getURI() + ".");
               }
            }

            MMRTransformer._logger.trace("Register search path: "
                  + path.getURI());

            this._searchPaths[i] = path.getURI();
         } catch (URISyntaxException e) {
            if (MMRTransformer._logger.isDebugEnabled()) {
               e.printStackTrace();
            }
            MMRTransformer._logger
                  .error("Unabled to proceess search path value "
                        + this._searchPaths[i] + ".");
         }
      }
   }

   @Override
   public ServiceProfile transfrom(Properties sources) throws ServiceProfileException {

      ServiceProfile result = null;

      String mmrfile = sources.getProperty("MMR");

      if (mmrfile == null) {
         return result;
      }

      try {
         result = this.transform(new URI(mmrfile));
      } catch (URISyntaxException | IOException e) {
         if (MMRTransformer._logger.isDebugEnabled()) {
            e.printStackTrace();
         }
      }
      return result;
   }

   /**
    * Method to transform the input MMR content string.
    *
    * @param contents the MMR data string
    */
   public ServiceProfile transform(String contents)
         throws ServiceProfileException {
      return this._transform(new StringReader(contents));
   }

   /**
    * Method to transform the input MMR referenced by a URI.
    *
    * @param uri the URI to the MMR file.
    */
   public ServiceProfile transform(URI uri) throws IOException,
         ServiceProfileException {

      ServiceProfile profile = null;
      FileObject file = null;

      try {
         if (!uri.isAbsolute()) {
            uri = new File(uri.toString()).toURI();
         }
         file = MMRTransformer._getFileObject(uri.toString());

         BufferedInputStream bis =
               new BufferedInputStream(file.getContent().getInputStream());
         byte[] buf = new byte[4092];
         int bytesRead = 0;
         StringBuffer msg = new StringBuffer();
         while ((bytesRead = bis.read(buf, 0, 4092)) > -1) {
            msg.append(new String(buf, 0, bytesRead));
         }
         bis.close();
         profile = this.transform(msg.toString());
      } catch (FileSystemException e) {
         if (MMRTransformer._logger.isDebugEnabled()) {
            e.printStackTrace();
         }
         throw new IOException("Unable to access file: " + uri.toString() + ".");
      } finally {
         if (file != null) {
            try {
               file.close();
            } catch (FileSystemException e) {
               if (MMRTransformer._logger.isDebugEnabled())
                  e.printStackTrace();
            }
         }
      }

      return profile;
   }
}
