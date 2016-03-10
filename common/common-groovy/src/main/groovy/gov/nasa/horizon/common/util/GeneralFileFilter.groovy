package gov.nasa.horizon.common.util

import java.util.regex.Pattern

/**
 * Created by IntelliJ IDEA.
 * User: thuang
 * Date: Sep 15, 2008
 * Time: 2:21:19 PM
 * To change this template use File | Settings | File Templates.
 */

class GeneralFileFilter implements FileFilter, FilenameFilter {

   /** Flag to treat all files the same, even if directory  */
   final static int DIRECTORY_AS_FILE = 0

   /** Flag to always accept file if it is a directory  */
   final static int DIRECTORY_ALWAYS_ACCEPT = 1

   /** Flag to always reject file if it is a directory  */
   final static int DIRECTORY_ALWAYS_REJECT = 2


   /** reference to the wildcard expression  */
   protected String expression

   /** reference to generated regular expression  */
   private String regExpression

   /** Pattern which takes regular expression  */
   protected Pattern pattern

   /** Flag indicating how directories should be filtered  */
   int directoryFlag

   //---------------------------------------------------------------------

   /**
    * Constructor.  Default expression is null and directory flag is
    * DIRECTORY_AS_FILE.
    */
   GeneralFileFilter() {
      setExpression(null);
      setDirectoryFlag(DIRECTORY_AS_FILE)
   }

   //---------------------------------------------------------------------

   /**
    * Constructor. Default directory flag is DIRECTORY_AS_FILE.
    * @param expression Initial expression used as file filter
    */
   GeneralFileFilter(String expression) {
      this();
      setExpression(expression)
   }

   //---------------------------------------------------------------------

   /**
    * Constructor.
    * @param expression Initial expression used as file filter
    * @param directoryFlag Flag indicating treatment of directories,
    *        one of: DIRECTORY_ALWAYS_ACCEPT, DIRECTORY_ALWAYS_REJECT,
    *        or DIRECTORY_AS_FILE.
    */
   GeneralFileFilter(String expression, int directoryFlag) {
      this(expression);
      setDirectoryFlag(directoryFlag)
   }

   //---------------------------------------------------------------------

   /**
    * Sets the value of the filter expression.
    *
    * @param expression New filter expression, can be null
    */
   void setExpression(String expression) {
      if (this.expression == null && expression == null)
         return
      if (this.expression != null && this.expression.equals(expression))
         return

      this.expression = expression
      this.regExpression = regularize(this.expression)
      this.pattern = Pattern.compile(this.regExpression)
   }

   //---------------------------------------------------------------------

   /**
    * Set the directory flag for this filter.  Parameter must legal flag
    * from DIRECTORY_ALWAYS_ACCEPT, DIRECTORY_ALWAYS_REJECT, or
    * DIRECTORY_AS_FILE as defined in GeneralFileFilter, otherwise
    * an IllegalArgumentException will be thrown.
    * @param dirFlag New directory flag value
    * @throws IllegalArgumentException if flag value is unrecognized.
    */
   void setDirectoryFlag(int dirFlag) throws IllegalArgumentException {
      if (dirFlag == this.directoryFlag)
         return

      if (dirFlag != DIRECTORY_ALWAYS_ACCEPT &&
            dirFlag != DIRECTORY_ALWAYS_REJECT &&
            dirFlag != DIRECTORY_AS_FILE) {
         throw new IllegalArgumentException("Unrecognized flag: " + dirFlag)
      }

      this.directoryFlag = dirFlag
   }

   //---------------------------------------------------------------------

   /**
    * Converts file system wildcard expression to regular expression. Periods
    * (.) are replaced with (\.). Stars (*) are replaced with (.*) Question
    * marks are replaced with (.) Null or empty string is replaced with .*
    *
    * @param string Instance of wildcard expression, can be null
    * @return Associated regular expression.
    */
   protected String regularize(String string) {
      String regEx = string

      if (string == null || string.equals("")) {
         regEx = ".*"
      } else {
         //replace . with \.
         regEx = regEx.replaceAll("\\.", "\\\\.")

         //replace * with .*
         regEx = regEx.replaceAll("\\*", "\\.\\*")

         //replace ? with .
         regEx = regEx.replaceAll("\\?", "\\.")
      }

      return regEx
   }

   //---------------------------------------------------------------------

   /**
    * Tests whether or not the specified abstract pathname should be included in
    * a pathname list.
    *
    * @param pathname The abstract pathname to be tested
    * @return True if and only if pathname should be included
    */
   boolean accept(File pathname) {

      if (pathname.isDirectory()) {
         if (this.directoryFlag == DIRECTORY_ALWAYS_ACCEPT)
            return true
         else if (this.directoryFlag == DIRECTORY_ALWAYS_REJECT)
            return false
      }

      if (this.expression == null)
         return false

      if (this.pattern.matcher(pathname.getName()).matches())
         return true

      return false
   }

   //---------------------------------------------------------------------

   /**
    * Implements the required method for FilenameFilter to match file name
    * against the defined regular expression.
    *
    * @param dir the directory in which the file was found
    * @param name the name of the file
    * @return true if and only if the name should be included in teh file list;
    *         false otherwise.
    */
   boolean accept(File dir, String name) {
      return accept(new File(dir, name))
   }

   //---------------------------------------------------------------------

}
