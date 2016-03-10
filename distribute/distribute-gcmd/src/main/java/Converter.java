import java.util.Calendar;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Class to house many of the static methods needed throughout the program
 * @author Dawn Finney
 **/
public class Converter{

	/**
	 * Converts everything in the array to a big <code>String</code>
	 * @param array 	the array in question
	 * @return the big <code>String</code>
	 **/
	public static String arrayToString(String[]array){
		if(array == null)
			return null;
		String ret = "";
		for(String s : array)
			ret = ret + s;
		return ret;
	}
	
	/**
	 * Converts everything in the array to a big <code>String</code>
	 * @param array 	the array in question
	 * @return the big <code>String</code>
	 **/
	public static String arrayToString(Global.MType[]array){
		if(array == null)
			return null;
		String ret = "";
		for(Global.MType s : array)
			ret = ret + s;
		return ret;
	}
	
	/**
	 * Method to change the date YYYYMMDD to acceptable 
	 * format YYYY-MM_DD. If incoming date is "ongoing," 
	 * then an empty <code>String</code> is returned. If the an 
	 * <code>IndexOutOfException</code> is encounter, the
	 * original <code>String</code> is returned.
	 * @param date	the date to convert  
	 * @return the converted <code>String</code>, the original <code>String</code> or <code>""</code>
	 **/
	public static String changeDate(String date){
		if(date.trim().equals("ongoing"))
			return "";
		date = date.replace(".", "").replace("E7", ""); //necessary because the excel does weird things
		try{
			if(date.length() < 8){
				while(date.length() < 8)
					date = date + "0";
			}
			String year = date.substring(0, 4);
			String month = date.substring(4, 6);
			String day = date.substring(6, 8);
			return year + "-" + month + "-" + day;
		}
		catch(IndexOutOfBoundsException e){
			System.out.println("IOBE "+ e.getMessage());
			return date;
		}
	}

	/**
	 * Method to the return the given number in seconds by 
	 * converting it from its original units. Supported units:
	 * minutes, hours, days, weeks, months, years, decades
	 * @param number 		the number in question
	 * @param units 		the units for the number
	 * @return the number in seconds. 
	 **/
	public static double changeToSeconds(double number, String units){
		if(units.equalsIgnoreCase("minutes") || units.equalsIgnoreCase("minute") 
				||units.equalsIgnoreCase("min"))
			return number * 60;
		if(units.equalsIgnoreCase("hours") || units.equalsIgnoreCase("hour") 
				|| units.equalsIgnoreCase("hr"))
			return number * 60 * 60;
		if(units.equalsIgnoreCase("days") || units.equalsIgnoreCase("day"))
			return number * 60 * 60 * 24;
		if(units.equalsIgnoreCase("weeks") || units.equalsIgnoreCase("week"))
			return number * 60 * 60 * 24 * 7;
		if(units.equalsIgnoreCase("months") || units.equalsIgnoreCase("month"))
			return number * 60 * 60 * 24 * 7 * 4;
		if(units.equalsIgnoreCase("years") || units.equalsIgnoreCase("year"))
			return number * 60 * 60 * 24 * 7 * 4 * 12;
		if(units.equalsIgnoreCase("decades") || units.equalsIgnoreCase("decade"))
			return number * 60 * 60 * 24 * 7 * 4 * 12 * 10;
		return number;
	}

	/**
	 * Method to convert the HORIZONTAL_RESOLUTION_RANGE data to a list of 
	 * acceptable ranges. Just returns empty list if the data already
	 * contains - < or -<.
	 * @param data 		the data in question
	 * @return a list of ranges
	 **/
	public static LinkedList<String> convertHorizontalResolutionRange(String data){
		data = data.trim();
		LinkedList<String> ret = new LinkedList<String>();
		if(data.contains("- <") || isEmpty(data) || data.contains("-<"))
			return ret;
		if(data.equalsIgnoreCase("Point Resolution")){
			ret.add("Point Resolution");
			return ret;
		}

		String minUnits = "", maxUnits = "";
		//find units for the maximum
		for(int i = data.length()-1; i > -1; i--){
			if(Character.isLetter(data.charAt(i)))
				maxUnits = data.charAt(i) + maxUnits;
			else
				break;
		}
		data = data.replace(maxUnits, "").replace("decimal", "").trim();
		String min = null, max = data;
		if(data.contains("to") || data.contains("-")){
			int index = data.indexOf("to");
			if(index == -1)
				index = data.indexOf("-");
			min = data.substring(0, index).trim();

			for(int i = min.length()-1; i > -1; i--){
				if(Character.isLetter(min.charAt(i)))
					minUnits = min.charAt(i) + minUnits;
				else
					break;
			}
			min = min.replace(minUnits, "").replace("decimal", "").trim();
			max = data.substring(index+2).trim();
		}
		//just the number versions of the maximum and minimum values
		double minNum = -1, maxNum = Double.parseDouble(max);
		if(min != null)
			minNum = Double.parseDouble(min);

		if(minNum != -1.0 && (minUnits.equalsIgnoreCase("km") || 
				(isEmpty(minUnits) && maxUnits.equalsIgnoreCase("km")))){
			minUnits = "meters";
			minNum = minNum * 1000;
		}
		if(maxUnits.equalsIgnoreCase("km")){
			maxUnits = "meters";
			maxNum = maxNum * 1000;
		}

		return getRanges(minNum, maxNum, maxUnits);
	}

