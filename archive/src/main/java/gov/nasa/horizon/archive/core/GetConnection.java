/*
 * Created on Jul 8, 2010
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gov.nasa.horizon.archive.core;

import gov.nasa.horizon.archive.exceptions.ArchiveException;
import gov.nasa.horizon.inventory.api.Constant.ProductArchiveStatus;

import java.net.URL;
import java.net.URLConnection;

final class GetConnection
{
   final private class Open implements Runnable
   {
      public void run()
      {
         synchronized (GetConnection.this.actor)
         {
            try
            {
               if (GetConnection.this.url != null) GetConnection.this.connection = GetConnection.this.url.openConnection();
               GetConnection.this.actor.notifyAll();
            }
            catch (Throwable t) { GetConnection.this.thrown = t; }
         }
      }
   }
   
   final private int timeout;
   final private Object lock = new Object();
   final private Thread actor;
   private Throwable thrown = null;
   private URL url;
   private URLConnection connection = null;

   private GetConnection()
   {
      int timeout = 30000;
      
      try
      {
         String to = System.getProperty ("gov.nasa.horizon.archive.core.http.timeout", "60");
         
         timeout = Integer.parseInt (to) * 1000;
      }
      finally
      {
         this.actor = new Thread(new Open());
         this.actor.setDaemon (true);
         this.timeout = timeout;
      }
   }
   static synchronized GetConnection getInstance()
   {
      return new GetConnection();
   }
   
   public URLConnection open (URL url) throws ArchiveException
   {
      synchronized (this.lock)
      {
         this.url = url;
         synchronized (this.actor)
         {
            this.actor.start();
            try { this.actor.wait (this.timeout); }
            catch (InterruptedException ie) { this.thrown = ie; }
         }
         
         if (this.thrown != null) throw new ArchiveException(ProductArchiveStatus.MISSING.toString(),
                                                             ProductArchiveStatus.MISSING.toString() + ": " + "URL " + url.toString() + "\n" + this.thrown.getMessage());
         if (this.connection == null) throw new ArchiveException(ProductArchiveStatus.MISSING.toString(),
                                                                 ProductArchiveStatus.MISSING.toString() + ": " + "URL " + url.toString() + " timed out.");
      }
      return this.connection;
   }
}
