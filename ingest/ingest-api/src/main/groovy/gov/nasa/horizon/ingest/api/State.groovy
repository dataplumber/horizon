package gov.nasa.horizon.ingest.api

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Dec 23, 2008
 * Time: 4:37:57 PM
 * To change this template use File | Settings | File Templates.
 */
public enum State {
  PENDING,
  PENDING_STORAGE,
  PENDING_ASSIGNED,
  ASSIGNED,
  LOCAL_STAGED,
  STAGED,
  INVENTORIED,
  PENDING_ARCHIVE,
  PENDING_ARCHIVE_STORAGE,
  ARCHIVED,
  RESERVED,
  ABORTED

  State next() {
    State[] vals = State.values();
    return vals[(this.ordinal() + 1) % vals.length];
  }

  State previous() {
    State[] vals = State.values();
    return vals[(this.ordinal() - 1 + vals.length) % vals.length];
  }
}
