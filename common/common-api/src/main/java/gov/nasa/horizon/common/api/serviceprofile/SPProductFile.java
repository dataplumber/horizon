package gov.nasa.horizon.common.api.serviceprofile;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: thuang
 * Date: 7/22/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SPProductFile extends Accessor {

   SPFile createFile();

   void setFile(SPFile file);

   SPFile getFile();

   void setFileType (SPCommon.SPFileClass fileType);

   SPCommon.SPFileClass getFileType();

}
