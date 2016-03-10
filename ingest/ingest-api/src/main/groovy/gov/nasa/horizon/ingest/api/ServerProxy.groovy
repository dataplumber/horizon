package gov.nasa.horizon.ingest.api

import gov.nasa.horizon.ingest.api.Session
import gov.nasa.horizon.ingest.api.SessionException
import gov.nasa.horizon.ingest.api.content.SIPHandler
import gov.nasa.horizon.ingest.api.protocol.IngestProtocol
import gov.nasa.horizon.ingest.api.protocol.Modifier
import gov.nasa.horizon.common.util.GeneralFileFilter
import java.io.IOException
import java.util.regex.Matcher
import javax.xml.bind.JAXBException
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.xml.sax.SAXParseException

public class ServerProxy implements Runnable {

  private final Log log = LogFactory.getLog(ServerProxy.class)

  private String federation
  private boolean alive = true
  private Session session
  private long userId = -1
  private long remoteSessionId = -1
  private String authToken = null
  private int refCount = 1
  private Request currentRequest
  private Thread proxyThread
  private def requests =
  new Vector(Constants.REQUEST_CAPACITY, Constants.REQUEST_CAPACITY_INCR).asList()


  ServerProxy(Session session) throws SessionException {
    this(session, session.federation)
  }
  
  ServerProxy(Session session, String federation) throws SessionException {
    this.session = session
    this.federation = federation

    log.debug("user: ${session.username}")

    IngestProtocol request = IngestProtocol.createLoginRequest(
            session.username,
            session.password,
            null)

    String action = session.domain.getUrl(this.federation) + Constants.INIT_ACTION

    Post post = new Post(url: action)
    post.body = request.toRequest()
    log.trace "Converted request body text: '$post.body'."
    IngestProtocol response = new IngestProtocol()
    try {
      response.load(post.getUncheckedText())
    } catch (IOException e) {
      String message = "Unable to connect to server ${federation} at ${action}."
      throw new SessionException(message, Errno.CONN_FAILED, e)
    } catch (JAXBException e) {
      String message = "Unable to parse server response from server at ${action}."
      throw new SessionException(message, e)
    } catch (SAXParseException e) {
      String message = "Unable to parse server response from server at ${action}."
      throw new SessionException(message, e)
    }
    if (response.errno != Errno.OK) {
      throw new SessionException(response.description, Errno.INVALID_LOGIN)
    }
    userId = response.userId
    remoteSessionId = response.sessionId
    authToken = response.sessionToken

    log.trace("""
            user: ${session.username},
            auth: ${authToken},
            issue: ${response.loginIssueTime},
            expires: ${response.loginExpireTime}
            """)
    proxyThread = new Thread(this)
    proxyThread.start()
  }

  int put(Request request) {
    return dispatchRequest(request, false)
  }

  int putExpedited(Request request) {
    return dispatchRequest(request, true)
  }

  void run() {
    runProxy()
  }

  protected void runProxy() {
    log.trace "Service thread started."

    try {
      while (true) {
        waitForCommand()
        if (!currentRequest) {
          // TODO close connection

          terminate()

          log.trace "${this} exiting service thread"
          return
        }

        decodeAndExecuteRequest()
      }
    } catch (IOException e) {
      log.trace(e.message, e)
    } catch (InterruptedException e) {
      terminate()
      log.trace("${this} close immediate has been called, exiting service thread.", e)
    }
  }

  private void waitForCommand() throws InterruptedException {
    synchronized (requests) {
      if (refCount < 1) {
        currentRequest = null
        return
      }

      while (requests.empty && alive) {
        log.trace "${this} Server thread waiting..."
        requests.wait()
        log.trace "${this} Server thread wait over."
      }

      if (alive) {
        currentRequest = (Request) requests[0]
        requests.remove(0)
      }
    }
  }

  private int dispatchRequest(Request request, boolean expedited) {
    final int transactionId = session.transactionId
    request.transactionId = transactionId

    synchronized (requests) {
      if (expedited) {
        requests.add(0, request)
      } else {
        requests.add(request)
      }
      requests.notify()
    }
    return transactionId
  }

