package CustomSocketServer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ServerCLIImplement {
	//all variables
	private CommandLine cmd=null;
	private Options helpOptions;
	public boolean hasDebugMsg = false; //parse -v
	public boolean hasDir = false; //parse -d
	public boolean hasPortNum = false; //parse -p
	private int argsPortNum = 8080; //parse -p
	private String argsDir = ""; //parse -d
	
	//all command constant
	private final String DEBUGMSG = "v";
	private final String DIR = "d";
	private final String PORT_NUM = "p";
	private final String HELP = "help";
	
	//constructor
		public ServerCLIImplement(String[] args) {
			//initialize postOptions and getOptions
			initializeOptions();
			
			if(validateArgs(args)) {
				//System.out.println("Valid args");
				parseOptions(args);
			}
			else {
				System.out.println("Invalid args");
				printHelpMsg();
			}
		}
		
		//config options and option group
		private void initializeOptions() {
			helpOptions = new Options();
			
			helpOptions.addOption(DEBUGMSG, false, "Prints debugging messages.");
			helpOptions.addOption(PORT_NUM, true, "Specifies the port number that the server will listen and serve at. Default is 8080.");
			helpOptions.addOption(DIR, true, "Specifies the directory that the server will use to read/write requested files. Default is the current directory when launching the application.");
			helpOptions.addOption(HELP, false, "Print help information");
		}
		
		//check the validity of command line
		private boolean validateArgs(String[] args) {
			//check httpfs help and httpfs (no arg) and httpfs (wrong args)
			if(args.length == 0 || (args.length == 1) && (args[0].toLowerCase().equals(HELP)) || args.length > 3) {
				printHelpMsg();
			}
			
			//check httpfs [-v] [-p PORT] [-d PATH-TO-DIR]
			//check if has options other than -v, -p, -d
//			if(args.length >=2) {
//			}
			return true;
		}
		
		//parse the value of the command line
		protected void parseOptions(String[] args){
			//parse all options
			CommandLineParser parser = new DefaultParser();
			try {
				//change options according to method
				cmd = parser.parse(helpOptions, args);
				
				//check if has options other than -v, -p, -d
				if(!cmd.hasOption(DEBUGMSG) && !cmd.hasOption(DIR) && !cmd.hasOption(PORT_NUM)) {
					System.out.println("Invalid command.");
					printHelpMsg();
				}
				
				//***check if has invalid options //loop through args and check if has invalid options
				
				if(cmd.hasOption(DIR)) {
					this.hasDir = true;
					//parse path into a string array
				}
				
				//has option -p and parse port number
				if(cmd.hasOption(PORT_NUM)) {
					int parsedPortNum = Integer.parseInt(cmd.getOptionValue(PORT_NUM));
					
					//check if the parsed port number is reserved or invalid
					if(parsedPortNum >= 1 && parsedPortNum <= 1023) {
						System.out.println("Port number "+ parsedPortNum + " is reserved. Please enter a non-reserved port number.");
					} 
					else if (parsedPortNum<= 65535){
						this.hasPortNum = true;
						this.argsPortNum = parsedPortNum;
						System.out.println("Current port number is "+this.argsPortNum);
					}
					else {
						System.out.println("Non-existing port number. Please enter a valid port number.");
					}
						
				}
				
				//has option -v
				if(cmd.hasOption(DEBUGMSG)) {
					this.hasDebugMsg = true;
				}
			} 
			catch(ParseException e) {
				System.out.println(e.getMessage());
				printHelpMsg();
			} 
		}
		
		private void printHelpMsg() {
			String cmlSyntax = "usage: httpfs [-v] [-p PORT] [-d PATH-TO-DIR]";
			String header = "\nhttpfs is a simple file server.\n\n";
			String footer = "\nUse \"httpfs help [command]\" for more information about a command.";
			new HelpFormatter().printHelp(cmlSyntax, header, helpOptions, footer);
			System.exit(0);
		}
}
