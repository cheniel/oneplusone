package oneplusone;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import java.io.*;
import java.util.Scanner;

/**
 * PairingGenerator
 * 
 * Main for the oneplusone application.
 * 
 */

public class PairingGenerator {
	
	// file name for database
	private static final String DB4OFILENAME = "organizationsDB";
	private static ObjectContainer db;
	private static Scanner user_input;

	private static void displayCommands() {
		System.out.println("\nThe commands are: ");
		System.out.println("\tl\tload organization from file");
		System.out.println("\t?\thelp");
		System.out.println("\tq\tquit");
	}
	
	
	private static void loadOrganizationFromFile() {
		System.out.println("\nEnter the filename used: ");
		String filename = user_input.next();
		File file = new File(filename);
		BufferedReader reader = null;
		
		// check that the organization does not already exist
		
		
		try {
	    reader = new BufferedReader(new FileReader(file));
	    System.out.println("File found! Loading into database.");
	    
	    String person;
	    while ((person = reader.readLine()) != null) {
        String[] info = person.split(";"); 
        String email = info[0];
        String teamsUnsplit = info[1];
        String[] teams = teamsUnsplit.split(",");
        
        for (String team : teams) {
        	
        	// if team does not exist under organization
        		// create the team
        	
        	
        	System.out.println("adding " + email + " to " + team);
        }
	    
	    }

		} catch (FileNotFoundException e) {
		  System.out.println("File not found.");
		} catch (IOException e) {
			System.out.println("There was an error reading the file.");
		} finally {
	    try {
	    	if (reader != null) {
	    		reader.close();
	    	}
	    } catch (IOException e) {
	    	// it's fine.
	    }
		}		
	}
	
	public static void main(String [] args) {
		System.out.println("Welcome to the 1+1 Generator\nCreated by Daniel Chen");
		System.out.println("----------------------------\n");
		System.out.println("Opening database...");

		db = Db4oEmbedded.openFile(Db4oEmbedded
		        .newConfiguration(), DB4OFILENAME);
		user_input = new Scanner( System.in );

		try {
			System.out.println("Successfully opened database.");

			displayCommands();
			
			boolean running = true;
			while (running) {
				System.out.println("\nEnter a command: ");
				String command = user_input.next();
				
				switch (command) {
					// display organizations
				
					// delete organization
					
					// load
					case "l": loadOrganizationFromFile();
										break;
				
					// help
					case "?":	displayCommands(); 
										break;
					
					// quit
					case "q": running = false;
										break;
					
					// invalid command
					default:	System.out.println("Command not recognized.");
										break;
				}
				
			}

		} finally {
			System.out.println("\nClosing database and scanner.");
		  db.close();
		  user_input.close();
		  System.out.println("Closed database and scanner.");
		}
		
	}
}