	/**
	 * Method to convert the <b>ENTRY_ID</b> so that all whitespaces 
	 * become underscores. Also removes extra whitespaces between
	 * words so that words are only separated by one character.
	 * @param title 	the <code>String</code> in question
	 * @return the <code>String</code> with whitespaces changed to underscores
	 **/
	public static String convertID(String title){
		return title.trim().replace(" ", "_");
	}

	/**
	 * Method to convert the TEMPORAL_RESOLUTION_RANGE data to a list of 
	 * acceptable ranges. Just returns empty list if the data already
	 * contains - < or -<.
	 * @param data 		the data in question
	 * @return a list of ranges
	 **/
	public static LinkedList<String> convertTemporalResolutionRange(String data){
		data = data.trim();
		if(data.contains("- <") || isEmpty(data) || data.contains("-<"))
			return new LinkedList<String>();

		String minUnits = "", maxUnits = "";
		//find units for the maximum
		for(int i = data.length()-1; i > -1; i--){
			if(Character.isLetter(data.charAt(i)))
				maxUnits = data.charAt(i) + maxUnits;
			else
				break;
		}
		//remove the maxUnits
		data = data.replace(maxUnits, "").trim();

		String min = null, max = data;
		if(data.contains("to") || data.contains("-")){
			int index = data.indexOf("to");
			if(index == -1)
				index = data.indexOf("-");
			min = data.substring(0, index).trim();

			for(int i = min.length()-1; i > -1; i--){
				if(Character.isLetter(min.charAt(i)))
					minUnits = min.charAt(i) + minUnits;
				else
					break;
			}
			min = min.replace(minUnits, "").trim();
			max = data.substring(index+2).trim();
		}

		//just the number versions of the maximum and minimum values
		double minNum = -1, maxNum = Double.parseDouble(max);
		if(min != null)
			minNum = Double.parseDouble(min);
		if(minNum!= -1){
			if(isEmpty(minUnits))
				minNum = changeToSeconds(minNum, maxUnits);
			else
				minNum = changeToSeconds(minNum, minUnits);
		}
		maxNum = changeToSeconds(maxNum, maxUnits);

		return getRanges(minNum, maxNum, "second");
	}

	/**
	 * @return the current date in YYYY-MM-DD format
	 **/
	public static String currentDate(){
		Calendar now = Calendar.getInstance();
		String month = (now.get(Calendar.MONTH)+1) + "";
		String day = (now.get(Calendar.DAY_OF_MONTH)) + "";
		if(month.length() < 2)
			month = "0" + month;
		if(day.length() < 2)
			day = "0" + day;
		return now.get(Calendar.YEAR) + "-" + month + "-" + day;
	}

	/**
	 * Returns a String representation of the current time
	 * separated by the incoming delimiter
	 * @param sep 		the delimiter
	 * @return a String representation of the current time separated by sep
	 **/
	public static String currentTime(String sep){
		Calendar now = Calendar.getInstance();
		return now.get(Calendar.HOUR_OF_DAY) + sep 
		+ (now.get(Calendar.MINUTE)) + sep 
		+ (now.get(Calendar.SECOND));
	}

	/**
	 * Returns the ranges for the given minimum and maximum. It is possible
	 * for there to be no minimum (denoted by -1 or -1.0 as it is a double). Only one
	 * unit is necessary because double numbers are converted to meters, degrees 
	 * or seconds to aid comparison. This method will return a LinkedList containing
	 * all the possible ranges. Because of the way the ranges are broken down, it is 
	 * possible to belong to more than category. This method will find the range for 
	 * the maximum value and then if there is a minimum the range for the minimum. Then
	 * ranges are then the ones for the maximum value and minimum value and all those 
	 * in between.
	 * @param minNumber 		the minimum in the range
	 * @param maxNumber 		the maximum in the range
	 * @param units				the units for the range
	 * @return all the acceptable ranges for the numbers 
	 **/
	public static LinkedList<String> getRanges(double minNumber, double maxNumber, String units){
		LinkedList<String> linky = new LinkedList<String>();
		Global.MType[] compareArray = null;
		if(units.equals("meter") || units.substring(0, units.length()-1).equals("meter"))
			compareArray = Global.allMeterTypes;
		else if (units.equals("degree") || units.substring(0, units.length()-1).equals("degree"))
			compareArray = Global.allDegreeTypes;
		else if (units.equals("second") || units.substring(0, units.length()-1).equals("second"))
			compareArray = Global.allSecTypes;
		if(compareArray!=null){
			int maxIndex = -1, minIndex = -1;
			for(int i = 0; i < compareArray.length; i++){
				if(compareArray[i].inRange(maxNumber)){
					maxIndex = i;
					linky.add(compareArray[i].dif);
					break;
				}
			}
			if(minNumber != -1){
				for(int i = 0; i < compareArray.length; i++){
					if(compareArray[i].inRange(minNumber)){
						minIndex = i;
						break;
					}
				}
				if(minIndex == -1)
					System.out.println(minNumber);
				for(int i = minIndex; i < maxIndex; i++)
					linky.add(compareArray[i].dif);
			}
		}
		return linky;
	}

