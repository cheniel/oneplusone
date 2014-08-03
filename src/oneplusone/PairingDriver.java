/**
 * PairingGenerator.java
 * 
 * Main for the oneplusone application.
 * 
 * Deals with prompting the user, calling commands, opening, navigating and 
 * editing the database.
 * 
 * @author Daniel Chen
 */

package oneplusone;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.ta.TransparentActivationSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class PairingDriver {	
	private static final String DB4OFILENAME = "organizations.oneplusoneDB";
	private static ObjectContainer db; // db4o database
	private static Scanner user_input; 

	/**
	 * Displays all of the possible commands to the console
	 */
	private static void displayCommands() {
		System.out.println("\nThe commands are: ");
		System.out.println("\t---------------------------------------------------");
	
		System.out.println("\tEXECUTE");
		System.out.println("\t\trun\t\trun pairings for this week."); //TODO run
		
		System.out.println("\tVIEW");
		System.out.println("\t\tvo\t\tview organization tree");
		System.out.println("\t\tlo\t\tlist organizations");
		System.out.println("\t\tvm\t\tview members of organization");

		System.out.println("\tADD");
		System.out.println("\t\tamt\t\tadd member-team"); 
		System.out.println("\t\tnorg\t\tadd new organization");
		System.out.println("\t\tlorg\t\tload organization from file"); 

		System.out.println("\tDELETE");
		System.out.println("\t\trmt\t\tremove member from team"); 
		System.out.println("\t\trm\t\tremove member from all teams"); 
		System.out.println("\t\trt\t\tremove team (keep members)"); 
		System.out.println("\t\tdorg\t\tdelete organization");

		System.out.println("\tOTHER");
		System.out.println("\t\t?\t\tdisplay commands");
		System.out.println("\t\tq\t\tquit");
		System.out.println("\t---------------------------------------------------");
	}
	
	/**
	 * Retrieves organizations from database and displays to user.
	 */
	private static void displayOrganizations() {
		ObjectSet<Organization> organizations 
			= db.queryByExample(new Organization(null));
		
		System.out.println("\nList of organizations: ");
		for (Organization org : organizations) {
			System.out.println("\t" + org.getName());
		}	
	}
	
	/**
	 * Saves database. Should be called whenever items are updated.
	 * 
	 * @param organization to update in the database.
	 */
	private static void saveDatabase(Organization org) {
		if (org == null) { return; }
		System.out.println("Saving " + org.getName() + "...");
		db.store(org);
		System.out.println("Saved.");
	}
	
	/**
	 * Prompts the user for an existing organization. Additionally allows user to 
	 * view the organizations to choose from and to cancel.
	 * 
	 * @return Organization that the user selected. Null if user cancels.
	 */
	private static Organization getOrganizationFromUser() {
		boolean valid = false;
		String command;
		Organization selected = null;
		
		do {
			System.out.print("\nType in organization name (q to exit, l to list): ");
			command = user_input.nextLine();

			switch (command) {
			case "l": 
				displayOrganizations(); // list organizations
				break;				
				
			case "q":	
				valid = true; // leave while returning null
				break;
								
			default: 	
				// check if the user's input is a valid organization
				ObjectSet<Object> results 
					= db.queryByExample(new Organization(command));
				
				if (results.hasNext() == true) { // if organization exists
					selected = (Organization) results.get(0); // select org to return
					valid = true; // break out of loop
				} else {
					System.out.println("Organization not found."); // reprompt
				}
				break;
			}
		} while (!valid);
		
		return selected;
	}
	
	/**
	 * Retrieves an existing team from the user given a selected organization.
	 * @see getOrganizationFromUser()
	 * @param organization to select teams from
	 * @return selected team, null if user cancels
	 */
	private static Team getTeamFromUser(Organization org) {
		if (org == null) { return null; }
		boolean valid = false;
		String command;
		Team selected = null;
		
		do {
			System.out.print("\nType in team name (q to exit, l to list): ");
			command = user_input.nextLine();

			switch (command) {
			
			case "l": 
				org.printTeams();
				break;
								
			case "q":	
				valid = true;
				break;
								
			default: 		
				// return team if it exists, null otherwise
				if (org.hasTeam(command)) {
					selected = org.getTeam(command); // select team to return
					valid = true; // break out of loop
				} else {
					System.out.println("Team not found.");  // reprompt
				}
				break;
			}
			
		} while (!valid);
		return selected;
	}
	
	/**
	 * Retrieves an existing member from the user given the organization.
	 * @see getOrganizationFromUser()
	 * @param organization to select members
	 * @return selected member, null if user cancels
	 */
	private static Person getMemberFromUser(Organization org) {
		if (org == null) { return null; }
		
		boolean valid = false;
		String command;
		Person selected = null;
		
		do {
			System.out.print("\nType in member email (q to exit, l to list): ");
			command = user_input.nextLine();

			switch (command) {
			
			case "l": 
				org.printMembers();
				break;
								
			case "q":	
				valid = true;
				break;
								
			default: 		
				if (org.hasMember(command)) { 
					selected = org.getMember(command); // select member to return
					valid = true; // break out of loop
				} else {
					System.out.println("Member not found."); // reprompt
				}
				break;
			}
			
		} while (!valid);
		
		return selected;
	}
	
	/**
	 * Retrieves an existing member from the user given the team within an 
	 * organization.
	 * @param team to pick members from
	 * @return member that the user picked, null if cancelled.
	 */
	private static Person getMemberFromUser(Team team) {
		if (team == null) { return null; }
		
		boolean valid = false;
		String command;
		Person selected = null;
		
		do {
			System.out.print("\nType in member email (q to exit, l to list): ");
			command = user_input.nextLine();

			switch (command) {
			
			case "l": 
				team.printMembers();
				break;
								
			case "q":	
				valid = true;
				break;
								
			default: 		
				if (team.hasMember(command)) {
					selected = team.getMember(command); // get member of team
					valid = true; // break out of loop
				} else {
					System.out.println("Member not found."); // reprompt
				}
				break;
			}
			
		} while (!valid);
		
		return selected;
	}
	
	/**
	 * Adds a member-team pair to an organization. Prompts user for team and 
	 * member email. If either the team or the member does not already exist,
	 * the user is asked to confirm.
	 * 
	 * Doesn't utilize getTeamFromUser and getMemberFromUser to as they restrict
	 * options to those that already exist, and this utility can potentially make
	 * new members and teams.
	 * 
	 * @param organization to add member-team pair to.
	 */
	private static void addMTfromUser(Organization org) {
		if (org == null) { return; }
			
		// get team
		System.out.print("Type in the name of the team: ");
		String team = user_input.nextLine();
		
		// get member
		System.out.print("Type in the email of the member: ");
		String member = user_input.nextLine();
		
		// add member team pair, no force
		boolean success = org.addMemberToTeam(member, team, false);
		
		// ask to force if failed
		if (!success) {
			System.out.print("Force add? (y/n): ");
			String force = user_input.nextLine();
			if (force.equals("y")) { org.addMemberToTeam(member, team, true); }
		}
		
		saveDatabase(org);
	}
	
	/**
	 * Removes a member from a team within an organization. The team and the 
	 * member are prompted from the user.
	 * 
	 * @param organization to remove a member from a team from.
	 */
	private static void removeMemberFromTeam(Organization org) {
		if (org == null) { return; }
			
		// get team
		Team team = getTeamFromUser(org);
		if (team == null) { return; }
		
		// get member
		Person member = getMemberFromUser(team);
		if (member == null) { return; }
		
		// remove member from team
		team.removeMember(member);
		
		saveDatabase(org);
	}
	
	/**
	 * Removes a member from all teams within an organization. The member is
	 * prompted from the user.
	 * 
	 * @param organization to remove member from.
	 */
	private static void removeMember(Organization org) {
		if (org == null) { return; }
		
		Person member = getMemberFromUser(org);
		if (member == null) { return; }
		
		// remove member from organization
		org.removeMember(member.getName());
		
		saveDatabase(org);
	}
	
	/**
	 * Removes a team from an organization. The team is given by the user.
	 * The members remain, and they decrease teammate bonds.
	 * 
	 * @param organization to remove team from.
	 */
	private static void removeTeam(Organization org) {
		if (org == null) { return; }
		
		// have user select a team
		Team team = getTeamFromUser(org);
		if (team == null) { return; }
		
		// remove team from organization
		org.removeTeam(team.getName());
		
		saveDatabase(org);
	}
	
	/**
	 * Creates an empty organization in the database. Prompts user for 
	 * organization name.
	 */
	private static void createOrganization() {
		System.out.print("\nEnter the organization name: ");
		String orgName = user_input.nextLine();

		// cancel if organization name is invalid.
		if (orgName.equals("q") || orgName.equals("l")) {
			System.out.println("Organization name cannot be q or l");
			return;
		}
		
		Organization org = new Organization(orgName);
    
		// if the organization does not already exist
		if (db.queryByExample(org).hasNext() == false) {
			System.out.println("Creating organization: " + orgName);	
			saveDatabase(org);
		} else {
			System.out.println("Organization name already exists.");
		}
	}
	
	/**
	 * Loads organization from a file. The filename is prompted from the user.
	 * See files in "orgs" folder for template.
	 * 
	 * The organization name is given by the filename.
	 * Simple way to populate an initial database.
	 */
	private static void loadOrganizationFromFile() {
		
		// get filename from user.
		System.out.print("\nEnter the filename used (q to exit): ");
		String filename = user_input.nextLine();
		
		File file = new File(filename);
		String[] path = filename.split("/");
		String orgName = path[path.length - 1]; // get name of file, not path
		BufferedReader reader = null;
		
		// cancel if organization name is invalid.
		if (orgName.equals("q") || orgName.equals("l")) {
			System.out.println("Organization name cannot be q or l");
			return;
		}
		
		try {
			reader = new BufferedReader(new FileReader(file));
			System.out.println("File found.");
		  
			// create new organization with name.
			Organization org = new Organization(orgName);
		    
			// if the organization does not already exist
			if (db.queryByExample(org).hasNext() == false) {
				
				System.out.println("Creating organization: " + orgName);
				
				// each line has a new person
				// e.g. 
				// person@website.com;team,team,team,team
		    String person;
		    while ((person = reader.readLine()) != null) {
		        String[] info = person.split(";"); 
		        String email = info[0];
		        String teamsUnsplit = info[1];
		        String[] teams = teamsUnsplit.split(",");
		        
		        // add member to specified teams
		        for (String teamName : teams) {
		        	org.addMemberToTeam(email, teamName, true);
		        }
		    }				
		    saveDatabase(org);
		    System.out.println("\n" + org);
		    
	    } else {
	    	System.out.println("The organization already exists. Halting.");
	    }

		} catch (FileNotFoundException e) {
		  System.out.println("File not found.");
		} catch (IOException e) {
			System.err.println("There was an error reading the file.");
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
	
	/**
	 * Opens database, prompts user for commands.
	 */
	public static void main(String [] args) {
		System.out.println("oneplusone");
		System.out.println("----------\n");
		System.out.println("Opening database (filename: " + DB4OFILENAME + ")...");

		// configuration makes it so only organization needs to be pulled and 
		// stored from db. When organization is loaded, entire organization
		// is loaded into memory.
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(Organization.class).cascadeOnUpdate(true);
		config.common().objectClass(Team.class).cascadeOnUpdate(true);
		config.common().objectClass(Person.class).cascadeOnUpdate(true);
		config.common().add(new TransparentActivationSupport());
		
		db = Db4oEmbedded.openFile(config, DB4OFILENAME);
		user_input = new Scanner( System.in );

		try {
			System.out.println("Successfully opened database.");

			displayCommands();
			
			Organization selected = null;
			
			boolean running = true;
			while (running) {
				System.out.print("\nEnter a command: ");
				String command = user_input.nextLine();
				
				switch (command) {
				
				// run 1+1s
				case "run":
					selected = getOrganizationFromUser();
					if (selected != null) {
						System.out.println(selected.getPairings());
						saveDatabase(selected); // save matchups
					}
					break;
				
				// add member to team
				case "amt":
					selected = getOrganizationFromUser();
					addMTfromUser(selected);
					break;
				
				// remove member from team
				case "rmt":
					selected = getOrganizationFromUser();
					removeMemberFromTeam(selected);
					break;
				
				// remove member from organization
				case "rm":
					selected = getOrganizationFromUser();
					removeMember(selected);
					break;
				
				// remove team from organization
				case "rt":
					selected = getOrganizationFromUser();
					removeTeam(selected);
					break;
				
				// view all members in organization.
				case "vm":
					selected = getOrganizationFromUser();
					if (selected != null) {
						selected.printMembers();
					}
					break;
					
				// view organization tree
				case "vo":
					selected = getOrganizationFromUser();
					if (selected != null) {
						System.out.println("\n" + selected);
					}
					break;
				
				// display organizations
				case "lo":	
					displayOrganizations();
					break;
				
				// delete organization
				case "dorg":
					selected = getOrganizationFromUser();
					if (selected != null) {
						db.delete(selected);
					}
					break;
				
				// create new organization
				case "norg":
					createOrganization();
					break;
				
				// load
				case "lorg":	
					loadOrganizationFromFile();
					break;
			
				// help
				case "?":	
					displayCommands(); 
					break;
				
				// quit
				case "q": 	
					running = false;
					break;
				
				// invalid command
				default:	
					System.out.println("Command \""+command+
							"\" not recognized. (\"?\" for help)");
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