  private void decodeAndExecuteRequest() {
    log.trace("${this} opcode $currentRequest.operation")
    log.trace("${this} transaction $currentRequest.transactionId")

    try {
      Opcode operation = currentRequest.operation
      if (operation == Opcode.CLIENT_ADD || operation == Opcode.CLIENT_REPLACE) {
        log.trace(currentRequest.modifier)
        if (currentRequest.modifier == Modifier.REGEXP) {
          addRegExp()
          //} else if (modifier == Constants.MEMTRANSFER) {
          //   addInMemoryMetadata()
        } else {
          addProducts()
        }
      } else if (operation == Opcode.CLIENT_LIST) {
        if (currentRequest.modifier == Modifier.REGEXP) {
          listRegExp()
        } else {
          listProducts()
        }
      } else if (operation == Opcode.LOGOUT) {
        log.trace "getting ready to quit"
        closeConnection()
      } else {
        execCmd(!Constants.NEED_TYPE)
      }
    } catch (SessionException e) {
      endTransaction(e.errno, e.message)
    }
  }

  private void listRegExp() throws SessionException {
    currentRequest.sessionId = remoteSessionId
    currentRequest.userId = userId
    currentRequest.expression = currentRequest.regexp


    String action = session.domain.getUrl(this.federation) + Constants.LIST_ACTION
    Post post = new Post(url: action)
    post.body = currentRequest.toRequest()
    log.trace "Converted request body text: '${post.body}'."
    Result result = new Result()
    try {
      String response = post.text
      log.trace "[RESPONSE] ${response}"
      result.load(response)
    } catch (IOException e) {
      result.errno = Errno.CONN_FAILED
      result.description = "Unable to communicate to remote service at ${action}."
      log.error(result.description)
    } catch (JAXBException e) {
      result.errno = Errno.INVALID_PROTOCOL
      result.description = "Unrecognized response from ingest engine."
    } catch (SAXParseException e) {
      result.errno = Errno.INVALID_PROTOCOL
      result.description = "Unrecognized response from ingest engine."
    }
    session.postResult(result)
  }


  private void listProducts() throws SessionException {

    for (int i = 0; i < currentRequest.productNames.length; ++i) {
      currentRequest.sessionId = remoteSessionId
      currentRequest.userId = userId
      currentRequest.product = currentRequest.productNames[i]

      String action = session.domain.getUrl(this.federation) + Constants.LIST_ACTION
      Post post = new Post(url: action)
      post.body = currentRequest.toRequest()
      log.trace "Converted request body text: '${post.body}'."
      Result result = new Result()
      try {
        String response = post.text
        log.trace "[RESPONSE] ${response}"
        result.load(response)
      } catch (IOException e) {
        result.errno = Errno.CONN_FAILED
        result.description = "Unable to communicate to remote service at ${action}."
        log.error(result.description)
      } catch (JAXBException e) {
        result.errno = Errno.INVALID_PROTOCOL
        result.description = "Unrecognized response from ingest engine."
      } catch (SAXParseException e) {
        result.errno = Errno.INVALID_PROTOCOL
        result.description = "Unrecognized response from ingest engine."
      }
      if (i == currentRequest.productNames.length - 1)
        result.endOfTransaction = true
      session.postResult(result)
    }

  }


  private void addRegExp() throws SessionException {
    final Matcher regExHandler

    GeneralFileFilter filter = new GeneralFileFilter(currentRequest.regexp)
    File directory = new File(currentRequest.dir)
    String[] fileList = directory.list(filter)

    if (fileList.length < 1) {
      endTransaction(Errno.NO_FILES_MATCH, "No files found.")
      return
    }

    if (log.traceEnabled) {
      for (String name in fileList) {
        log.trace("File: ${name}.")
      }
    }

    currentRequest.productNames = fileList
    addProducts()
  }

