/**
 * WeightedCSP.java
 * 
 * Object which solves weighted constraint satisfaction problem posted by
 * oneplusone.
 * 
 * Constructor takes in a list of members in the organization.
 * 
 * solve() returns a PairingAssignment with a pairing given those members. It 
 * calls a recursive function, wBacktracking (short for weighted backtracking)
 */

package oneplusone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import oneplusone.Person.Teammate;

public class WeightedCSP {
	public static final boolean TEST = false;
	PairingAssignment assignment; 
	ArrayList<Teammate> bestPartner; 
	ArrayList<Person> sortedMembers; // sorted from lowest to highest # teammates
	int recursiveCalls;
	/**
	 * Constructor for WeightedCSP
	 * Sorts list of members by lowest to highest number of teammates.
	 * @see TeammatesCompare
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
	 * @return optimal PairingAssignment given sortedMembers
	 */
	public PairingAssignment solve() {
		// create new assignment
		assignment = new PairingAssignment(sortedMembers);
		
		// instantiate new bestPartner list
		bestPartner = new ArrayList<Teammate>();
		for (int i = 0; i < sortedMembers.size(); i++) { bestPartner.add(null); }
		
		recursiveCalls = 0;
		
		// call recursive backtracking method which uses assignment to parse 
		// through options, storing best partnerships in bestPartner list.
		int cost = wBacktracking(0, Integer.MAX_VALUE, 0);
		
		if (cost == -1) { System.err.println("There was an error w/ solving CSP.");}
		if (TEST) System.out.println("Cost of: " + cost);
		if (TEST) System.out.println("Recursive calls: " + recursiveCalls);
		
		// after backtracking, assignment actually has no assignments in it, as all
		// assignments are reversed in backtracking.

		// to get the optimal assignment stored by wBacktracking, we use bestPartner
		// list to add the optimal teammate pairings.		
		for (int i = 0; i < bestPartner.size(); i++) {
			Teammate teammate = bestPartner.get(i);
			if (teammate != null) {
				assignment.assignPairing(sortedMembers.get(i), teammate);
			}
		}
		
		// records pairing data into Person objects, so that we remember who
		// the last teammates were, and which teammates have been matched up with
		// in the current cycle
		assignment.record();
		
		return assignment;
	}

	/**
	 * Implementation of weighted backtracking to produce a bestPartner list.
	 * 
	 * @param indexOfMember, starting at 0
	 * @param bestSoFar, starting at the maximum integer
	 * @param costSoFar, starting at 0
	 * @return cost of assignment
	 */
	private int wBacktracking(int indexOfMember, int bestSoFar, int costSoFar) {
		recursiveCalls++;
		if (assignment == null) { return -1; }
		
		// tabs used for prints in testing. corresponds to depth of recursion.
		// thanks stack overflow -- http://stackoverflow.com/a/4903603/3739861
		String tabs = new String(new char[indexOfMember]).replace("\0", "\t");
		
		// check if all members have been iterated through, return cost if so
		if (assignment.isCompleted(indexOfMember)) {
			if(TEST) System.out.println(tabs+"Cost "+costSoFar+" found.");
			return costSoFar;
		
		// iterate through all sensible options for teammates for this person
		// recursively find the cost of picking that teammate, and store the best
		// teammate into the bestPartner list.
		} else {		
			
			Person current = sortedMembers.get(indexOfMember);
			if (TEST) { System.out.println(tabs+"Finding pair for "+ current); }
			
			// get teammates sorted in terms of immediate cost to solution.
			// ArrayList is 2D array, indices cost of teammate, values list of 
			// teammate with taht cost.
			ArrayList<ArrayList<Teammate>> sortedTM = 
					assignment.getSortedTeammates(current, tabs);
			
			// loop through each cost, unless adding that cost would give us a worse
			// value than the value we have found so far.
			for (int cost = 0; cost <= PairingAssignment.MAX_COST 
					&& cost + costSoFar < bestSoFar; cost++ ) {
				
				// for each teammate at the cost, see what the best cost of using that
				// teammate is. store best teammate.
				for (Teammate tm : sortedTM.get(cost)) {	
					if (TEST) { System.out.println(tabs+"\tAttempting "+ tm.person); }
					
					assignment.assignPairing(current, tm);
					
					int pathCost = 
							wBacktracking(indexOfMember+1, bestSoFar, costSoFar + cost);
					
					// if choosing this teammate results in the lowest cost found so far
					// at this depth, set as new best Partner.
					if (pathCost < bestSoFar) {
						bestSoFar = pathCost;
						bestPartner.set(indexOfMember, tm);
						if (TEST) {
							System.out.println(tabs+"\tBest teammate set to "+ tm.person);
						}
					} 
					
					assignment.unassignPairing(current, tm);
				}
			}
			
			// if there is no bestPartner set, that means the member does not actually
			// have any teammates and wBacktracking was never called from this depth.
			// Call wBacktracking to get bestPartners for the reset of the members.
			if (bestPartner.get(indexOfMember) == null) {
				wBacktracking(indexOfMember+1, bestSoFar, costSoFar);
			}
			
			// corresponds to cost of partnering w/ bestPartner at index of member
			return bestSoFar;
		}
	}

	/**
	 * Comparator used to compare two people by the number of teammates.
	 * Sorts from lowest to highest.
	 * Used in constructor on the list of people in organization.
	 */
	class TeammatesCompare implements Comparator<Person> {
    public int compare(Person o1, Person o2) {
        return o1.teammateCount() - o2.teammateCount();
    }
	}
}