	/**
	 * Method to determine if a <code>String</code> is empty. 
	 * Needed for java 1.5 compatibility.
	 * @param s 		the <code>String</code> in question
	 * @return <code>true</code> if the given <code>String</code> is empty, <code>false</code> otherwise 
	 **/
	public static boolean isEmpty(String s){
		if(s.length() == 0)
			return true;
		return false;
	}

	public static boolean isNegativeResponse(String response){
		if(response.equalsIgnoreCase("No") || response.equalsIgnoreCase("N") 
				|| response.equalsIgnoreCase("False") || response.equalsIgnoreCase("F"))
			return true;
		return false;
	}
	
	public static boolean isPositiveResponse(String response){
		if(response.equalsIgnoreCase("Yes") || response.equalsIgnoreCase("Y") 
				|| response.equalsIgnoreCase("True") || response.equalsIgnoreCase("T"))
			return true;
		return false;
	}
	
	/**
	 * Replaces the illegal XML characters >, <, and & with their appropriate
	 * XML friendly version
	 * @param s the <code>String</code> in question
	 * @return the modified <code>String</code>
	 **/
	public static String replaceIllegalChars(String s){
		if(s.contains(">"))
			s = s.replace(">", "&gt;");
		if(s.contains("<"))
			s = s.replace("<", "&lt;");
		if(s.contains("&"))
			s = s.replace("&", "&amp;");
		return s;
	}
	
	/**
	 * Method to separate a string delimited by a delimiter.
	 * @param string the string to parse
	 * @param delimiter to use
	 * @param list where to add the newly separated Strings
	 * @return the list where the newly separated Strings were added
	 **/
	public static LinkedList<String> separate(String string, String delimiter, LinkedList<String> list){
		if(string == null || delimiter == null || list == null)
			return null;
		Scanner scan = new Scanner(string).useDelimiter(delimiter);
		while(scan.hasNext())
			list.add(scan.next().trim());
		return list;
	}

	/**
	 * Method to separate a <code>String</code> delimited by 
	 * commas and return as an array of <code>String</code>s
	 * @param string the string to parse
	 * @return the list where the newly separated Strings were added
	 **/
	public static String[] separateToArray(String string){
		return separateToArray(string, ",");
	}
	
	/**
	 * Method to separate a <code>String</code> delimited by delimiters and return as
	 * an array of <code>String</code>s
	 * @param string the string to parse
	 * @param delimiter to use
	 * @return the list where the newly separated Strings were added
	 **/
	public static String[] separateToArray(String string, String delimiter){
		if(string == null || delimiter == null || isEmpty(string.trim()))
			return new String[0];
		LinkedList<String> list = new LinkedList<String>();
		Scanner scan = new Scanner(string).useDelimiter(delimiter);
		while(scan.hasNext())
			list.add(scan.next().trim());
		if(list.isEmpty())
			list.add(string.trim());
		return toArray(list);
	}
	
	/**
	 * Method to separate a string delimited by semicolons.
	 * @param string the string to parse
	 * @param list where to add the newly separated Strings
	 * @return the list where the newly separated Strings were added
	 **/
	public static LinkedList<String> separate(String string, LinkedList<String> list){
		return separate(string, ";", list);
	}
	
	/**
	 * Method to determine if the bigger <code>String</code> 
	 * begins with the smaller <code>String</code>
	 * @param startString the smaller <code>String</code>
	 * @param bigString the bigger <code>String</code>
	 * @return <code>true</code> if the big <code>String</code> starts with the smaller one, false otherwise.
	 **/
	public static boolean startsWith(String startString, String bigString){
		if(bigString.length() < startString.length())
			return false;
		if(!bigString.substring(0,startString.length()).equals(startString))
			return false;
		return true;
	}
	
	/**
	 * Converts the list to an array
	 * @param list	the list to convert
	 * @return an array of <code>String</code>s 
	 **/
	public static String[] toArray(LinkedList<String>list){
		if(list == null)
			return null;
		String[]ret = new String[list.size()];
		for(int i = 0 ; i < ret.length; i++)
			ret[i] = list.get(i);
		return ret;
	}
	
	/**
	 * Converts the list to an array
	 * @param list	the list to convert
	 * @return an array of <code>Global.MType</code>s 
	 **/
	public static Global.MType[] toMTypeArray(LinkedList<Global.MType>list){
		if(list == null)
			return null;
		Global.MType[]ret = new Global.MType[list.size()];
		for(int i = 0 ; i < ret.length; i++)
			ret[i] = list.get(i);
		return ret;
	}
}
