package gov.nasa.horizon.ingest.api

import gov.nasa.horizon.common.api.util.DateTimeUtility

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Dec 24, 2008
 * Time: 2:34:08 PM
 * To change this template use File | Settings | File Templates.
 */

public class Product {
  String name
  State state
  Lock lock
  String metadataText
  String note
  String archiveNote
  Date createdTime
  Date stagedTime
  Date archivedTime
  List<ProductFile> productFiles = []

  String toString() {
    String result = """
   name         = ${name}
   state        = ${state.toString()}
   lock         = ${lock.toString()}
   metadata     = ${metadataText ? metadataText : ''}
   note         = ${note ? note : ''}
   archiveNote  = ${archiveNote ? archiveNote : ''}
   createdTime  = ${createdTime ? DateTimeUtility.getDateCCSDSAString(createdTime) : ''}
   stagedTime   = ${stagedTime ? DateTimeUtility.getDateCCSDSAString(stagedTime) : ''}
   archivedTime = ${archivedTime ? DateTimeUtility.getDateCCSDSAString(archivedTime) : ''}\n"""
    if (productFiles && productFiles.size > 0) {
      for (int i = 0; i < productFiles.size; ++i) {
        result += "   FILE[${i}] = ${productFiles[i].name} | ${productFiles[i].size} | ${productFiles[i].checksum}\n"
      }
    }
    return result
  }
}