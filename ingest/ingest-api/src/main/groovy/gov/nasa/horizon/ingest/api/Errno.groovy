package gov.nasa.horizon.ingest.api

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Dec 23, 2008
 * Time: 4:43:23 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Errno {

  OK,
  WARNING,
  INVALID_LOGIN,
  INVALID_TYPE,
  TYPE_OPEN,
  CONN_FAILED,
  ALREADY_OPEN,
  TCP_PORT_RANGE,
  TIMEOUT_RANGE,
  NO_SERVERS,
  CHECKSUM_ERROR,
  INVALID_SESSION,
  NO_PRODUCT_SPECIFIED,
  MISSING_FEDERATION,
  NACKED,
  FILE_NOT_NORMAL,
  DIRECTORY_IGNORED,
  INVALID_FILE_EXPR,
  NO_FILES_MATCH,
  NO_FILE_SPECIFIED,
  PRODUCT_EXISTS,
  UNREGISTERED_HOST,
  INTERRUPTED,
  USERNOTLOGGED,
  INVENTORY_ERR,
  DENIED,
  SESSION_EXPIRED,
  INVALID_PROTOCOL,
  RESTART_FILE_ERR,
  SERVER_BUSY,
  INGEST_ERR,
  UNEXPECTED_UPDATE,
  PROVIDER_CONN_ERROR,
  PRODUCT_NOT_FOUND,
  FILE_NOT_FOUND,
  ARCHIVE_ERR,
  INVALID_QUERY,
  IGNORED

  Errno next() {
    State[] vals = Errno.values();
    return vals[(this.ordinal() + 1) % vals.length];
  }

  Errno previous() {
    Errno[] vals = Errno.values();
    return vals[(this.ordinal() - 1 + vals.length) % vals.length];
  }

}