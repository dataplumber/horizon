package bus;

import java.io.Serializable;
import java.util.List;
import java.util.ListIterator;
import db.CatalogDao;

public class Catalog implements Serializable {

    private List fields;

    public void setFields(List f) {
        fields = f;
    }

    public List getFields() {
        return fields;
    }

}
