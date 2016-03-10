package gov.nasa.horizon.common.api.serviceprofile;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: thuang
 * Date: 7/22/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SPFile extends Accessor {

   SPCommon.SPChecksumAlgorithm getChecksumType();

   void setChecksumType(SPCommon.SPChecksumAlgorithm checksumType);

   String getChecksumValue();

   void setChecksumValue(String checksumValue);

   SPCommon.SPCompressionAlgorithm getCompressionType();

   void setCompressionType(SPCommon.SPCompressionAlgorithm compressionType);

   SPCommon.SPDataFormat getDataFormat();

   void setDataFormat(SPCommon.SPDataFormat dataFormat);

   void clearLinks();

   void addLink(String link);

   List<String> getLinks();

   String getName();

   void setName(String name);

   Long getSize();

   void setSize(Long size);
}
