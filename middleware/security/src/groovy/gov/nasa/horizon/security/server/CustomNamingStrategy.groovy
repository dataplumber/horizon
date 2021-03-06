package gov.nasa.horizon.security.server

import org.hibernate.cfg.ImprovedNamingStrategy
import org.hibernate.internal.util.StringHelper

class CustomNamingStrategy extends ImprovedNamingStrategy {

    String classToTableName(String className) {
        "SEC_" + StringHelper.unqualify(className)
    }

}
