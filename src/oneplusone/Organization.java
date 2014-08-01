/**
 * Organization.java
 * 
 * Class for an organization. 
 * 
 * Contains a collection of teams and members (stored in HashMap).
 * 
 * Functions take care of rearranging members and teams on an organization scale
 * 
 * Contains function to generate 1+1s.
 */

package oneplusone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class Organization {
	private String name;
	private HashMap<String, Person> people;
	private HashMap<String, Team> teams;
	
	public Organization(String organizationName) {
		name = organizationName;
		people = new HashMap<String, Person>(); //
		teams = new HashMap<String, Team>();
	}
	
	/**
	 * 
	 * @return
	 */
	public PairingAssignment getPairings() {
		WeightedCSP csp = new WeightedCSP(new ArrayList<Person>(people.values()));
		
		return csp.solve();
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Adds a member to a team in the organization. 
	 * 
	 * If force is not selected, the member and the team must already exist in 
	 * the organization.
	 * 
	 * If force is selected, either the member and the team or both can be new to
	 * the organization.
	 * 
	 * @param memberEmail, the team to add to, and whether or not to force the add
	 * @return success
	 */
	public boolean addMemberToTeam(String memberEmail, String teamName, 
			boolean force) {
		boolean memberExists = people.containsKey(memberEmail);
		boolean teamExists = teams.containsKey(teamName);
		
		// if both exist or if the operation is forced, add member to team
		if ((memberExists && teamExists) || force ) {
			
			Person member;
			Team team;
			
			if (!memberExists) {
				member = new Person(memberEmail);
				people.put(memberEmail, member);
				System.out.println("Added new member: " + memberEmail);
				
			} else {
				member = people.get(memberEmail);
			}
			
			if (!teamExists) {
				team = new Team(teamName);
				teams.put(teamName, team);
				System.out.println("Created new team: " + teamName);
				
			} else {
				team = teams.get(teamName);
			}
			
			System.out.println("Adding " + memberEmail + " to " + teamName);
			team.addMember(member);
			
			return true;
			
		} else {
			System.out.println("Add cancelled:");
			
			if (!memberExists) {
				System.out.println("\tMember does not already exist.");
			}
			
			if (!teamExists) {
				System.out.println("\tTeam does not already exist.");
			}
			
			return false;
		}		
	}

	/**
	 * Prints all of the members in the organization. 
	 */
	public void printMembers() {
		for (String member : people.keySet()) {
			System.out.println(member);
		}
	}
	
	/**
	 * Prints out all the teams within the organization.
	 */
	public void printTeams() {
		for (String team : teams.keySet()) {
			System.out.println(team);
		}	
	}
	
	/**
	 * @param teamName
	 * @return whether or not the team exists in the organization.
	 */
	public boolean hasTeam(String teamName) {
		return teams.containsKey(teamName);
	}
	
	/**
	 * @param teamName
	 * @return the team associated with the team, null if doesn't exist.
	 */
	public Team getTeam(String teamName) {
		return teams.get(teamName);
	}
	
	/**
	 * @param memberEmail
	 * @return whether or not the member exists in the organization
	 */
	public boolean hasMember(String memberEmail) {
		return people.containsKey(memberEmail);
	}
	
	/**
	 * @param memberEmail
	 * @return the member associated with the email, null if doesn't exist.
	 */
	public Person getMember(String memberEmail) {
		return people.get(memberEmail);
	}
	
	/**
	 * Removes a member from the organization. Also removes them from all teams,
	 * and removes all teammate links. 
	 * 
	 * @param memberEmail of member to remove
	 */
	public void removeMember(String memberEmail) {		
		Person member = people.get(memberEmail);
		if (member == null) return;
		
		// remove from all teams
		for (String team : member.teams()) {
			System.out.println("Removing " + memberEmail + " from " + team);
			teams.get(team).removeMember(member);
		}
		
		// remove from org
		System.out.println("Removing " + memberEmail + " from " + name);
		people.remove(memberEmail);
	}
	
	/**
	 * Removes a team from an organization. Members remain.
	 * @param teamName of team to remove
	 */
	public void removeTeam(String teamName) {
		Team team = teams.get(teamName);
		if (team == null) return; 
		
		// remove team from members of team
		for (String member : team.getMembers()) {
			System.out.println("Removing " + member + " from " + team.getName());
			team.removeMember(people.get(member)); 
		}
		
		// remove from org
		teams.remove(teamName);
	}
	
	public boolean equals(Object other) {
		return name.equals(((Organization) other).getName());
	}

	/**
	 * Prints out a tree of the organization structure
	 * e.g.
	 * Team 1
	 * 	Member 1
	 * 	Member 2
	 * Team 2
	 * 	Member 2
	 * 	Member 3
	 * ...
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer tree = new StringBuffer();
		
		for (String team : teams.keySet()) {
			tree.append(team + "\n");
			
			Set<String> teamMembers = teams.get(team).getMembers();
			for (String person : teamMembers) {
				tree.append("\t" + person + "\n");
			}
		}
		return tree.toString();
	}
}
