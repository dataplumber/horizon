package gov.nasa.horizon.ingest.api

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Dec 23, 2008
 * Time: 4:39:04 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Lock {
  NONE,
  ADD,
  GET,
  ARCHIVE,
  INVENTORY,
  REPLACE,
  DELETE,
  PENDING_RESERVED,
  RESERVED,
  RENAME,
  TRASH,
  PURGE

  Lock next() {
    State[] vals = Lock.values();
    return vals[(this.ordinal() + 1) % vals.length];
  }

  Lock previous() {
    Lock[] vals = Lock.values();
    return vals[(this.ordinal() - 1 + vals.length) % vals.length];
  }
}
