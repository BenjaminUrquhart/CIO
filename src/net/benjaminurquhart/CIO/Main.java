package net.benjaminurquhart.CIO;

import net.benjaminurquhart.CIO.*;

public class Main {

	public static void main(String[] args) {
		if(args.length < 5){
			System.out.println("Usage: java -jar path/to/file.jar <token> <guild id> <channel id> <voice/text> <input/output>");
			return;
		}
		if(args[4].equalsIgnoreCase("output")) {
			
		}
		else if(args[4].equals("input")) {
			
		}
		else {
			System.err.println("Unknown stream type: " + args[4]); 
		}
	}
}
