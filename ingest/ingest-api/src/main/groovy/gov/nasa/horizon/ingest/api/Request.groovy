package gov.nasa.horizon.ingest.api

import gov.nasa.horizon.ingest.api.protocol.IngestProtocol
import javax.xml.bind.JAXBException
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory


class Request extends IngestProtocol {

  private static final Log log = LogFactory.getLog(Request.class)

  String regexp
  String dir

  String productName
  String originalProductName
  String metadata
  String[] productNames
  int transactionId

  Request() {
    super()
  }

  Request(Opcode opcode) {
    super()
    this.operation = opcode
  }

  Request(String federation, String productType, String dir, Opcode opcode, String regexp) {
    super()
    this.operation = opcode
    this.federation = federation
    this.productType = productType
    this.dir = dir
    this.regexp = regexp
  }

  Request(String federation, String productType, String dir, Opcode opcode, String[] productNames) {
    super()
    this.operation = opcode
    this.federation = federation
    this.productType = productType
    this.dir = dir
    this.productNames = productNames
  }

  Request(String federation, String productType, Opcode opcode) throws JAXBException {
    super()
    this.operation = opcode
    this.federation = federation
    this.productType = productType
  }
}
