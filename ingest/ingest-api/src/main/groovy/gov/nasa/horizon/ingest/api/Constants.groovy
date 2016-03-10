/*****************************************************************************
 * Copyright (c) 2009 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
 
package gov.nasa.horizon.ingest.api

/**
 * Static class for constants used by the Ingest system
 *
 * @author T. Huang
 * @version $Id:$
 */
class Constants {

  static final String API_VERSION = "5.0.0e"
  static final String VERSION = "5.0.0e"
  static final String VERSION_DATE = "November 2013"
  static final String API_VERSION_STR = "DMAS API release $API_VERSION"
  static final String COPYRIGHT =
  "Copyright 2013, Jet Propulsion Laboratory, Caltech, NASA"
  static final String CLIENT_VERSION_STR =
  "DMAS Ingest Client Release $VERSION, $VERSION_DATE"

  static final String SERVER_VERSION_STR =
  "DMAS Ingest Server Release $VERSION, $VERSION_DATE"

  /* Request list capacity */
  static final int REQUEST_CAPACITY = 16
  static final int REQUEST_CAPACITY_INCR = 8

  /* Result list capacity */
  static final int RESULT_CAPACITY = 128
  static final int RESULT_CAPACITY_INCR = 64

  static final boolean NEED_TYPE = true

  static final String RESTART_DIR = ".horizon"
  static final String KEYCHAIN_FILE = ".keychain"
  static final String METADATA_EXTENSION = ".xml"

  static final String PROP_RESTART_DIR = "horizon.restart.dir"
  static final String PROP_DOMAIN_FILE = "horizon.domain.file"
  static final String PROP_USER_APP = "horizon.user.application"

  /* Request field names */
  static final String SUCCESS = "success"
  static final String ERRNO = "errno"
  static final String DESCRIPTION = "description"
  static final String OPCODE = "opcode"
  static final String PRODUCTTYPE = "producttype"
  static final String USERNAME = "username"
  static final String USERID = "userid"
  static final String PASSWORD = "password"
  static final String AUTHTOKEN = "authtoken"
  static final String SESSIONID = "sessionid"
  static final String ISSUETIME = "issuetime"
  static final String EXPIRETIME = "expiretime"
  static final String FILECOUNT = "filecount"
  static final String FILE = "file"

  /*
  * User/ProductType capabilities.
  */
  static final int GET = 1;
  static final int ADD = 2;
  static final int REPLACE = 4;
  static final int DELETE = 8;
  static final int OFFLINE = 16;
  static final int RENAME = 32;
  static final int NOTIFY = 64;
  static final int CONFIRM = 128;
  static final int QAACCESS = 256;
  static final int ARCHIVE = 512;
  static final int SUBTYPE = 1024;
  static final int RECEIPT = 2048;
  static final int LOCKTYPE = 4096;
  static final int PUSHSUBSCRIBE = 8192;

  /* Special user access */
  static final int ADMIN = 3
  static final int WRITE_ALL = 2
  static final int READ_ALL = 1
  static final int NO_ACCESS = 0
  static final int NOT_SET = -1


  /* Results timeout range: 0 means bloack until new event occurs*/
  static final def RESULT_TIMEOUT = 0..60


  /* Modifier */
  static final String REGEXP = "REGEXP"
  static final String MEMTRANSFER = "MEMTRANSFER"
  static final String NO_MODIFIER = "NO_MODIFIER"

  static final String INIT_ACTION = '/init'
  static final String ADD_ACTION = '/add'
  static final String ADD_UPDATE_ACTION = '/addupdate'
  static final String REPLACE_ACTION = '/replace'
  static final String DELETE_ACTION = '/delete'
  static final String DELETE_UPDATE_ACTION = '/deleteupdate'
  static final String ARCHIVE_UPDATE_ACTION = '/archiveupdate'
  static final String PURGE_UPDATE_ACTION = '/purgeupdate'
  static final String MOVE_UPDATE_ACTION = '/moveupdate'
  static final String LIST_ACTION = '/list'
  static final String BOOT_ACTION = '/boot'

  private Constants() {}

}