  private void addProducts() throws SessionException {

    log.trace "starting addProducts"

    if (!currentRequest.productNames || currentRequest.productNames.length < 1) {
      endTransaction(Errno.NO_PRODUCT_SPECIFIED, "No products to add.")
      return
    }

    log.trace "continue addProducts"

    for (int i = 0; i < currentRequest.productNames.length; ++i) {
      String file = currentRequest.productNames[i]
      File f;
      if (file.indexOf(File.separator) > -1) {
        f = new File(file)
        file = f.name
      } else {
        f = new File(currentRequest.dir, file)
      }
      log.trace("Adding product from metadata file: '${file}'.")

      Result result
      if (!f.exists()) {
        result = new Result(Errno.FILE_NOT_FOUND,
                "SIP file '${file}' does not exist.")
        if (i == currentRequest.productNames.length - 1) {
          result.endOfTransaction = true
        }
        session.postResult(result)
        continue
      }

      if (!f.file) {
        if (f.directory) {
          result = new Result(Errno.DIRECTORY_IGNORED, "Metadata file '${file}' is a directory.")
        } else {
          result = new Result(Errno.FILE_NOT_NORMAL, "Metadata file '${file}' is not a normal file.")
        }
        if (i == currentRequest.productNames.length - 1) {
          result.endOfTransaction = true
        }
        session.postResult(result)
        continue
      }

      log.trace "processing [${i}] ${currentRequest.productNames[i]}."
      def handler

      try {
        handler = new SIPHandler(f.text)
      } catch (Exception e) {
        result = new Result(Errno.FILE_NOT_NORMAL, "Unsupported metadata file format for '${file}.")
        if (i == currentRequest.productNames.length - 1) {
          result.endOfTransaction = true
        }
        session.postResult(result)
        continue
      }

      IngestProtocol request = IngestProtocol.createClientAddRequest(
              remoteSessionId,
              authToken,
              userId,
              currentRequest.productType,
              handler.productName
      )

      //request.notify = null //handler.notify

      if (currentRequest.operation == Opcode.CLIENT_REPLACE) {
        if (!request.originalProduct) {
          if (!handler.replace) {
            request.originalProduct = handler.productName
            handler.replace = handler.productName
          } else {
            request.originalProduct = handler.replace
          }
        } else
          handler.replace = request.originalProduct
      }

      //log.trace("Notify ${request.notify}")
      request.addMetadata = handler.metadataText
      request.addFiles = handler.productFiles

      log.trace request.toString()

      String action
      if (currentRequest.operation == Opcode.CLIENT_ADD)
        action = session.domain.getUrl(this.federation) + Constants.ADD_ACTION
      else
        action = session.domain.getUrl(this.federation) + Constants.REPLACE_ACTION
      Post post = new Post(url: action)
      post.body = request.toRequest()
      log.trace "Converted request body text: '${post.body}'."
      result = new Result()
      try {
        result.load(post.text)
      } catch (IOException e) {
        result.errno = Errno.CONN_FAILED
        result.description = "Unable to communicate to remote service at ${action}."
        log.error(result.description)
      } catch (JAXBException e) {
        result.errno = Errno.INVALID_PROTOCOL
        result.description = "Unrecognized response from ingest engine."
      } catch (SAXParseException e) {
        result.errno = Errno.INVALID_PROTOCOL
        result.description = "Unrecognized response from ingest engine."
      }
      if (i == currentRequest.productNames.length - 1)
        result.endOfTransaction = true
      session.postResult(result)
    }
  }

  private void closeConnection() throws SessionException {
    def nextRequest
    log.trace "closing product type ${currentRequest.productType}."
    synchronized (requests) {
      int count = requests.size()
      for (int i = count - 1; i >= 0; --i) {
        nextRequest = requests.get(i)
        log.trace "currentReuqest type = ${currentRequest.productType}."
        log.trace "nextRequest type = ${nextRequest.productType}."
        if ((!currentRequest.productType && !nextRequest.productType) ||
                (currentRequest.productType && nextRequest.productType &&
                        nextRequest.productType == currentRequest.productType)) {
          requests.remove(i)
        }
      }
      log.trace "reference count before: ${refCount}."
      if (decrementRefCount() < 1) {
        log.trace "closing connection."
        // TODO maybe need to send a close signal to the server to remove the token.
      }
      log.trace "reference count after: ${refCount}."
    }

    endTransaction(Errno.OK, "Connection closed.")
  }

  int incrementRefCount() {
    synchronized (requests) {
      return ++refCount
    }
  }

  int decrementRefCount() {
    synchronized (requests) {
      return --refCount
    }
  }

  private void endTransaction(Errno errno, String message) {
    Result lastResult = new Result(currentRequest)
    lastResult.errno = errno
    lastResult.description = message
    lastResult.endOfTransaction = true
    session.postResult(lastResult)
  }

  private void terminate() {
    alive = false
    synchronized (requests) {
      requests.notify()
    }
  }

}
