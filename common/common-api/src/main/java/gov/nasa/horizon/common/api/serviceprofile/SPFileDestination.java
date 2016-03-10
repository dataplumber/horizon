package gov.nasa.horizon.common.api.serviceprofile;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: thuang
 * Date: 7/19/13
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SPFileDestination extends Accessor{
   List<String> getLinks();
   void addLink(String link);
   String getLocation();
   void setLocation(String location);
}
