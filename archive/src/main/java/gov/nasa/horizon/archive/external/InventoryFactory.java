package gov.nasa.horizon.archive.external;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nasa.horizon.archive.core.ArchiveProperty;


public abstract class InventoryFactory
{
    private static InventoryFactory self = null;
    private static Log log = LogFactory.getLog(ArchiveProperty.class);
    public static synchronized InventoryFactory getInstance()
    {
        if (InventoryFactory.self == null)
        {
            Class<?> defaultImpl;
            String propertyName = "gov.nasa.horizon.inventory.factory";
            
            //String defaultImplName = System.getProperty (propertyName, "gov.nasa.horizon.archive.external.wsm.Factory");
            String defaultImplName = ArchiveProperty.getInstance().getProperty(propertyName, "gov.nasa.horizon.archive.external.wsm.Factory");
            log.debug("Creating Factory: " + defaultImplName);
            
            try
            {
                defaultImpl = Class.forName (defaultImplName);
                InventoryFactory.self = (InventoryFactory)defaultImpl.newInstance();
            }
            catch (ClassNotFoundException cnfe)
            {
                System.err.println ("Error locating factory class '" + defaultImplName + "'. The property '" + propertyName
                        + "' is incorrectly defined or the JAR file is either corrupt or incorrectly built.");
            }
            catch (IllegalAccessException iae)
            {
                System.err.println ("Error accessing factory class '" + defaultImplName + "'. The property '" + propertyName
                        + "' is incorrectly defined or the JAR file is either corrupt or incorrectly built.");
            }
            catch (InstantiationException ie)
            {
                System.err.println ("Error creating factory object of class '" + defaultImplName + "'. The property '" + propertyName
                        + "' is incorrectly defined or the JAR file is either corrupt or incorrectly built.");
            }
        }
        
        return InventoryFactory.self;
    }
    
    abstract public InventoryAccess getAccess();
    abstract public InventoryQuery getQuery();
}
