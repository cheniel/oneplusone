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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Person {
	private static final boolean TEST = false;
	private String id;
	private HashMap<String, Team> teams;
	private HashMap<String, Teammate> teammates;
	private HashSet<Person> previousMatch;
	private int cycleCount;
	
	public Person(String memberEmail) {
		id = memberEmail;
		teams = new HashMap<String, Team>();
		teammates = new HashMap<String, Teammate>();
		previousMatch = new HashSet<Person>();
		cycleCount = 0;
	}
	
	/**
	 * Increase links with a teammate if already teammate, create new teammate 
	 * if not already teammates.
	 * 
	 * Only connects one way.
	 * 
	 * @param teammate to add
	 */
	public void addTeammate(Person teammate) {
		if (teammate == null) {
			System.err.println("addTeammate: teammate null");
			return;
		}
		
		if (!teammates.containsKey(teammate.getName())) {
			teammates.put(teammate.id, new Teammate(teammate));
			
			if (TEST) {
				System.out.println("\tMade "+teammate.id+" "+id+ "'s teammate");
			}
		} else {
			teammates.get(teammate.getName()).links++;
			
			if (TEST) {
				System.out.println("\t"+id+" shares "
							+teammates.get(teammate.getName()).links+" teams with "
							+teammate.id);
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
			System.err.println("removeTeammate: Received null teammate.");
			return;
		}
		
		if (!teammates.containsKey(teammate.id)) return;
		
		Teammate toRemove = teammates.get(teammate.id);
		toRemove.links--;
		
		if (toRemove.links == 0) {
			teammates.remove(teammate.id);
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
	
	public String getName() {
		return id;
	}
	
	public boolean equals(Object other) {
		return id.equals(((Person) other).getName());
	}
	
	public String toString() {
		return id;
	}
	
	/**
	 * 
	 */
	public boolean cycleFull() {
		return cycleCount / 2 >= teammates.size();
	}
	
	/**
	 * 
	 */
	public void resetCycle() {
		cycleCount = 0;
		for (Teammate tm : teammates.values()) {
			tm.matched = false;
		}
	}
	
	public boolean previouslyMatchedWith(Person person) {
		return previousMatch.contains(person);
	}
	
	public ArrayList<Teammate> getTeammates() {
		return new ArrayList<Teammate>(teammates.values());
	}

	public void setPreviousMatchups(HashSet<Person> matchups) {
		previousMatch = matchups;
		
		for (Person tm : matchups) {
			teammates.get(tm.getName()).matched = true;
			cycleCount++;
		}
	}
	
	/**
	 * Wrapper class for a person.
	 * Used to store a reference to a teammate along with certain information:
	 * 		- Whether or not the teammate has been matched with in this cycle.
	 * 		- The number of shared teams (links) between the person and the teammate
	 */
	public class Teammate {
		public Person person;
		private boolean matched;
		private int links;
		
		public Teammate(Person teammate) {
			person = teammate;
			matched = false;
			links = 1;
		}
		
		public boolean matchedInCycle() {
			return matched;
		}
		
		public int getLinks() {
			return links;
		}
	}
}
