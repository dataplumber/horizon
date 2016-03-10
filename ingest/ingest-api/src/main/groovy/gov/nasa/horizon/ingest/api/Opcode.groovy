package gov.nasa.horizon.ingest.api

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Dec 23, 2008
 * Time: 6:26:40 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Opcode {
  LOGIN,
  LOGOUT,
  CLIENT_ADD,
  CLIENT_REPLACE,
  CLIENT_DELETE,
  CLIENT_LIST,
  ENGINE_BOOT,
  ENGINE_HOTBOOT,
  ENGINE_PING,
  ENGINE_INGEST,
  ENGINE_DELETE,
  ENGINE_JOB,
  ENGINE_MOVE,
  ENGINE_PURGE

  Opcode next() {
    State[] vals = Opcode.values();
    return vals[(this.ordinal() + 1) % vals.length];
  }

  Opcode previous() {
    Lock[] vals = Opcode.values();
    return vals[(this.ordinal() - 1 + vals.length) % vals.length];
  }

}
