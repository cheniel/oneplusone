/**
 * 
 */

package oneplusone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import oneplusone.Person.Teammate;

public class WeightedCSP {
	public static final boolean TEST = false;
	PairingAssignment assignment; 
	ArrayList<Teammate> bestPartner; 
	ArrayList<Person> sortedMembers; // sorted from lowest to highest # of teammates
	
	/**
	 * Constructor for WeightedCSP
	 * 
	 * Sorts list of members by lowest to highest number of teammates.
	 * 
	 * @param people
	 */
	public WeightedCSP(ArrayList<Person> people) {	
		Collections.sort(people, new TeammatesCompare());
		sortedMembers = people;
		assignment = null;
		bestPartner = null;
	}

	/**
	 * Calls weightedBacktracking function to get optimal assignment.
	 * @return
	 */
	public PairingAssignment solve() {
		assignment = new PairingAssignment(sortedMembers);
		bestPartner = new ArrayList<Teammate>();
		for (int i = 0; i < sortedMembers.size(); i++) bestPartner.add(null);
		
		int cost = wBacktracking(0, Integer.MAX_VALUE, 0, 0);
		if (cost == -1) System.err.println("There was an error w/ solving CSP.");
		
		if (TEST) System.out.println("Cost of: " + cost);

		// use best partner list to create optimal assignment
		for (int i = 0; i < bestPartner.size(); i++) {
			Teammate teammate = bestPartner.get(i);
			if (teammate != null) {
				assignment.assignTeammate(sortedMembers.get(i), teammate);
			}
		}
		
		// set new previousMatches
		assignment.record();
		
		return assignment;
	}

	/**
	 * 
	 * @param indexOfMember
	 * @param bestSoFar
	 * @param costSoFar
	 * @return cost of assignment
	 */
	private int wBacktracking(int indexOfMember, int bestSoFar, int costSoFar, int depth) {
		if (assignment == null) return -1;
		
		String tabs = new String(new char[depth]).replace("\0", "\t");
		
		if (assignment.isCompleted(indexOfMember)) {
			
			if (TEST) System.out.println(tabs+"Assignment w/ cost "+ costSoFar +" found.");
			return costSoFar;
			
		} else {			
			Person current = sortedMembers.get(indexOfMember);
			
			if (TEST) System.out.println(tabs+"Finding pair for " + current.getName());
			
			ArrayList<ArrayList<Teammate>> sortedTM = 
					assignment.getSortedTeammates(current, tabs);
			
			for (int cost = 0; 
					cost <= PairingAssignment.MAX_COST && cost + costSoFar < bestSoFar; 
					cost++) {
				
				for (Teammate tm : sortedTM.get(cost)) {	
					
					if (TEST) System.out.println(tabs+"Paired "+ tm.person.getName() + " with " + current.getName());
					
					assignment.assignTeammate(current, tm);
					int pathCost = 
							wBacktracking(indexOfMember + 1, bestSoFar, costSoFar + cost, depth + 1);
					
					if (pathCost < bestSoFar) {
						if (TEST) System.out.println(tabs+"Best pair (" + current.getName() + "): " + tm.person.getName() + " "+pathCost+" saved.");
						bestSoFar = pathCost;
						bestPartner.set(indexOfMember, tm);
					} 
					
					assignment.unassignTeammate(current, tm);
				}
			}
			
			return bestSoFar;
		}
	}

	class TeammatesCompare implements Comparator<Person> {

    @Override
    public int compare(Person o1, Person o2) {
        return o1.teammateCount() - o2.teammateCount();
    }
	}
	
}
