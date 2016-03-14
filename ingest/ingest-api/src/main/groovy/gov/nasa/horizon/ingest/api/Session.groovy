package gov.nasa.horizon.ingest.api

import gov.nasa.horizon.ingest.api.protocol.Modifier
import javax.xml.bind.JAXBException
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.xml.sax.SAXException

class Session {

  private static Log log = LogFactory.getLog(Session.class)

  //private ServerProxy proxy

  private int transactionCount = 0
  private int transactionId = 0
  String username
  String password
  String domainFile
  Domain domain
  Keychain keychain
  String currentDir = System.getProperty("user.dir")
  private String registry
  private def results =
  new Vector(Constants.RESULT_CAPACITY, Constants.RESULT_CAPACITY_INCR).asList()
  String federation
  private Map federationProxyMap = [:]

  /**
   * Constructor to load user login information for the user local registry
   * and use the default federation name for connection lookup
   */
  Session(String domainFile) throws SessionException {
    this.domainFile = domainFile
    configure()
  }

  Session(String username, String password, String domainFile) throws SessionException {
    this.username = username
    this.password = password
    this.domainFile = domainFile
    configure()
  }

  Session(String federation, String domainFile) throws SessionException {
    this.federation = federation
    this.domainFile = domainFile
    configure()
  }

  /**
   * Constructor use the provided login information and use the
   * default federation name for connection lookup
   */
  Session(String federation, String username, String password, String domainFile) throws SessionException {
    this.federation = federation
    this.username = username
    this.password = password
    this.domainFile = domainFile
    configure()

  }

  String getRegistry() {
    return registry
  }

  void setLoginInfo(String username, String password) {
    this.username = username
    this.password = password
  }

  boolean isLoggedOn() {
    return (username && password)
  }

  void open() throws SessionException {
  /*
    if (!federation) {
      federation = domain.default
    }
    if (!username || !password) {
      username = keychain.getFederationUsername(federation)
      password = keychain.getFederationPassword(federation)
      if (password) {
        password = Encrypt.encrypt(password)
      }
    }

    log.trace("username: ${username}, password: ${password}")
    proxy = new ServerProxy(this)
  */
  }
  
  private ServerProxy getServerProxy(String federation) throws SessionException {
    if (!username || !password) {
      username = keychain.getFederationUsername(federation)
      password = keychain.getFederationPassword(federation)
      if (password) {
        password = password
      }
    }
    synchronized(federationProxyMap) {
       ServerProxy proxy = federationProxyMap[federation]
       if (!proxy) {
          log.trace("username: ${username}")
          proxy = new ServerProxy(this, federation)
          federationProxyMap[federation] = proxy
       }
       return proxy
    }
  }
  
  protected String getFederation(String productType) {
     String federation = domain.getFederation(productType)
     if (!federation) {
       federation = domain.default
     }
     log.trace("productType: ${productType}, federation: ${federation}")
     return federation
  }

  void postResult(Result result) {
    log.trace "Posting result."
    synchronized (results) {
      log.trace(result.description)
      results.add(result)
      log.trace "Posting result notify."
      results.notifyAll()
      log.trace "Posting result notified"
    }
  }

  synchronized int getTransactionId() {
    ++transactionCount
    ++transactionId
    return transactionId
  }

  Result result(int timeout = Constants.RESULT_TIMEOUT.from) throws SessionException {
    Result result = null;
    log.trace "result timeout = $timeout"
    if (!Constants.RESULT_TIMEOUT.contains(timeout)) {
      throw new SessionException("Timeout '$timeout' not in range (" +
              Constants.RESULT_TIMEOUT.from + ".." + Constants.RESULT_TIMEOUT.to + ").",
              Errno.TIMEOUT_RANGE)
    }

    log.debug "transaction count = ${transactionCount}."
    synchronized (results) {
      if (transactionCount > 0) {
        while (results.empty) {
          try {
            if (timeout == Constants.RESULT_TIMEOUT.from) {
              log.trace "waiting for result."
              results.wait()
            } else {
              log.trace "waiting for result for $timeout seconds."
              results.wait(timeout * 1000)
            }
          } catch (InterruptedException e) {
            log.trace(null, e)
            throw new SessionException("Unexpected interrupt.",
                    Errno.INTERRUPTED)
          }
        }
      }

      if (!results.empty) {
        log.trace("result queue has ${results.size()} result(s).")
        result = (Result) results.remove(0)
        if (result.endOfTransaction) {
          --transactionCount
        }
        log.trace("End of transaction is ${result.endOfTransaction}")
      } else {
        log.trace("The result queue is empty.")
      }
    }
    return result
  }

  synchronized int getTransactionCount() {
    return transactionCount
  }

  void closeImmediate() {
    try {
      close()
      while (transactionCount > 0) {
        result()
      }
    } catch (SessionException e) {
      log.error(e.getMessage(), e)
    }
  }

  /**
   *
   * @return the close transaction ID
   */
  int close() throws SessionException {
    return this.logout()
  }


