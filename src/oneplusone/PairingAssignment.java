/**
 * 
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
	public static final int MAX_COST = 5;
	private static final boolean TEST = WeightedCSP.TEST;
	private HashMap<Person, HashSet<Person>> pairings; 

	public PairingAssignment(ArrayList<Person> sortedMembers) {
		pairings = new HashMap<Person, HashSet<Person>>();
		
		for (int i = 0; i < sortedMembers.size(); i++) {
			pairings.put(sortedMembers.get(i), new HashSet<Person>());
		}
	}
	
	/**
	 * Clones a pairing assignment.
	 * @param pairings
	 */
	public PairingAssignment(PairingAssignment another) {
		pairings = new HashMap<Person, HashSet<Person>>();
		
		for (Person person : another.pairings.keySet()) {
			pairings.put(person, new HashSet<Person>(another.pairings.get(person)));
		}
	}
	
	/**
	 * 2D array of cost of pairing to teammate to pair with.
	 * 
	 * 0 - already teamed up
	 * 1 - no issues
	 * 2 - previously matched up with in this cycle
	 * 3 - matched with someone else
	 * 4 - last person matched up with
	 * 5 - last person matched up with, matched with someone else
	 * 
	 * @param p
	 * @return
	 */
	public ArrayList<ArrayList<Teammate>> getSortedTeammates(Person p, String tabs) {
		if (p == null) return null;
		
		if (TEST) System.out.println(tabs + "sorting teammates");
		
		ArrayList<ArrayList<Teammate>> sorted =new ArrayList<ArrayList<Teammate>>();
		for (int c = 0; c <= MAX_COST; c++) sorted.add(new ArrayList<Teammate>());
		
		for (Teammate tm : p.getTeammates()) {
			boolean matchedPreviously = tm.matchedInCycle();
			boolean matchedUp = pairings.get(tm.person).size() > 0;
			boolean matchedLastTime = p.previouslyMatchedWith(tm.person);
			boolean alreadyPaired = pairings.get(p).contains(tm.person);
			int cost;
			
			if (alreadyPaired) {
				cost = 0;
				
			} else if (matchedLastTime & matchedUp) {
				cost = 5;
				
			} else if (matchedLastTime) {
				cost = 4;

			} else if (matchedPreviously) {
				cost = 3;

			} else if (matchedUp) {
				cost = 2;
				
			} else { // plain add
				cost = 1;
			}
			
			sorted.get(cost).add(tm);
			if (TEST) System.out.println(tabs + "\t" + tm.person.getName() + " given cost of " + cost);
		}
		
		// shuffle teammates that have the same cost
		for (int c = 0; c <= MAX_COST; c++) Collections.shuffle(sorted.get(c));
		
		return sorted;
	}
	
	/**
	 * 
	 * @param indexOfPerson
	 * @param teammate
	 */
	public void assignTeammate(Person person, Teammate teammate) {
		pairings.get(person).add(teammate.person);			
		pairings.get(teammate.person).add(person);
		
		person.matchTeammate(teammate.person);
		teammate.person.matchTeammate(person);
	}
	
	/**
	 * 
	 * @param indexOfPerson
	 * @param teammate
	 */
	public void unassignTeammate(Person person, Teammate teammate) {
		pairings.get(person).remove(teammate.person);		
		pairings.get(teammate.person).remove(person);
		
		person.unmatchTeammate(teammate.person);
		teammate.person.unmatchTeammate(person);
	}

	public String toString() {
		StringBuffer pairingsBuffer = new StringBuffer();
		
		// get members, sort alphabetically
		ArrayList<Person> members = new ArrayList<Person>(pairings.keySet());
		Collections.sort(members, new AlphaCompare());
		
		Date date = new Date();
		pairingsBuffer.append("\n1+1 Pairings generated for " + date + ": \n");
		
		for (Person member : members) {
			pairingsBuffer.append("\t" + member.getName() + ": \n");
			
			for (Person paired : pairings.get(member)) {
				pairingsBuffer.append("\t\t" + paired.getName() + "\n");
			}			
		}
		
		return pairingsBuffer.toString();
	}
	
	/**
	 * Get's ready for organization to be called.
	 * Saves matchup of assignment.
	 * Refreshes matched attribute of teammates if teammate cycle is over.
	 */
	public void record() {
		for (Person p : pairings.keySet()) {
			p.setPreviousMatchups(pairings.get(p));
			
			if (p.cycleFull()) {
				if (TEST) System.out.println("resetting cycle for " + p);
				p.resetCycle();
			}
		}
	}
	
	class AlphaCompare implements Comparator<Person> {
    public int compare(Person o1, Person o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
    }
	}

	public boolean isCompleted(int indexOfMember) {
		return indexOfMember >= pairings.keySet().size();
	}

	public boolean hasTeammate(Person current) {
		return pairings.get(current).size() > 0;
	}


}
