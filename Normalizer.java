/* 
 * Normalizer.java
 * Martin Deutsch
 * 7/19/22
 */
 
import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.*;
 
public class Normalizer {
 	/**
 	 * Normalizes a CSV line
 	 * @param rawLine The CSV line to process.
 	 * @return The processed CSV line.
 	 **/
 	private String normalize(String rawLine) {
 		int separatorIndex1 = rawLine.indexOf(",");
 		String timestamp = rawLine.substring(0, separatorIndex1);
 		String formattedTimestamp = formatTimestamp(timestamp);
 		if (formattedTimestamp.length() == 0) {
 			System.err.println(String.format("Unable to parse timestamp '%s'", timestamp));
 			return "";
 		}
 		
 		int separatorIndex2 = rawLine.indexOf(",", separatorIndex1+1);
 		if (rawLine.charAt(separatorIndex1+1) == '\"') {
 			separatorIndex2 = rawLine.indexOf('\"', separatorIndex1+2) + 1;
 		}
 		String address = rawLine.substring(separatorIndex1+1, separatorIndex2);
 		
 		int separatorIndex3 = rawLine.indexOf(",", separatorIndex2+1);
 		String zip = rawLine.substring(separatorIndex2+1, separatorIndex3);
 		String formattedZip = formatZip(zip);
 		
 		int separatorIndex4 = rawLine.indexOf(",", separatorIndex3+1);
 		String fullName = rawLine.substring(separatorIndex3+1, separatorIndex4);
 		String formattedName = formatFullName(fullName);
 		
 		int separatorIndex5 = rawLine.indexOf(",", separatorIndex4+1);
 		String fooDuration = rawLine.substring(separatorIndex4+1, separatorIndex5);
 		double formattedFooDuration = formatDuration(fooDuration);
 		if (formattedFooDuration < 0) {
 			System.err.println(String.format("Unable to parse foo duration '%s'", fooDuration));
 			return "";
 		}
 		
 		int separatorIndex6 = rawLine.indexOf(",", separatorIndex5+1);
 		String barDuration = rawLine.substring(separatorIndex5+1, separatorIndex6);
 		double formattedBarDuration = formatDuration(barDuration);
 		if (formattedBarDuration < 0) {
 			System.err.println(String.format("Unable to parse bar duration '%s'", barDuration));
 			return "";
 		}
 		
 		int separatorIndex7 = rawLine.indexOf(",", separatorIndex6+1);
 		double totalDuration = formattedFooDuration + formattedBarDuration;
 		
 		String notes = rawLine.substring(separatorIndex7+1);
 		
 		return formattedTimestamp + "," + address + "," + formattedZip + "," + formattedName + "," 
 			+ formattedFooDuration + "," + formattedBarDuration + "," + totalDuration + "," + notes;
 	}
 	
 	/**
 	 * Converts a timestamp to RFC3339 Eastern time.
 	 * @param timestamp The raw timestamp.
 	 * @return The processed timestamp.
 	 **/
 	private String formatTimestamp(String timestamp) {
 		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy h:mm:ss a", Locale.US);
 		try {
			LocalDateTime dateTime = LocalDateTime.parse(timestamp, formatter);
			ZonedDateTime pacificDateTime = dateTime.atZone(ZoneId.of("America/Los_Angeles"));
			ZonedDateTime easternDateTime = pacificDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));
			return easternDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}
		catch (DateTimeParseException e) {
			return "";
		}
 	}
 	
 	/**
 	 * Left pads a zip code with zeroes.
 	 * @param zip The raw zip code.
 	 * @return The padded zip code.
 	 **/
 	private String formatZip(String zip) {
 		return String.format("%5s", zip).replace(' ', '0');
 	}
 	
 	/**
 	 * Converts a name to uppercase
 	 * @param fullName The raw name.
 	 * @return The uppercase name.
 	 **/
 	private String formatFullName(String fullName) {
 		return fullName.toUpperCase();
 	}
 	
 	/**
 	 * Converts a HH:MM:SS.MS duration to total seconds.
 	 * @param duration The raw time span.
 	 * @return The converted duration in double form.
 	 **/
 	private double formatDuration(String duration) {
		String[] tokens = duration.split(":");
		try {
			int hours = Integer.parseInt(tokens[0]);
			int minutes = Integer.parseInt(tokens[1]);
			String[] secs = tokens[2].split("\\.");
			int seconds = Integer.parseInt(secs[0]);
			double miliseconds = Double.parseDouble(secs[1]);
			return (3600*hours + 60*minutes + seconds + miliseconds/1000);
		}
		catch (NumberFormatException e) {
			return -1;
		}
 	}
 	
 	/**
 	 * Reads in a csv from stdin and writes the normalized csv to stdout
 	 * @param args The command line arguments.
 	 **/
	public static void main(String[] args) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
    		String headers = in.readLine();
    		if (headers == null) {
    			return;
    		}
    		out.write(headers, 0, headers.length());
    		out.newLine();
    		
    		Normalizer normalizer = new Normalizer();
    		String line = "";
    		while ((line = in.readLine()) != null) {
    			String normalizedLine = normalizer.normalize(line);
    			if (normalizedLine.length() > 0) {
    				out.write(normalizedLine, 0, normalizedLine.length());
    				out.newLine();
    			}
    		}
    		in.close();
    		out.close();
    	}
    	catch (IOException e) {
    		System.err.println("Unable to read input.");
    	}
	}
}