  void setCurrentDir(String path) throws SessionException {
    if (!new File(path).directory) {
      throw new SessionException("Path '${path}' is not a directory.", Errno.FILE_NOT_FOUND)
    }
    currentDir = path
  }


  def void configure() throws SessionException {
    domain = new Domain()
    if (domainFile) {
      try {
        domain.load(new File(domainFile).text)
      } catch (JAXBException e) {
        log.debug(e.message, e)
        throw new SessionException("Unable to parse domain file: ${domainFile}.", e)
      } catch (SAXException e) {
        log.debug(e.message, e)
        throw new SessionException("Unable to parse domain file: ${domainFile}.", e)
      }
    }
    registry = System.getProperty(Constants.PROP_RESTART_DIR)
    if (!registry) {
      registry = System.getProperty("user.home") +
              File.separator + Constants.RESTART_DIR
    } else {
      registry = registry + File.separator + Constants.RESTART_DIR
    }

    boolean ok = true
    File f = new File(registry)
    if (!f.exists()) {
      ok = f.mkdir()
    }
    if (!ok)
      throw new SessionException(
              "Unable to create restart directory \"$registry\".",
              Errno.RESTART_FILE_ERR)

    File keyfile = new File(registry + File.separator + Constants.KEYCHAIN_FILE)
    keychain = new Keychain()

    if (keyfile.exists()) {
      try {
        keychain.load(keyfile.text)
      } catch (JAXBException e) {
        log.debug(e.message, e)
        throw new SessionException("Unable to parse keychain file: ${keyfile.name}.", e)
      } catch (SAXException e) {
        log.debug(e.message, e)
        throw new SessionException("Unable to parse domain file: ${keyfile.name}.", e)
      }
    }
  }


  int add(String productType, String regexp) throws SessionException {
    log.trace "here"
    String federation = getFederation(productType)
    if (!federation) {
      throw new SessionException("Federation is not set.", Errno.MISSING_FEDERATION)
    }
    Request cmd = new Request(federation, productType,
            currentDir, Opcode.CLIENT_ADD,
            regexp)
    cmd.modifier = Modifier.REGEXP
    return getServerProxy(federation).put(cmd)
  }

  int add(String productType, String[] productNames) throws SessionException {
    log.trace "here"
    String federation = getFederation(productType)
    if (!federation) {
      throw new SessionException("Federation is not set.", Errno.MISSING_FEDERATION)
    }
    Request cmd = new Request(federation, productType,
            currentDir, Opcode.CLIENT_ADD,
            productNames)
    return getServerProxy(federation).put(cmd)
  }

  int replace(String productType, String originalProductName, String newProductFile) throws SessionException {
    String federation = getFederation(productType)
    if (!federation) {
      throw new SessionException("Federation is not set.", Errno.MISSING_FEDERATION)
    }

    Request cmd = new Request(federation, productType, currentDir, Opcode.CLIENT_REPLACE,
            (String[]) [newProductFile])
    cmd.originalProduct = originalProductName
    return getServerProxy(federation).put(cmd)

  }

  int replace(String productType, String regexp) throws SessionException {
    String federation = getFederation(productType)
    if (!federation) {
      throw new SessionException("Federation is not set.", Errno.MISSING_FEDERATION)
    }
    Request cmd = new Request(federation, productType,
            currentDir, Opcode.CLIENT_REPLACE,
            regexp)
    cmd.modifier = Modifier.REGEXP    
    return getServerProxy(federation).put(cmd)
  }

  int replace(String productType, String[] productNames) throws SessionException {
    String federation = getFederation(productType)
    if (!federation) {
      throw new SessionException("Federation is not set.", Errno.MISSING_FEDERATION)
    }
    Request cmd = new Request(federation, productType,
            currentDir, Opcode.CLIENT_REPLACE,
            productNames)
    return getServerProxy(federation).put(cmd)
  }

  int list(String productType, String regexp) throws SessionException {
    String federation = getFederation(productType)
    if (!federation) {
      throw new SessionException("Federation is not set.", Errno.MISSING_FEDERATION)
    }
    Request cmd = new Request(federation, productType,
            currentDir, Opcode.CLIENT_LIST, regexp)
    return getServerProxy(federation).put(cmd)
  }

  int list(String productType, String[] productNames) throws SessionException {
    String federation = getFederation(productType)
    if (!federation) {
      throw new SessionException("Federation is not set.", Errno.MISSING_FEDERATION)
    }
    Request cmd = new Request(federation, productType,
            currentDir, Opcode.CLIENT_LIST,
            productNames)
    return getServerProxy(federation).put(cmd)
  }

  int logout() {
    int transactionId = -1
    synchronized(federationProxyMap) {
       Iterator iter = federationProxyMap.values().iterator()
       while (iter.hasNext()) {
          ServerProxy proxy = iter.next()
          if (proxy) {
             Request cmd = new Request(
                Opcode.LOGOUT)
             transactionId = proxy.putExpedited(cmd)
             log.trace("Logout of " + proxy.federation + ", transaction ID " + transactionId)
             iter.remove()
          }
       }
    }
    return transactionId
  }

}