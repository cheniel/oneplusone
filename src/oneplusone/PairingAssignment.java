/**
 * PairingAssignment.java
 * 
 * Used to store a pairing assignment for a given week.
 * Called by WeightedCSP, given to Organization.
 * 
 * Functions are used to assign and unassign pairings, in 
 * WeightedCSP.wBacktracking() to get data structures that are depending on
 * the current pairing and to get information on the status of the pairing.
 * 
 * record() is a function to solidify pairing data into Person objects for
 * record-keeping once the assignment is determined to be optimal and is sent
 * to the user of oneplusone.
 */
package oneplusone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import oneplusone.Person.Teammate;

public class PairingAssignment {
	private static final boolean TEST = WeightedCSP.TEST; 
	public static final int MAX_COST = 5; // per teammate add
	private HashMap<Person, HashSet<Person>> pairings; 

	/**
	 * Constructor for PairingAssignment
	 * Instantiates pairings HashMap.
	 * @param sortedMembers
	 */
	public PairingAssignment(ArrayList<Person> sortedMembers) {
		pairings = new HashMap<Person, HashSet<Person>>();
		for (int i = 0; i < sortedMembers.size(); i++) {
			pairings.put(sortedMembers.get(i), new HashSet<Person>());
		}
	}
	
	/**
	 * Creates a 2D array of cost of pairing to teammate to pair with and returns
	 * it. Used to order teammates in terms of their cost of pairing with the 
	 * parameter P person.
	 * 
	 * 0 - already teamed up with person
	 * 1 - no issues
	 * 2 - matched with someone else
	 * 3 - previously matched up with in this cycle
	 * 4 - last person matched up with (includes previous match up)
	 * 5 - last person matched up with, matched with someone else 
	 * 
	 * @param p Person to get teammates from
	 * @return 2D arraylist, index is the cost of pairing and, value is list of
	 * 	list of teammates at that cost.
	 */
	public ArrayList<ArrayList<Teammate>> getSortedTeammates(Person p, String tabs) {
		if (p == null) { return null; }
		if (TEST) { System.out.println(tabs + "\tsorting teammates"); }
		
		ArrayList<ArrayList<Teammate>> sorted= new ArrayList<ArrayList<Teammate>>();
		for (int c = 0; c <= MAX_COST; c++) {sorted.add(new ArrayList<Teammate>());}
		
		for (Teammate tm : p.getTeammates()) {
			boolean matchedPreviously = tm.matchedInCycle();
			boolean matchedUp = pairings.get(tm.person).size() > 0;
			boolean matchedLastTime = p.previouslyMatchedWith(tm.person);
			boolean alreadyPaired = pairings.get(p).contains(tm.person);
			int cost;
			
			// determine cost of teammate based on values
			if (alreadyPaired) { cost = 0; } // already paired with teammate
			else if (matchedLastTime & matchedUp) { cost = 5; } 
			else if (matchedLastTime) { cost = 4; } 
			else if (matchedPreviously) { cost = 3; } 
			else if (matchedUp) { cost = 2; } 
			else { cost = 1; } // regular add, no issues
			
			// add teammates to array under appropriate cost
			sorted.get(cost).add(tm);
			if (TEST) {
				System.out.println(tabs+"\t\t"+ tm.person +" given cost of "+ cost);
			}
		}
		
		// shuffle teammates that have the same cost
		for (int c = 0; c <= MAX_COST; c++) { Collections.shuffle(sorted.get(c)); }
		
		return sorted;
	}
	
	/**
	 * Assigns a person-teammate pair. Does both sides.
	 * @param Person 
	 * @param Teammate
	 */
	public void assignPairing(Person person, Teammate teammate) {
		pairings.get(person).add(teammate.person);			
		pairings.get(teammate.person).add(person);
	}
	
	/**
	 * Unassigns a person-teammate pair. Does both sides.
	 * @param Person
	 * @param Teammate teammate
	 */
	public void unassignPairing(Person person, Teammate teammate) {
		pairings.get(person).remove(teammate.person);		
		pairings.get(teammate.person).remove(person);
	}

	/**
	 * @param indexOfMember at recursive call to wBacktracking
	 * @return whether or not the assignment is complete at this point
	 */
	public boolean isCompleted(int indexOfMember) {
		return indexOfMember >= pairings.keySet().size();
	}
	
	/**
	 * After assignment has been determined to be optimal, this is called.
	 * Used to set-up bookkeeping in Person objects, in terms of who they have
	 * been matched up with before.
	 */
	public void record() {
		for (Person p : pairings.keySet()) {
			p.setPreviousMatchups(pairings.get(p));
			
			if (p.cycleFull()) {
				if (TEST) { System.out.println("resetting cycle for " + p); }
				p.resetCycle();
			}
		}
	}
	
	/**
	 * Returns a string with the pairings stored in this assignment.
	 */
	public String toString() {
		StringBuffer pairingsBuffer = new StringBuffer();
		
		// get members, sort alphabetically
		ArrayList<Person> members = new ArrayList<Person>(pairings.keySet());
		Collections.sort(members, new AlphaCompare());
		
		Date date = new Date();
		pairingsBuffer.append("\n1+1 Pairings generated for " + date + ": \n");
		
		// print member
		for (Person member : members) {
			pairingsBuffer.append("\t" + member.getName() + ": \n");
			
			// print matches with member
			for (Person paired : pairings.get(member)) {
				pairingsBuffer.append("\t\t" + paired.getName() + "\n");
			}			
		}
		
		return pairingsBuffer.toString();
	}
	
	/**
	 * Comparator that organizes people alphabetically in terms of name.
	 * @see toString();
	 */
	class AlphaCompare implements Comparator<Person> {
    public int compare(Person o1, Person o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
    }
	}
}
