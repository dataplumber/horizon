package bus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.ListIterator;
import java.util.List;
import java.util.Set;

import db.CatalogDao;
import db.CatalogDaoJdbc;
import java.util.ArrayList;


public class CatalogManager implements Serializable {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private CatalogDao cd;   // = new CatalogDaoJdbc();
    private Catalog cat;

    public void setCatalogDao(CatalogDao cd) {
        this.cd = cd;
    }

    public void setCatalog(Catalog c) {
        this.cat = c;
        cd.setCatalogToDB(this.cat);
    }

    public Catalog getCatalog() {
        cat = cd.getCatalogFromDB();
        return cat;
    }

}
