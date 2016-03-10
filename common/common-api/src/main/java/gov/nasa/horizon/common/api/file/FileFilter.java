/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.file;

import java.util.HashSet;
import java.util.Set;

/**
 * File filter class used by the crawler to screen for files. The filter supports filetering by file name regular
 * expression, and time-based query.
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id:$
 */
public class FileFilter {

   /**
    * time query mode.
    *
    * @author thuang
    */
   public enum TIME_FILTER_MODE {
      BEFORE, AFTER, BETWEEN, NONE
   }

   private TIME_FILTER_MODE _mode = TIME_FILTER_MODE.NONE;
   private long[] _filterValues = {-1, -1};
   private String[] _regexps = {"*"};
   private Set<FileProduct> _excludes = new HashSet<FileProduct>();

   /**
    * Default constructor that accepts all files.
    */
   public FileFilter() {

   }

   /**
    * Constructor that supports time-based query.
    *
    * @param filterValues the time values. this is a list, because when query by bounding range the value must be in
    * pair.
    * @param mode time query mode
    */
   public FileFilter(long[] filterValues, TIME_FILTER_MODE mode) {
      switch (mode) {
         case BEFORE:
         case AFTER:
            this._filterValues = filterValues;
            this._mode = mode;
            break;
      }
   }

   /**
    * Constructor that takes a list of file name regular expressions.
    *
    * @param regexps list of file name regular expressions
    */
   public FileFilter(String[] regexps) {
      if (regexps != null)
         this._regexps = regexps;
   }

   /**
    * Combo constructor to support name expression and time-based query
    *
    * @param regexps file name expressions
    * @param filterValues the values
    * @param mode time query mode
    */
   public FileFilter(String[] regexps, long[] filterValues,
         TIME_FILTER_MODE mode) {
      this(filterValues, mode);
      if (regexps != null)
         this._regexps = regexps;
   }

   /**
    * Method to return the exclusion list. This is used when operate in periodic scan, such as daemon.
    *
    * @return the list of exclude file.
    */
   public synchronized Set<FileProduct> getExcludeList() {
      Set<FileProduct> excludes = new HashSet<FileProduct>();
      for (FileProduct product : this._excludes) {
         excludes.add(product);
      }
      return excludes;
   }

   /**
    * Method to return the list of name expressions
    *
    * @return the list of name expressions
    */
   public String[] getNameExpression() {
      return this._regexps;
   }

   /**
    * Method to return the time filter mode
    *
    * @return the time filter mode
    */
   public TIME_FILTER_MODE getTimeFilterMode() {
      return this._mode;
   }

   /**
    * Method to return the time filter value(s)
    *
    * @return the time filter value(s)
    */
   public long[] getTimeFilterValue() {
      return this._filterValues;
   }

   /**
    * Method to set the exclusion list. This is used when operate in periodic scan, such as daemon.
    *
    * @param excludes the list of files to be excluded.
    */
   public synchronized void setExcludeList(Set<FileProduct> excludes) {
      if (excludes == null)
         return;
      this._excludes = new HashSet<FileProduct>();
      for (FileProduct exclude : excludes) {
         this._excludes.add(exclude);
      }
   }

}
