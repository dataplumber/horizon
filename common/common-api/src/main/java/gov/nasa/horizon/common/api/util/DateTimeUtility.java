/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/


package gov.nasa.horizon.common.api.util;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;


/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: DateTimeUtility.java 244 2007-10-02 20:12:47Z axt $
 *
 */
public class DateTimeUtility {
   
   private static final String[] _DATE_TIME_FORMAT_ISO_8601_WITH_UTC = {
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss.SSS"
   };
   
   public static final TimeZone TIME_ZONE_UTC = TimeZone.getTimeZone("Etc/UTC");
   private static SimpleDateFormat _simpleDateFormat = new SimpleDateFormat();
   
   private static SimpleDateFormat _getSimpleDateFormat(String format) {
      DateTimeUtility._simpleDateFormat.applyPattern(format);
      DateTimeUtility._simpleDateFormat.setTimeZone(TIME_ZONE_UTC);
      
      
      return DateTimeUtility._simpleDateFormat;
   }
   
   /**
    * Completes an hh:mm:ss.SSS time strings with 0's, does some rudimentary checks
    * to see if the year is within a valid range (i.e. 4 digit years only).
    *
    * @param datetime time string to be completed
    * @param hasT true if time string format requires a "T" between
    * date and time
    * @return the formated time string value
    * @throws Exception when general failure
    */
   public static String completeTimeString(String datetime, boolean hasT)
       throws Exception {
       StringTokenizer st = new StringTokenizer(datetime, ":.");
       int nt = st.countTokens();
       if (nt == 4)
           return datetime;

       // The first token will contain the entire 'date part' of the
       // datetime and possibly a 'T' followed by time input.   
       String ns = st.nextToken();

       // Check to see if year part of date is four characters long, the fifth character will be
       // an '-'.  If the fifth character is not an '-', throw an exception.
       // Examples of valid input: 2003-06-01 or 9999-01-01 or 1976-09-24
       // Examples of invalid input: 200-06-01 or 20000-01-01 or 03-05-01
       if (ns.length() > 4) {
           if (ns.charAt(4) != '-') {
               throw new Exception("Invalid year input: ");
           }

           // Year input is too short: 200 or 199 or 02
       } else {
           throw new Exception("Invalid year input: ");
       }

       if (st.hasMoreTokens())
           ns += ":" + st.nextToken();
       else {
           if (hasT) {
               if (ns.indexOf("T") > -1)
                   ns += ":00:00.000";
               else
                   ns += "T00:00:00.000";
           } else {
               if (ns.indexOf(" ") > -1)
                   ns += ":00:00.000";
               else
                   ns += " 00:00:00.000";
           }
           return ns;
       }

       if (st.hasMoreTokens())
           ns += ":" + st.nextToken();
       else {
           ns += ":00.000";
           return ns;
       }

       if (st.hasMoreTokens()) {
           ns += "." + st.nextToken();
       } else {
           ns += ".000";
           return ns;
       }
       return datetime;
   }
   
   /**
    * Converts DB formatted date yyyy-MM-dd hh:mm:ss.SSS to
    * YYYY-MM-DDThh:mm:ss.SSS (all dates GMT)
    *
    * @param datetime - time string representation  yyyy-MM-dd hh:mm:ss.SSS
    * @return time string representation yyyy-MM-ddThh:mm:ss.SSS
    * @throws ParseException when string parse fail
    */
   public static String convertFromDBFormat(String datetime)
       throws ParseException {
       SimpleDateFormat format = DateTimeUtility.getDBFormat();
       try {
           Date date =
               format.parse(DateTimeUtility.completeTimeString(datetime, false));
           return DateTimeUtility.getDateCCSDSAString(date);
       } catch (ParseException pe) {
           throw new ParseException(
               "Invalid date-time: "
                   + datetime
                   + ", format: yyyy-MM-dd[Thh:mm:ss.SSS]",
               0);

       } catch (Exception e) {
           throw new ParseException(
               e.getMessage()
                   + datetime
                   + ", format: yyyy-MM-dd[Thh:mm:ss.SSS]",
               0);
       }

   }
   
