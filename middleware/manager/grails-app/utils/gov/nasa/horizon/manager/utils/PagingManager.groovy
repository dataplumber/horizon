/*
* Copyright (c) 2008-2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.utils

/**
 *
 * @author T. Huang
 * @version $Id: $
 */
public class PagingManager {
   private int entriesPerPage
   private int entries
   private int page

   public PagingManager(int entriesPerPage, int entries, int page) {
      this.entriesPerPage = entriesPerPage
      this.page = page
      this.entries = entries

      def pages = this.getPages();
      if(this.page > pages) {
         this.page = pages
      }
      if(this.page < 1) {
         this.page = 1
      }
   }

   public int getPages() {
      return (this.entries > 0) ? (int)Math.ceil(this.entries / this.entriesPerPage) : 0
   }

   public int getStart() {
      return (this.page > 0) ? ((this.page - 1) * this.entriesPerPage) : 0
   }

   public int getEnd() {
      int end = (this.page > 0) ? ((this.page * this.entriesPerPage) - 1) : 0
      if(end >= this.entries) {
         end = (this.entries - 1)
      }
      if(end < 0) {
         end = 0
      }

      return end
   }

   public List getRange() {
      def start = this.getStart()
      def end = this.getEnd()

      return (start..end)
   }
}
