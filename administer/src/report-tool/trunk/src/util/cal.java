package util;

import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class cal {
    private static Calendar CALENDAR = Calendar.getInstance();

    public static void main(String[] args) {
      cal c = new cal();
      System.out.println("****** previous Monday: "+ c.dateOfPreviousMonday());
    }

    // Returns the date of the Monday before the last
    public static String dateOfMonBeforeLast () {
      Date today = new Date();
      // get last Monday
      long dm = getDay(today.getTime(), Calendar.MONDAY, -1);
      // get the Sunday before last Monday
      dm = getDay(dm, Calendar.SUNDAY, -1);
      // get the Monday before the last
      dm = getDay(dm, Calendar.MONDAY, -1);
      Date MonBeforeLast = new Date();
      MonBeforeLast.setTime(dm);
      String dateMonBeforeLast = parseData(1900+MonBeforeLast.getYear())+
                           "-"+parseData(MonBeforeLast.getMonth()+1)+
                           "-"+parseData(MonBeforeLast.getDate());
      return dateMonBeforeLast;
    }


    // Returns the date of previous Monday
    public static String dateOfPreviousMonday() {
      Date today = new Date();
      //Date today = new Date(107, 1, 29);
      System.out.println("****** today is: "+ today.toString());

      Date previousMon = new Date();
      long dm = getDay(today.getTime(), Calendar.MONDAY, -1);
      previousMon.setTime(dm);

      //GregorianCalendar cal1 = new GregorianCalendar();
      //cal1.setGregorianChange(previousMon);

      String datePrevMon = parseData(1900+previousMon.getYear())+
                           "-"+parseData(previousMon.getMonth()+1)+
                           "-"+parseData(previousMon.getDate());
      //return previousMon.toString();
      return datePrevMon;
    }


    // Returns the day of the date in the previous or next week given a date
    // and startOfWeek
    private static long getDay(long date, int startOfWeek, int increment) {
        Calendar calendar = CALENDAR;
        synchronized(calendar) {
            calendar.setTimeInMillis(date);
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            // Normalize the view starting date to a week starting day
            while (day != startOfWeek) {
                calendar.add(Calendar.DATE, increment);
                day = calendar.get(Calendar.DAY_OF_WEEK);
            }
            return startOfDayInMillis(calendar.getTimeInMillis());
        }
    }


    // Returns a time in Millis
    public static long startOfDayInMillis(long date) {
        Calendar calendar = CALENDAR;
        synchronized(calendar) {
            calendar.setTimeInMillis(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            return calendar.getTimeInMillis();
        }
    }


    // Adds a 0 to a single digit
    public static String parseData(int data) {
      String r;

      if(data <= 9)
        return r="0"+String.valueOf(data);
      else
        return r=String.valueOf(data);
    }
}