   /**
    * Returns instance of a Date object, from a string formatted in CCSDS ASCII
    * date type A.  This format is also known as ISO 8601
    * (dateTime format in XML).  Completion of hh:mm:ss.ddd with 0's if needed.
    *
    * @param datetime time string representation YYYY-MM-DDThh:mm:ss.ddd
    * @return the converted Date object reference
    * @throws ParseException when string parse fail
    */
   public static Date getCCSDSADate(String datetime) throws ParseException {
       SimpleDateFormat format = DateTimeUtility.getCCSDSAFormat();
       try {
           String s1 = DateTimeUtility.completeTimeString(datetime, true);
           return format.parse(s1);
       } catch (ParseException pe) {
           throw new ParseException(
               "Invalid date-time: "
                   + datetime
                   + ", format: YYYY-MM-DD[Thh:mm:ss.ddd]",
               0);

       } catch (Exception e) {
           throw new ParseException(
               e.getMessage()
                   + datetime
                   + ", format: yyyy-MM-dd[Thh:mm:ss.SSS]",
               0);
       }
   }
   
   /**
    * Returns instance of a Date object, from a string formatted in CCSDS ASCII
    * date type A. (GMT).  This format is also known as ISO 8601
    * (dateTime format in XML) Completion of hh:mm:ss.ddd with 0's if needed.
    *
    * @param datetime - time string representation YYYY-MM-DDThh:mm:ss.ddd
    * @return the converted Date object
    * @throws ParseException when string parsing fail
    */
   public static Date getCCSDSADateGMT(String datetime)
       throws ParseException {
       SimpleDateFormat format = DateTimeUtility.getCCSDSAFormat();
       format.setTimeZone(TimeZone.getTimeZone("GMT"));
       try {
           String s1 = DateTimeUtility.completeTimeString(datetime, true);
           return format.parse(s1);
       } catch (ParseException pe) {
           throw new ParseException(
               "Invalid date-time: "
                   + datetime
                   + ", format: YYYY-MM-DD[Thh:mm:ss.ddd]",
               0);

       } catch (Exception e) {
           throw new ParseException(
               e.getMessage()
                   + datetime
                   + ", format: yyyy-MM-dd[Thh:mm:ss.SSS]",
               0);
       }
   }
   
   /**
    * Returns instance of a SimpleDateFormat where the
    * format has been set to yyyy-mm-ddThh:mm:ss.SSS
    * XXX Not sure of the name of this format.
    *
    * @return a SimpleDateFormat object reference
    */
   private static SimpleDateFormat getCCSDSAFormat() {
       SimpleDateFormat format =
           new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH:mm:ss.SSS");
       format.setLenient(false);
       return format;
   }
   
   /**
    * Get current Date (GMT)
    * 
    * @return current Date object reference
    */
   public static Date getCurrentDate() {
      Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
      return cal.getTime();
   }

   /**
    * Get current date in CCSDSA format
    *
    * @return converted date string
    */
   public static String getCurrentDateCCSDSAString() {
       SimpleDateFormat format = DateTimeUtility.getCCSDSAFormat();
       return format.format(DateTimeUtility.getCurrentDate());
   }

   /**
    * Get current date in DB format
    *
    * @return converted date string
    */
   public static String getCurrentDateDBString() {
       SimpleDateFormat format = DateTimeUtility.getDBFormat();
       return format.format(DateTimeUtility.getCurrentDate());
   }

   /**
    * Get current date in mm/dd/yyyy hh:mm:ss.SSS format
    *
    * @return converted date string
    */
   public static String getCurrentDateString() {
       SimpleDateFormat format = DateTimeUtility.getFormat();
       return format.format(DateTimeUtility.getCurrentDate());
   }

   /**
    * Get current date in UTC format
    *
    * @return converted date string
    */
   public static String getCurrentDateUTCString() {
       SimpleDateFormat format = DateTimeUtility.getUTCFormat();
       return format.format(DateTimeUtility.getCurrentDate());
   }

   /**
    * Accessor method to returns instance of a Date object.  Completion of
    * hh:mm:ss.SSS with 0's if needed
    *
    * @param datetime - time string representation MM/dd/yyyy hh:mm:ss.SSS
    * @return the Date
    * @throws ParseException when parsing failure
    */
   public static Date getDate(String datetime) throws ParseException {
       SimpleDateFormat format = DateTimeUtility.getFormat();
       try {
           String s1 = DateTimeUtility.completeTimeString(datetime, false);
           return format.parse(s1);
       } catch (ParseException pe) {
           throw new ParseException(
               "Invalid date-time: "
                   + datetime
                   + ", format: MM/dd/yyyy[ hh:mm:ss.SSS]",
               0);

       } catch (Exception e) {
           throw new ParseException(
               e.getMessage()
                   + datetime
                   + ", format: yyyy-MM-dd[Thh:mm:ss.SSS]",
               0);
       }
   }

