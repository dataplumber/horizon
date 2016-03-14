/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

import java.text.ParseException;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * This is an implementation of the standard UNIX 'get_opt' function. That
 * provides application programs with commandline input format as follow:
 * 
 * <pre>
 *    jdbq -h -u &lt;username&gt;-p &lt;password&gt;-s &lt;server&gt;-d &lt;database&gt;-c &lt;command&gt;
 * </pre>
 * 
 * <p>
 * Programming example:
 * 
 * <pre>
 * GetOpt opt = new GetOpt(args, &quot;hu:p:s:d:c:&quot;);
 * String str = null;
 * try {
 *   while ((str = opt.nextArg()) != null) {
 *    switch (str.charAt(0)) {
 *    case 'h':
 *       this.printUsage();
 *       break;
 *    case 'u':
 *       username = opt.getArgValue();
 *       break;
 *    case 'p':
 *       password = opt.getArgValue();
 *       break;
 *    case 's':
 *       server = opt.getArgValue();
 *       break;
 *    case 'd':
 *       database = opt.getArgValue();
 *       break;
 *    default:
 *       System.out.println(&quot;Error&quot;);
 *    }
 *  }
 * } catch (ParseException pe) {
 *    ...
 * }
 * </pre>
 * 
 * @author T. Huang, {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public class GetOpt {

   /** The internal argument string array. */
   private String[] _args = null;

   /** Reference to the current argument keyword. */
   private String _currentArg = null;

   /** The flag to indicate the current argument has an option argument value. */
   private boolean _hasArg = false;

   /** The internal index to keep track of the current input array offset. */
   private int _index = 0;

   /** The internal data structure to store options argument. */
   protected Hashtable<String, String> _argTable = null;

   /** The internal flag to determine if arguments should be case sensitive. */
   protected boolean _ignoreCase = false;

   /** The argument prefix. */
   private String _prefix = null;

   /**
    * Constructor with argument case sensitive and default argument prefix (e.g.
    * the '-' in argument "-u").
    * 
    * @param args the input argument array.
    * @param optstring the input options string. A colon in the string means
    *           that the previous character is an option that it expects an
    *           argument.
    */
   public GetOpt(String[] args, String optstring) {
      this(args, optstring, false, new String("-"));
   }

   /**
    * Constructor.
    * 
    * @param args the input argument array.
    * @param optstring the input options string. A colon in the string means
    *           that the previous character is an option that it expects an
    *           argument.
    * @param prefix the prefix string
    * @param ignoreCase the flag to determin argument case sensitivity.
    */
   public GetOpt(String[] args, String optstring, boolean ignoreCase,
         String prefix) {
      this._args = args;
      this._hasArg = false;
      this._ignoreCase = ignoreCase;
      this._prefix = prefix;
      this._init(optstring);
   }

   /**
    * Method to initialize the internal option argument data structure.
    * 
    * @param optstring the options string (e.g. "hu:p:s:d:")
    */
   protected synchronized void _init(String optstring) {
      this._argTable = new Hashtable<String, String>();

      // tokenize the input string by ":" delimiter.
      StringTokenizer tokenSet = new StringTokenizer(optstring, ":");

      while (tokenSet.hasMoreTokens()) {
         String token = tokenSet.nextToken();

         // register every command args that have not option values.
         for (int i = 0; i < token.length() - 1; ++i)
            this._argTable
                  .put(String.valueOf(token.charAt(i)), new String("#"));

         // assume the last arg is the one that requires an option value.
         this._argTable.put(String.valueOf(token.charAt(token.length() - 1)),
               new String(":"));
      }
   }

   /**
    * Method to return the current argument value.
    * 
    * @return the current argument value string. It returns null if the
    *         programmed keyword does not expect to have a value.
    * @throws ParseException if argument is expected but not found.
    */
   public synchronized String getArgValue() throws ParseException {

      if (this._hasArg) {

         if (this._prefix != null && !this._prefix.equalsIgnoreCase("")
               && this._args[this._index].startsWith(this._prefix)) {
            throw new ParseException("No argument found for "
                  + this._currentArg, -1);
            // return null;
         }

         // adding a safty net here in case we walk walk beyond
         // the arry bound.
         if (this._index >= this._args.length)
            throw new ParseException("No argument found for "
                  + this._currentArg, -1);

         return this._args[this._index++];
      } else
         return null;
   }

   /**
    * Method to return the next argument keyword.
    * 
    * @return a character object represent the current keyword character.
    */
   public synchronized String nextArg() {
      this._currentArg = null;
      if (this._args == null) {
         return this._currentArg;
      }

      if (this._index < this._args.length) {
         String arg = this._args[this._index];
         String value = this._args[this._index++];
         int strIndex = 0;
         if (this._ignoreCase)
            arg = arg.toLowerCase();
         if (this._prefix != null) {
            if (!arg.startsWith(this._prefix)) {
               this._currentArg = value;
               return this._currentArg;
            }
            // since prefix is specified, we need to skip over the prefix to
            // get to the actural argument keyword.
            strIndex += this._prefix.length();
         }

         String str = (String) this._argTable.get(arg.substring(strIndex));
         // return a space when the token is not found. We can't return
         // null here, since GetOpt can be process at any point during the
         // application's execution cycle.

         if (str == null) {
            this._currentArg = new String(value.substring(strIndex));
            return this._currentArg;
         }

         if (str.equals("#")) {
            this._hasArg = false;
            this._currentArg = new String(value.substring(strIndex));
         } else if (str.equals(":")) {
            this._hasArg = true;
            this._currentArg = new String(value.substring(strIndex));
         } else {
            this._currentArg = null;
         }
         return this._currentArg;
      }

      return this._currentArg;
   }

   /**
    * Method to reset the internal reference counter, so application program can
    * reuse the already initialized object instance of this class.
    */
   public synchronized void rewind() {
      this._hasArg = false;
      this._index = 0;
   }
}