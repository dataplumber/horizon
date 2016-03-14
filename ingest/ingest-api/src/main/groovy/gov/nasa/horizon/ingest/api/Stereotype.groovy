package gov.nasa.horizon.ingest.api

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Dec 23, 2008
 * Time: 4:37:57 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Stereotype {
  INGEST,
  ARCHIVE,
  PURGE

  Stereotype next() {
    Stereotype[] vals = Stereotype.values();
    return vals[(this.ordinal() + 1) % vals.length];
  }

  Stereotype previous() {
    Stereotype[] vals = Stereotype.values();
    return vals[(this.ordinal() - 1 + vals.length) % vals.length];
  }
}