   /**
    * Method to return CCSDSA string representiaion for the input Date object
    *
    * @param date the input Date object
    * @return converted date string
    */
   public static String getDateCCSDSAString(Date date) {
       SimpleDateFormat format = DateTimeUtility.getCCSDSAFormat();
       return format.format(date);
   }

   /**
    * Method to return DB string representiaion for the input Date object
    *
    * @param date the input Date object
    * @return converted date string
    */
   public static String getDateDBString(Date date) {
       SimpleDateFormat format = DateTimeUtility.getDBFormat();
       return format.format(date);
   }

   /**
    * Method to return mm/dd/yyyy hh:mm:ss.SSS string representiaion of an
    * input Date object.
    *
    * @param date the input Date object
    * @return converted date string
    */
   public static String getDateString(Date date) {
       SimpleDateFormat format = DateTimeUtility.getFormat();
       return format.format(date);
   }

   /**
    * Method to return get UTC string representiaion for the input Data object
    *
    * @param date the input Date object
    * @return converted date string
    */
   public static String getDateUTCString(Date date) {
       SimpleDateFormat format = DateTimeUtility.getUTCFormat();
       return format.format(date);
   }

   /**
    * Extract day of month from mm/dd/yyyy hh:mm:ss.SSS format (GMT)
    *
    * @param datetime time string representation mm/dd/yyyy hh:mm:ss.SSS
    * @return month value in integer
    * @throws ParseException when invalid input date string
    */
   public static int getDayOfMonth(String datetime) throws ParseException {
       Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
       cal.setTime(DateTimeUtility.getDate(datetime));
       return cal.get(Calendar.DAY_OF_MONTH);
   }

   /**
    * Returns instance of a Date object.  Completion of hh:mm:ss.SSS with 0's
    * if needed.
    *
    * @param datetime time string representation yyyy-mm-dd hh:mm:ss.SSS
    * @return the converted Date object reference
    * @throws ParseException when string parse fail
    */
   public static Date getDBDate(String datetime) throws ParseException {
       SimpleDateFormat format = DateTimeUtility.getDBFormat();
       try {
           String s1 = DateTimeUtility.completeTimeString(datetime, false);
           return format.parse(s1);
       } catch (ParseException pe) {
           throw new ParseException(
               "Invalid date-time: "
                   + datetime
                   + ", format: [yyyy-mm-dd hh:mm:ss.SSS]",
               0);

       } catch (Exception e) {
           throw new ParseException(
               e.getMessage()
                   + datetime
                   + ", format: yyyy-MM-dd[Thh:mm:ss.SSS]",
               0);
       }
   }

   /**
    * Returns instance of a SimpleDateFormat where the
    * format has been set to yyyy-MM-dd hh:mm:ss.SSS
    *
    * @return a SimpleDateFormat object reference
    */
   private static SimpleDateFormat getDBFormat() {
       SimpleDateFormat format =
           new SimpleDateFormat("yyyy'-'MM'-'dd HH:mm:ss.SSS");
       format.setLenient(false);
       return format;
   }

   /**
    * Returns instance of a SimpleDateFormat where the format has been set to
    * MM/dd/yyyy hh:mm:ss.SSS, GMT
    *
    * @return a SimpleDateFormat object reference
    */
   private static SimpleDateFormat getFormat() {
       SimpleDateFormat format =
           new SimpleDateFormat("MM'/'dd'/'yyyy HH:mm:ss.SSS");
       format.setLenient(false);
       return format;
   }

   /**
    * Extract hour from mm/dd/yyyy hh:mm:ss.SSS format (GMT)
    *
    * @param datetime time string representation mm/dd/yyyy hh:mm:ss.SSS
    * @return hour value in integer
    * @throws ParseException when invalid input date string
    */
   public static int getHour(String datetime) throws ParseException {
       Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
       cal.setTime(DateTimeUtility.getDate(datetime));
       return cal.get(Calendar.HOUR_OF_DAY);
   }

