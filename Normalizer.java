/* 
 * Normalizer.java
 * Martin Deutsch
 * 7/19/22
 */
 
import java.io.*;
 
 /**
 * Reads in a csv from stdin and writes the normalized csv to stdout
 * @param args The command line arguments.
 **/
 public class Normalizer {
 	public static String normalize(String rawLine) {
 		return rawLine;
 	}
 	
	public static void main(String[] args) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
    		String headers = in.readLine();
    		if (headers == null) {
    			return;
    		}
    		out.write(headers, 0, headers.length());
    		out.newLine();
    		
    		String line = "";
    		while ((line = in.readLine()) != null) {
    			out.write(normalize(line));
    			out.newLine();
    		}
    		in.close();
    		out.close();
    	}
    	catch (IOException e) {
    		System.err.println("Unable to read input.");
    	}
	}
}