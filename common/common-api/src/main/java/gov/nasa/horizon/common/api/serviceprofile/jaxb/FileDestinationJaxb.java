package gov.nasa.horizon.common.api.serviceprofile.jaxb;

import gov.nasa.horizon.common.api.jaxb.serviceprofile.FileDestination;
import gov.nasa.horizon.common.api.serviceprofile.SPFileDestination;

import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: thuang
 * Date: 7/19/13
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileDestinationJaxb extends AccessorBase implements
      SPFileDestination {

   private FileDestination _jaxbObj;

   public FileDestinationJaxb() {
      this._jaxbObj = new FileDestination();
   }

   public FileDestinationJaxb(FileDestination jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public FileDestinationJaxb(SPFileDestination fileDestination) {
      this._jaxbObj = new FileDestination();
      this._jaxbObj.setLocation(fileDestination.getLocation());
      for (String link : fileDestination.getLinks()) {
         this.addLink(link);
      }
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final FileDestinationJaxb other = (FileDestinationJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   @Override
   public List<String> getLinks() {
      List<String> result = new Vector<String>();

      if (this._jaxbObj.getLinks() != null) {
         for (String link : this._jaxbObj.getLinks().getLink()) {
            result.add(link);
         }
      }
      return result;
   }

   protected List<String> _getLinks() {
      if (this._jaxbObj.getLinks() == null) {
         this._jaxbObj.setLinks(new FileDestination.Links());
      }
      return this._jaxbObj.getLinks().getLink();
   }

   @Override
   public synchronized void addLink(String link) {
      this._getLinks().add(link);
   }

   @Override
   public String getLocation() {
      return this._jaxbObj.getLocation();
   }

   @Override
   public void setLocation(String location) {
      this._jaxbObj.setLocation(location);
   }
}
