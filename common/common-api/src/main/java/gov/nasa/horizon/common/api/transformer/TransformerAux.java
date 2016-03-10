package gov.nasa.horizon.common.api.transformer;

import gov.nasa.horizon.common.api.serviceprofile.SPCommon.SPMessageLevel;

public interface TransformerAux {

   String getContributorEmailAddress();

   SPMessageLevel getContributorMessageLevel();

   String getRootURI();

   void setContributorEmailAddress(String emailAddress);

   void setContributorMessageLevel(SPMessageLevel messageLevel);

   void setRootURI(String rootURI);
}
