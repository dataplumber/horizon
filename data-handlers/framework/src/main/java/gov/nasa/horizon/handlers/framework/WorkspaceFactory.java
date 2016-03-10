package gov.nasa.horizon.handlers.framework;

/**
 * Factory interface to create new workspace objects
 *
 * @author T. Huang
 * @version $Id: $
 */
public interface WorkspaceFactory {

   Workspace createWorkspace(String productType);

}
