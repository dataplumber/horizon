package gov.nasa.horizon.ingest.server

import org.restlet.data.Request as RestletRequest
import org.restlet.data.Response as RestletResponse

import gov.nasa.horizon.ingest.api.Errno
import gov.nasa.horizon.ingest.api.Opcode
import gov.nasa.horizon.ingest.api.Result
import gov.nasa.horizon.ingest.api.protocol.IngestProtocol
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.restlet.Context
import org.restlet.data.MediaType
import org.restlet.resource.Representation
import org.restlet.resource.Resource

class ServerPortHandler extends Resource {
  private static Log log = LogFactory.getLog(ServerPortHandler.class)

  ServerPortListener listener

  def ServerPortHandler(Context context,
          RestletRequest request,
          RestletResponse response) {

    super(context, request, response)
    listener = (ServerPortListener) context.attributes.listener
    modifiable = true
    log.debug("Server Port context created")
  }

  def boolean allowDelete() {
    return false
  }

  def void acceptRepresentation(Representation representation) {

    String result = handleRequest(representation.text)
    response.setEntity(result, MediaType.APPLICATION_XML)
    representation.release()
  }

  protected def handleRequest(String message) {
    if (!message) {
      return handleError(Errno.INVALID_PROTOCOL, "Invalid request protocol.")
    }

    log.trace(message)

    def xml = new XmlSlurper().parseText(message)

    IngestProtocol manifest = new IngestProtocol()
    IngestProtocol response = new IngestProtocol()
    try {
      manifest.load(xml.params.toString())
      response.load(xml.params.toString())

      if (manifest.operation == Opcode.ENGINE_PING) {
        if (log.traceEnabled) {
          log.trace("Current ${ServerMonitor.instance.userJobs.size()} active job(s) -")
          ServerMonitor.instance.userJobs.each {job ->
            log.trace("${job.name} ${job.received}")
          }
        }
        response.errno = Errno.OK
        response.description = "OK"
      } else {
        if (manifest.operation == Opcode.ENGINE_JOB) {
          def name = "${manifest.jobId}:${manifest.federation}:${manifest.productType}:${manifest.product}"
          // handle query for current jobs
          if (log.traceEnabled) {
            ServerMonitor.instance.userJobs.each {job ->
              log.trace("${job.name} ${job.received}")
            }
          }
          Map job = ServerMonitor.instance.findUserJob(name)
          if (job) {
            response.errno = Errno.OK
            response.description = "${job.name} is in progress.  Received at : ${job.received}."
          } else {
            response.errno = Errno.PRODUCT_NOT_FOUND
            response.description = "Job ${name} not found."
          }
        } else if (listener.jobPool.jobCount == listener.maxCon) {
          if (log.traceEnabled) {
            listener.jobPool.jobs.each {
              log.trace "[${it.name} - ${it.received}]"
            }
          }
          response.errno = Errno.SERVER_BUSY
          response.description = "Engine ${IngestServer.instance.serverName} is currently at its maximum load of ${listener.maxCon}."
        } else if (manifest.operation == Opcode.ENGINE_INGEST) {
          def name = "${manifest.jobId}:${manifest.federation}:${manifest.productType}:${manifest.product}"
          if (listener.jobPool.execute(name) {
            listener.add(manifest)
          }) {
            response.errno = Errno.OK
            response.description = "OK"
          } else {
            response.errno = Errno.SERVER_BUSY
            response.description = "Unable to queue job ${name}."
          }
        } else if (manifest.operation == Opcode.ENGINE_DELETE) {
          def name = "${manifest.jobId}:${manifest.federation}:${manifest.productType}:${manifest.product}"
          if (listener.jobPool.execute(name) {
            listener.delete(manifest)
          }) {
            response.errno = Errno.OK
            response.description = "OK"
          } else {
            response.errno = Errno.SERVER_BUSY
            response.description = "Unable to queue job ${name}."
          }
        } else if (manifest.operation == Opcode.ENGINE_MOVE) {
          def name = "${manifest.jobId}:${manifest.federation}:${manifest.productType}:${manifest.product}"
          if (listener.jobPool.execute(name) {
            listener.move(manifest)
          }) {
            response.errno = Errno.OK
            response.description = "OK"
          } else {
            response.errno = Errno.SERVER_BUSY
            response.description = "Unable to queue job ${name}."
          }
        } else {
          return handleError(Errno.INVALID_PROTOCOL, "Unknown request: ${manifest.operation}")
        }
      }
    } catch (Exception e) {
      log.debug(e.message, e)
      return handleError(Errno.INVALID_PROTOCOL, "Invalid request protocol.")
    }
    log.trace("[RESPONSE] ${response.toString()}")
    return response.toString()
  }

  protected def handleError(Errno errno, String message) {
    Result result = new Result()
    result.errno = errno
    result.description = message
    return result.toString()
  }


}
