package net.benjaminurquhart.CIO;

public class Main {

	public static void main(String[] args) throws Exception{
		if(args.length < 5){
			System.out.println("Usage: java -jar path/to/file.jar <token> <guild id> <channel id> <voice/text> <input/output>");
			return;
		}
		if(args[4].equalsIgnoreCase("output")) {
			net.benjaminurquhart.CIO.output.Main.main(args);
		}
		else if(args[4].equals("input")) {
			net.benjaminurquhart.CIO.input.Main.main(args);
		}
		else {
			System.err.println("Unknown stream type: " + args[4]); 
		}
	}
}
