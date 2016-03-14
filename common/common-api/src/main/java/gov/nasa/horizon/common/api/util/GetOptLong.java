/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

import java.util.Hashtable;

import java.util.StringTokenizer;

/**
 * This is an extended implementation of the standard UNIX 'get_opt' function.
 * That provides application programs with long keyword argument commandline
 * input format as follow:
 * 
 * <pre>
 * 
 *   jdbq -help -username &lt;username&gt;-password &lt;password&gt;
 *   -server &lt;server&gt;-database &lt;database&gt;-command &lt;command&gt;
 *  
 * </pre>
 * 
 * <p>
 * Programming example:
 * 
 * <pre>
 * GetOptLong opt =
 *       new GetOptLong(args, &quot;help|username:password:server:database:command:&quot;);
 * String str = null;
 * while ((str = opt.nextArg()) != null) {
 *    if (str.equals(&quot;help&quot;))
 *       this.printUsage();
 *    else if (str.equals(&quot;username&quot;))
 *       username = opt.getArgValue();
 *    else if (str.equals(&quot;password&quot;))
 *       password = opt.getArgValue();
 *    else if (str.equals(&quot;server&quot;))
 *       server = opt.getArgValue();
 *    else if (str.equals(&quot;database&quot;))
 *       database = opt.getArgValue();
 *    else if (str.equals(&quot;command&quot;))
 *       command = opt.getArgValue();
 *    else
 *       System.out.println(&quot;Error&quot;);
 * }
 * </pre>
 * 
 * @author T. Huang, {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */

public class GetOptLong extends GetOpt {

   /**
    * Constructor with argument case sensitive and default argument prefix (e.g.
    * the '-' in argument "-u").
    * 
    * @param args the input argument array.
    * @param optstring the input options string. A colon in the string means
    *           that the previous string is an option that it expects an
    *           argumentand and a '|' in the string means that the previous
    *           string does not require any arguments. (e.g.
    *           "help|username:password:")
    */
   public GetOptLong(String[] args, String optstring) {
      super(args, optstring);
   }

   /**
    * Constructor.
    * 
    * @param args the input argument array.
    * @param optstring the input options string. A colon in the string means
    *           that the previous string is an option that it expects an
    *           argument and a '|' in the string means that the previous string
    *           does not require any arguments. (e.g. "help|username:password:")
    * @param ignoreCase the flag to determin argument case sensitivity.
    * @param prefix the prefix string
    */
   public GetOptLong(String[] args, String optstring, boolean ignoreCase,
         String prefix) {
      super(args, optstring, ignoreCase, prefix);
   }

   /**
    * Override parent method to initialize the internal option arugment data
    * structure.
    * 
    * @param optstring the options string (e.g. "help|username:password:")
    */
   protected synchronized void _init(String optstring) {
      this._argTable = new Hashtable<String, String>();

      // tokenize the input string by ":" delimiter.
      StringTokenizer tokenSet = new StringTokenizer(optstring, ":");

      while (tokenSet.hasMoreTokens()) {
         // tokenize the input token set by "|" delimiter.
         StringTokenizer innerTokenSet =
               new StringTokenizer(tokenSet.nextToken(), "|");
         int count = innerTokenSet.countTokens();
         String token = null;

         // register all arguments that do not require additional options.
         for (int i = 0; i < count - 1; ++i) {
            token = innerTokenSet.nextToken();
            if (this._ignoreCase)
               token = token.toLowerCase();
            this._argTable.put(token, new String("#"));
         }

         // the last token should the one that expects an option input.
         token = innerTokenSet.nextToken();
         this._argTable.put(token, new String(":"));
      }
   }
}