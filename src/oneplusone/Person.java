/**
 * Person.java
 * 
 * Class for a person.
 * 
 * Contains a list of teams, teammates. 
 * 
 * Functions are mostly accessor functions to team lists, as well as functions
 * that modify teammate list.
 */

package oneplusone;

import java.util.HashMap;
import java.util.Set;

public class Person {
	private static final boolean TEST = true;
	private String email;
	private HashMap<String, Team> teams;
	private HashMap<String, Teammate> teammates;
	private Teammate previousMatch;
	
	public Person(String memberEmail) {
		email = memberEmail;
		teams = new HashMap<String, Team>();
		teammates = new HashMap<String, Teammate>();
		previousMatch = null;
	}
	
	/**
	 * Increase links with a teammate if already teammate, create new teammate 
	 * if not already teammates.
	 * 
	 * @param teammate to add
	 */
	public void addTeammate(Person teammate) {
		if (teammate == null) {
			System.out.println("addTeammate: teammate null");
			return;
		}
		
		if (!teammates.containsKey(teammate.getEmail())) {
			teammates.put(teammate.email, new Teammate(teammate));
			
			if (TEST) {
				System.out.println("\tMade "+teammate.email+" "+email+ "'s teammate");
			}
		} else {
			teammates.get(teammate.getEmail()).links++;
			
			if (TEST) {
				System.out.println("\t"+email+" shares "
							+teammates.get(teammate.getEmail()).links+" teams with "
							+teammate.email);
			}
		}
	}
	
	/**
	 * @return teammate count
	 */
	public int teammateCount() {
		return teammates.keySet().size();
	}
	
	/**
	 * Removes a teammate.
	 * 
	 * More specifically, decreases the links (number of shared teams) with the
	 * teammate. Removes teammate entirely if the links to that teammate decrease 
	 * to zero.
	 * 
	 * One-way disconnect. Disconnecting teammates requires two calls to this 
	 * function.
	 * 
	 * @param teammate to remove
	 */
	public void removeTeammate(Person teammate) {
		if (teammates == null) {
			System.out.println("removeTeammate: Received null teammate.");
			return;
		}
		
		if (!teammates.containsKey(teammate.email)) return;
		
		Teammate toRemove = teammates.get(teammate.email);
		toRemove.links--;
		
		if (toRemove.links == 0) {
			teammates.remove(teammate.email);
		}
	}
	
	/**
	 * Called by Team.addMember() to add team to a member
	 * 
	 * Teammate connections are formed in Team.addMember()
	 * 
	 * @see Team.addMember()
	 * @param team
	 */
	public void addTeam(Team team) {
		teams.put(team.getName(), team);
	}
	
	/**
	 * Removes member from a certain team.
	 * 
	 * Removing teammate connections happens in Team.removeMember()
	 * 
	 * @see Team.removeMember()
	 * @param team
	 */
	public void removeTeam(Team team) {
		teams.remove(team.getName());
	}
	
	/**
	 * @see Organization.removeMember()
	 * @return the set of teams that this person belongs to.
	 */
	public Set<String> teams() {
		return teams.keySet();
	}
	
	public String getEmail() {
		return email;
	}
	
	public boolean equals(Object other) {
		return email.equals(((Person) other).getEmail());
	}
	
	public String toString() {
		return email;
	}
	
	public void matchTeammate(Person person) {
		if (person == null) {
			System.out.println("matchTeammate: person is null"); 
			return;
		}
		
		if (!teammates.containsKey(person.email)) {
			System.out.println("matchTeammate: person is not a teammate!");
		} else {
			teammates.get(person.email).matched = true;
		}
	}
	
	public void unmatchTeammate(Person person) {
		if (person == null) {
			System.out.println("unmatchTeammate: person is null"); 
			return;
		}
		
		if (!teammates.containsKey(person.email)) {
			System.out.println("unmatchTeammate: person is not a teammate!");
		} else {
			teammates.get(person.email).matched = false;
		}
	}
	
	/**
	 * Wrapper class for a person.
	 * Used to store a reference to a teammate along with certain information:
	 * 		- Whether or not the teammate has been matched with in this cycle.
	 * 		- The number of shared teams (links) between the person and the teammate
	 */
	class Teammate {
		Person person;
		private boolean matched;
		private int links;
		
		public Teammate(Person teammate) {
			person = person;
			matched = false;
			links = 1;
		}
	}


}
