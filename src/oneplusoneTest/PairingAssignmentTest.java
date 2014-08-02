package oneplusoneTest;

import static org.junit.Assert.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import oneplusone.PairingAssignment;
import oneplusone.Person;
import oneplusone.Person.Teammate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PairingAssignmentTest {
	private static PrintStream original;
	private static PairingAssignment tester;
	private static Person m1;
	private static Person m2;
	private static Person m3;

	/**
	 * Called before every test.
	 * 
	 * DO NOT MODIFY!!!
	 * Many tests are dependent on this set up.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		// silences print statements. 
		// thanks: http://stackoverflow.com/a/18804033/3739861
		original = System.out;
		System.setOut(new NullPrintStream());
		
		ArrayList<Person> members = new ArrayList<Person>();
		
		m1 = new Person("m1");
		m2 = new Person("m2");
		m3 = new Person("m3");
		
		m1.addTeammate(m2);
		m2.addTeammate(m1);
		
		// m1 teammates: m2
		// m2 teammates: m1
		// m3 teammates: none
		
		members.add(m1);
		members.add(m2);
		members.add(m3);
		
		tester = new PairingAssignment(members);	
	}
	
	/**
	 * Called after every test.
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		System.setOut(original);
	}
	
	@Test
	public void assignPairingsWorksBothWays() {
		tester.assignPairing(m1, m1.getTeammates().get(0));
		
		HashMap<Person, HashSet<Person>> pairings = tester.getCopyOfPairings();
		
		assertTrue(pairings.get(m1).contains(m2));
		assertTrue(pairings.get(m2).contains(m1));
	}
	
	@Test
	public void removingPairingsWorks() {
		tester.assignPairing(m1, m1.getTeammates().get(0));
		tester.unassignPairing(m1, m1.getTeammates().get(0));
		
		HashMap<Person, HashSet<Person>> pairings = tester.getCopyOfPairings();
		
		assertFalse(pairings.get(m1).contains(m2));
		assertFalse(pairings.get(m2).contains(m1));
	}

	@Test
	public void alreadyPairedAssignedCostOfZero() { 
		tester.assignPairing(m1, m1.getTeammates().get(0));
		
		ArrayList<ArrayList<Teammate>> sorted;
		sorted = tester.getSortedTeammates(m1, "");
		
		// m2 teammate has cost of 0 b/c already paired
		assertTrue(sorted.get(0).get(0).person == m2);
	}
	
	@Test
	public void teammateAssignedCostOfOne() { 
		ArrayList<ArrayList<Teammate>> sorted;
		sorted = tester.getSortedTeammates(m1, "");
		
		// m2 teammate has cost of 1 b/c teammate
		assertTrue(sorted.get(1).get(0).person == m2);
	}

	@Test
	public void teammateAlreadyAssignedToSomeoneElseGivenCostOfTwo() {
		m3.addTeammate(m2);
		m2.addTeammate(m3);
		
		// m1 teammates: m2
		// m2 teammates: m1 and m3
		// m3 teammates: m2
		
		// assign m1 to m2
		tester.assignPairing(m1, m1.getTeammates().get(0));

		// get sorted teammates for m3
		ArrayList<ArrayList<Teammate>> sorted;
		sorted = tester.getSortedTeammates(m3, "");
		
		// m2 is cost two b/c already assigned to m1
		assertTrue(sorted.get(2).get(0).person == m2);
	}
	
	@Test
	public void teammateMatchedInCycleGivenCostOfThree() {		
		m3.addTeammate(m2);
		m2.addTeammate(m3);
				
		// m1 teammates: m2
		// m2 teammates: m1, m3
		// m3 teammates: m2	
		
		// first matching
		// m2 matched with m1
		HashSet<Person> firstMatchup = new HashSet<Person>();
		firstMatchup.add(m1);
		m2.setPreviousMatchups(firstMatchup);
		
		// second matching
		// m2 matched with m3
		HashSet<Person> secondMatchup = new HashSet<Person>();
		secondMatchup.add(m3);
		m2.setPreviousMatchups(secondMatchup);
		
		// m1 should not be given a cost of 3 to be paired with m2
		// assuming cycle has not reset. which it hasn't, because that 
		// is done externally of person object in PairingAssignment.record() 
		ArrayList<ArrayList<Teammate>> sorted;
		sorted = tester.getSortedTeammates(m2, "");
		
		// m2 is cost two b/c already assigned to m1
		assertTrue(sorted.get(3).get(0).person == m1);
	}
	
	@Test
	public void teammateMatchedLastTimeGivenCostOfFour() {
		// create matching
		// m2 matched with m1
		HashSet<Person> matchup = new HashSet<Person>();
		matchup.add(m1);
		m2.setPreviousMatchups(matchup);
		
		// m1 should not be given a cost of 3 to be paired with m2
		// assuming cycle has not reset. which it hasn't, because that 
		// is done externally of person object in PairingAssignment.record() 
		ArrayList<ArrayList<Teammate>> sorted;
		sorted = tester.getSortedTeammates(m2, "");
		
		// m2 is cost two b/c already assigned to m1
		assertTrue(sorted.get(4).get(0).person == m1);
	}
	
	@Test
	public void teammateMatchedLastTimeAndAlreadyPairedGivenCostOfFour() {
		m3.addTeammate(m2);
		m2.addTeammate(m3);
				
		// m1 teammates: m2
		// m2 teammates: m1, m3
		// m3 teammates: m2	
		
		// create matching
		// m1 matched up with m2
		HashSet<Person> matchup = new HashSet<Person>();
		matchup.add(m2);
		m1.setPreviousMatchups(matchup);
		
		// get m3 teammate wrapper in terms of m2
		ArrayList<Teammate> m2teammates = m2.getTeammates();
		int m3index = 0;
		for (m3index = 0; m3index < m2teammates.size(); m3index++) {
			if (m2teammates.get(m3index).person == m3) {
				break;
			}
		}
		// m3index is now the index of the m3 teammate in m2's teammate list
		
		// set m2's current pairing as m3
		tester.assignPairing(m2, m2teammates.get(m3index));
		
		// m1 should not be given a cost of 3 to be paired with m2
		// assuming cycle has not reset. which it hasn't, because that 
		// is done externally of person object in PairingAssignment.record() 
		ArrayList<ArrayList<Teammate>> sorted;
		sorted = tester.getSortedTeammates(m1, "");
		
		// m2 is cost two b/c already assigned to m1
		assertTrue(sorted.get(5).get(0).person == m2);
	}
}


