/**
 * 
 */
package oneplusone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import oneplusone.Person.Teammate;

public class PairingAssignment {
	ArrayList<HashSet<Person>> pairings; // id of person -- hashset of pairings

	// ordered from least to most teammates
	ArrayList<Person> dir;
	HashMap<Person, Integer> reversedir;
	
	public PairingAssignment(ArrayList<Person> sortedMembers) {
		dir = sortedMembers;
		pairings = new ArrayList<HashSet<Person>>();
		
		for (int i = 0; i < sortedMembers.size(); i++) {
			pairings.set(i, new HashSet<Person>());
			reversedir.put(sortedMembers.get(i), i);
		}
	}
	
	//TODO sort teammates function
	
	public ArrayList<ArrayList<Person>> getSortedTeammates(Person p) {
		return null;
	}
	
	/**
	 * 
	 * @param indexOfPerson
	 * @param teammate
	 */
	public void assignTeammate(int indexOfPerson, Teammate teammate) {
		pairings.get(indexOfPerson).add(teammate.person);				
		pairings.get(reversedir.get(teammate.person)).add(dir.get(indexOfPerson));
		
		dir.get(indexOfPerson).matchTeammate(teammate.person);
		teammate.person.matchTeammate(dir.get(indexOfPerson));
	}
	
	/**
	 * 
	 * @param indexOfPerson
	 * @param teammate
	 */
	public void unassignTeammate(int indexOfPerson, Teammate teammate) {
		pairings.get(indexOfPerson).remove(teammate);
		pairings.get(reversedir.get(teammate.person)).remove(dir.get(indexOfPerson));
		
		dir.get(indexOfPerson).unmatchTeammate(teammate.person);
		teammate.person.unmatchTeammate(dir.get(indexOfPerson));
	}

	public String toString() {
		return "need to write toString()";
	}
}
