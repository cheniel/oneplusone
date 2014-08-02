/**
 * Team.java
 * 
 * Class for a team. Contains a list of members.
 * 
 * Functions take care of membership on a team scale.
 */

package oneplusone;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Team {
	private String name;
	private HashMap<String, Person> members;

	public String getName() {
		return name;
	}
	
	public Team(String teamName) {
		name = teamName;
		members = new HashMap<String, Person>();
	}
	
	/**
	 * @param email of member
	 * @return if member is in team
	 */
	public boolean hasMember(String memberEmail) {
		return members.containsKey(memberEmail);
	}
	
	/**
	 * @param email of member
	 * @return the member, null if not in team.
	 */
	public Person getMember(String memberEmail) {
		return members.get(memberEmail);
	}
	
	/**
	 * Adds a member to the team
	 * 
	 * @param memberEmail
	 * @return success
	 */
	public boolean addMember(Person member) {		
		if (member != null) {
			
			if (!members.containsKey(member.getName())) {
				
				// make member teammate of other members
				// make existing members teammate of member
				for (Person teammate : members.values()) {
					member.addTeammate(teammate);
					teammate.addTeammate(member);
				}
				
				// add this team to the member's team list
				member.addTeam(this);
				
				// add member to this team's member list
				members.put(member.getName(), member);	
				
			} else {
				System.out.println(member.getName() + " is already in " + name);
			}

			return true;
			
		} else {
			System.err.println("Team.addMember() failed!");
			return false;
		}
	}
	
	/**
	 * Removes a member from the team. Also removes teammate connections.
	 * @param member to remove
	 */
	public void removeMember(Person member) {
		
		// remove member from members
		members.remove(member.getName());
		
		// loop through remaining teammates
		for (Person remainingTeammate : members.values()) {
			remainingTeammate.removeTeammate(member);	// remove member from teammate
			member.removeTeammate(remainingTeammate); // remove teammate from member
		}
		
		// remove team from member
		member.removeTeam(this);
	}
	
	/**
	 * Returns the string set of members of the team.
	 * 
	 * Copys keyset as it is modified later. Not doing so would cause a 
	 * ConcurrentModificationException.
	 * @see Organization.removeTeam();
	 */
	public Set<String> getMembers() {
		return new HashSet<String>(members.keySet());
	}
	
	/**
	 * Prints members of team.
	 */
	public void printMembers() {
		for (String member : members.keySet()) {
			System.out.println(member);
		}
	}
	
	public boolean equals(Object other) {
		return name.equals(((Team) other).getName());
	}
	
}
