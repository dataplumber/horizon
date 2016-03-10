package gov.nasa.podaac.j1slx;
 
import org.hibernate.cfg.ImprovedNamingStrategy
import org.hibernate.util.StringHelper

class CustomNamingStrategy extends ImprovedNamingStrategy {

    String classToTableName(String className) {
        "J1SLX_" + StringHelper.unqualify(className)
    }

//    String propertyToColumnName(String propertyName) {
//        "col_" + StringHelper.unqualify(propertyName)
//    }
}
