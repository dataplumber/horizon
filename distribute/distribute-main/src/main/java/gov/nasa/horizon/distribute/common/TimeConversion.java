//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.horizon.distribute.common;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * This class contains time conversion tool.
 *
 * @author clwong
 *
 * @version
 * $Id: TimeConversion.java 4503 2010-01-27 20:40:12Z gangl $
 */
public class TimeConversion {

	/**
	 *  Get the current date in the appropriate format.
	 */
	public static XMLGregorianCalendar currentDate() {
		try {
			TimeZone gmt = TimeZone.getTimeZone("GMT");
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(
					new GregorianCalendar(gmt));
		} catch (DatatypeConfigurationException e) {
			throw new java.lang.Error(e);
		}
	}

	/**
	 *  Convert the date returned from the query into the appropriate format.
	 */
	public static XMLGregorianCalendar convertDate(java.util.Date date) {
		 try {
             if (date == null) {
                     return (null);
             }
             TimeZone gmt = TimeZone.getTimeZone("GMT");
             Calendar tempCal = Calendar.getInstance();
             tempCal.setTime(date);
             GregorianCalendar cal = new GregorianCalendar(gmt);
             cal.setTime(date);
             //cal.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DATE), tempCal.get(Calendar.HOUR_OF_DAY), tempCal.get(Calendar.MINUTE), tempCal.get(Calendar.SECOND));
             //cal.set(Calendar.MILLISECOND, 0);
             return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
     } catch (DatatypeConfigurationException e) {
             throw new java.lang.Error(e);
     }

	}
	
}
