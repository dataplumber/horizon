package gov.nasa.horizon.ingest.api.protocol

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Jan 15, 2009
 * Time: 4:26:08 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Modifier {
  NOMODIFIER,
  REGEXP,
  LATEST,
  DATETIME,
  PRODUCTNAMES,
  PRODUCTSINCE,
  PRODUCTBETWEEN

  Modifier next() {
    Modifier[] vals = Modifier.values();
    return vals[(this.ordinal() + 1) % vals.length];
  }

  Modifier previous() {
    Modifier[] vals = Modifier.values();
    return vals[(this.ordinal() - 1 + vals.length) % vals.length];
  }

}
