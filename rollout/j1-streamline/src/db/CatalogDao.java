package db;

import bus.Catalog;
import java.util.List;

public interface CatalogDao {

    public Catalog getCatalogFromDB();
    public void setCatalogToDB(Catalog cat);
}