   /**
    * Get local Date (PST)
    *
    * @return current Date object reference
    */
   public static Date getLocalDate() {
       Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("PST"));
       return cal.getTime();
   }

   /**
    * Get local Date (PST)
    *
    * @param time local time in milliseconds
    * @return current Date object reference
    */
   public static Date getLocalDate(long time) {
       Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("PST"));
       cal.setTimeInMillis(time);
       return cal.getTime();
   }

   /**
    * Method to return current date in mm/dd/yyyy hh:mm:ss.SSS format (PST)
    *
    * @return PST date string
    */
   public static String getLocalDateString() {
       SimpleDateFormat format = DateTimeUtility.getLocalFormat();
       return format.format(DateTimeUtility.getLocalDate());
   }

   /**
    * Method to return date in mm/dd/yyyy hh:mm:ss.SSS format (PST)
    *
    * @param date the input Date object
    * @return PST date string
    */
   public static String getLocalDateString(Date date) {
       SimpleDateFormat format = DateTimeUtility.getLocalFormat();
       return format.format(date);
   }

   /**
    * Returns instance of a SimpleDateFormat where the format has been set to
    * MM/dd/yyyy hh:mm:ss.SSS, PST
    *
    * @return a SimpleDateFormat object for the local time zone
    */
   private static SimpleDateFormat getLocalFormat() {
       SimpleDateFormat format =
           new SimpleDateFormat("MM'/'dd'/'yyyy HH:mm:ss.SSS");
       format.setTimeZone(TimeZone.getTimeZone("PST"));
       format.setLenient(false);
       return format;
   }

   /**
    * Extract millisecond from mm/dd/yyyy hh:mm:ss.SSS format (GMT)
    *
    * @param datetime time string representation mm/dd/yyyy hh:mm:ss.SSS
    * @return millisecond value in integer
    * @throws ParseException when invalid input date string
    */
   public static int getMilliseconds(String datetime) throws ParseException {
       Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
       cal.setTime(DateTimeUtility.getDate(datetime));
       return cal.get(Calendar.MILLISECOND);
   }

   /**
    * Extract minute from mm/dd/yyyy hh:mm:ss.SSS format (GMT)
    *
    * @param datetime time string representation mm/dd/yyyy hh:mm:ss.SSS
    * @return minute value in integer
    * @throws ParseException when invalid input date string
    */
   public static int getMinute(String datetime) throws ParseException {
       Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
       cal.setTime(DateTimeUtility.getDate(datetime));
       return cal.get(Calendar.MINUTE);
   }

   /**
    * Extract month from mm/dd/yyyy hh:mm:ss.SSS format (GMT)
    *
    * @param datetime string representation mm/dd/yyyy hh:mm:ss.SSS
    * @return month value in integer
    * @throws ParseException when invalid input date string
    */
   public static int getMonth(String datetime) throws ParseException {
       Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
       cal.setTime(DateTimeUtility.getDate(datetime));
       return cal.get(Calendar.MONTH);
   }

   /**
    * Extract second from mm/dd/yyyy hh:mm:ss.SSS format (GMT)
    *
    * @param datetime time string representation mm/dd/yyyy hh:mm:ss.SSS
    * @return second value in integer
    * @throws ParseException when invalid input date string
    */
   public static int getSecond(String datetime) throws ParseException {
       Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
       cal.setTime(DateTimeUtility.getDate(datetime));
       return cal.get(Calendar.SECOND);
   }

   /**
    * Return date/time in the form of a Unix time_t (seconds since 1-Jan-1970)
    *
    * @param datetime time string representation mm/dd/yyyy hh:mm:ss.SSS
    * @return seconds since 1-Jan-1970 in long
    * @throws ParseException when invalid input date string
    */
   public static long getTime_t(String datetime) throws ParseException {
       Date d = DateTimeUtility.getDate(datetime);
       return d.getTime();
   }

   /**
    * Accessor method to return current time as a yyyy-MM-dd hh:mm:ss.SSS
    * string (aka SQL TIMESTAMP format)
    *
    * @return the time string
    */
   public static String getTimestamp() {
       long date = (new Date()).getTime();
       return (new Timestamp(date)).toString();
   }

   /**
    * Returns instance of a Date object.  Completion of hh:mm:ss.SSS with 0's
    * if needed
    *
    * @param datetime - time string representation YYYY-DDDThh:mm:ss.ddd
    * @return the converted Date object reference
    * @throws ParseException when string parsing fail
    */
   public static Date getUTCDate(String datetime) throws ParseException {
       SimpleDateFormat format = DateTimeUtility.getUTCFormat();
       try {
           String s1 = DateTimeUtility.completeTimeString(datetime, false);
           return format.parse(s1);
       } catch (ParseException pe) {
           throw new ParseException(
               "Invalid date-time: "
                   + datetime
                   + ", format: YYYY-DDD[Thh:mm:ss.ddd]",
               0);

       } catch (Exception e) {
           throw new ParseException(
               e.getMessage()
                   + datetime
                   + ", format: yyyy-MM-dd[Thh:mm:ss.SSS]",
               0);
       }
   }

   /**
    * Returns instance of a SimpleDateFormat where the format has been set to
    * yyyy-DDDThh:mm:ss.SSS, GMT
    *
    * @return a SimpleDateFormat object reference
    */
   private static SimpleDateFormat getUTCFormat() {
       SimpleDateFormat format =
           new SimpleDateFormat("yyyy'-'DDD'T'HH:mm:ss.SSS");
       format.setLenient(false);
       return format;
   }

   /**
    * Extract year from mm/dd/yyyy hh:mm:ss.SSS format (GMT)
    *
    * @param datetime time string representation mm/dd/yyyy hh:mm:ss.SSS
    * @return year value in integer
    * @throws ParseException when invalid input date string
    */
   public static int getYear(String datetime) throws ParseException {
       Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
       cal.setTime(DateTimeUtility.getDate(datetime));
       return cal.get(Calendar.YEAR);
   }

   /**
    * Verify the validity of a date time string
    * Note: This is "strict" validation (does not automatically
    * complete hh:mm:ss.sss with zeros)
    *
    * @param datetime time string representation mm/dd/yyyy hh:mm:ss.SSS
    * @return true if input is a valid date time string.
    */
   public static boolean isValid(String datetime) {
       SimpleDateFormat format = DateTimeUtility.getFormat();
       try {
           format.parse(datetime);
           return true;
       } catch (ParseException pe) {
           return false;
       }
   }

   /**
    * Verify the validity of a date time string
    * Note: This is "strict" validation (does not automatically
    * complete hh:mm:ss.sss with zeros)
    *
    * @param datetime CCSDSA time string representation yyyy-mm-ddThh:mm:ss.sss
    * @return true if input is a valid CCSDSA value.
    */
   public static boolean isValidCCSDSA(String datetime) {
       SimpleDateFormat format = DateTimeUtility.getCCSDSAFormat();
       try {
           format.parse(datetime);
           return true;
       } catch (ParseException pe) {
           return false;
       }
   }

   /**
    * Verify the validity of a date time string
    * Note: This is "strict" validation (does not automatically
    * complete hh:mm:ss.sss with zeros)
    *
    * @param datetime DB time string representation yyyy-MM-dd hh:mm:ss.SSS
    * @return true if input is a valid DB format value
    */
   public static boolean isValidDB(String datetime) {
       SimpleDateFormat format = DateTimeUtility.getDBFormat();
       try {
           format.parse(datetime);
           return true;
       } catch (ParseException pe) {
           return false;
       }
   }

   /**
    * Verify the validity of a date time string
    * Note: This is "strict" validation (does not automatically
    * complete hh:mm:ss.sss with zeros)
    *
    * @param datetime UTC time string representation yyyy-dddThh:mm:ss.SSS
    * @return true if input is a valid UTC value.
    */
   public static boolean isValidUTC(String datetime) {
       SimpleDateFormat format = DateTimeUtility.getUTCFormat();
       try {
           format.parse(datetime);
           return true;
       } catch (ParseException pe) {
           return false;
       }
   }

   public static String parseDate(Date date) {
      return parseDate(_DATE_TIME_FORMAT_ISO_8601_WITH_UTC[0], date);
   }

   public static Date parseDate(String date) {
      Date result = null;
      for(String format : _DATE_TIME_FORMAT_ISO_8601_WITH_UTC) {
         result = parseDate(format, date);
         if(result != null) {
            break;
         }
      }
      
      return result;
   }
   
   public static Date parseDateWithTZ(String date, String tz) {
	      Date result = null;
	      String format = "yyyy-MM-dd'T'HH:mm:ss z";
	      
	      //for(String format : _DATE_TIME_FORMAT_ISO_8601_WITH_UTC) {
	      result = parseDate(format, date + " " + tz);
//	         if(result != null) {
//	            break;
//	         }
//	      }
	      
	      return result;
	   }

   public static String parseDate(String format, Date date) {
      SimpleDateFormat sdf = _getSimpleDateFormat(format);
      return sdf.format(date);
   }

   public static Date parseDate(String format, String date) {
      Date result = null;
      try {
         SimpleDateFormat sdf = _getSimpleDateFormat(format);
         result = sdf.parse(date);
      }
      catch(Exception exception) {
    	  exception.printStackTrace();
      }
      
      
      return result;
   }

   protected DateTimeUtility() {
   }
}
