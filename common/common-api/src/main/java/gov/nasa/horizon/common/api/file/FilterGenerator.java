package gov.nasa.horizon.common.api.file;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: thuang
 * Date: 8/23/13
 * Time: 12:02 AM
 * To change this template use File | Settings | File Templates.
 */
public interface FilterGenerator {

   String getURL();

   Set<String> getFileSet();

}
