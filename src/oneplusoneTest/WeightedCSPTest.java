/**
 * JUnit test for WeightedCSP
 * 
 * Only tests for basic functionality, as results are random
 * to an extent and the optimal result is difficult to find by
 * hand except in the most trivial of cases. 
 * 
 * Unit testing is not a good way of evaluating result of solve.
 * Looking at results, seeing if they "make sense" is the only option
 * for more complex cases (such as the Beatles organization) 
 * 
 * Run unit tests from TestDriver.java
 */

package oneplusoneTest;

import static org.junit.Assert.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import oneplusone.PairingAssignment;
import oneplusone.Person;
import oneplusone.Team;
import oneplusone.WeightedCSP;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WeightedCSPTest {
	private static PrintStream original;
	private static WeightedCSP tester;
	private static ArrayList<Person> people;
	private static Person p1;
	private static Person p2;
	private static Person p3;
	private static Person p4;
	private static Person p5;
	private static Person p6;
	private static Person p7;
	private static Person p8;
	private static HashMap<Person, HashSet<Person>> culmulativePairings;

	@Before
	public void setUp() throws Exception {
		
		// silences print statements. 
		// thanks: http://stackoverflow.com/a/18804033/3739861
		original = System.out;
		System.setOut(new NullPrintStream());
		
		people = new ArrayList<Person>();

		p1 = new Person("p1");
		p2 = new Person("p2");
		p3 = new Person("p3");
		p4 = new Person("p4");
		p5 = new Person("p5");
		p6 = new Person("p6");
		p7 = new Person("p7");
		p8 = new Person("p8");

		people.add(p1);
		people.add(p2);
		people.add(p3);
		people.add(p4);
		people.add(p5);
		people.add(p6);
		people.add(p7);
		people.add(p8);
		
		// keeps track of what teams have been matched up with
		culmulativePairings = new HashMap<Person, HashSet<Person>>();
		culmulativePairings.put(p1, new HashSet<Person>());
		culmulativePairings.put(p2, new HashSet<Person>());
		culmulativePairings.put(p3, new HashSet<Person>());
		culmulativePairings.put(p4, new HashSet<Person>());
		culmulativePairings.put(p5, new HashSet<Person>());
		culmulativePairings.put(p6, new HashSet<Person>());
		culmulativePairings.put(p7, new HashSet<Person>());
		culmulativePairings.put(p8, new HashSet<Person>());

		tester = new WeightedCSP(people);
	}

	@After
	public void tearDown() throws Exception {
		System.setOut(original);
	}
	
	/**
	 * Test for organization with two teams of four members each
	 * 
	 * solve is called 3 teams. Each time, the pairing should generate a pairing
	 * with members that that member has not been paired with yet.
	 */
	@Test
	public void twoTeamsEightMembersTest() {
		
		// create team structure
		Team team1 = new Team("team1");
		team1.addMember(p1);
		team1.addMember(p2);
		team1.addMember(p3);
		team1.addMember(p4);

		Team team2 = new Team("team2");
		team2.addMember(p5);
		team2.addMember(p6);
		team2.addMember(p7);
		team2.addMember(p8);
		
		for (int i = 0; i < 3; i++) {
			PairingAssignment solution = tester.solve();
			HashMap<Person, HashSet<Person>> pairingMap = 
					solution.getCopyOfPairings();
						
			for (Person key : pairingMap.keySet()) {
				
				HashSet<Person> matches = pairingMap.get(key);
				
				// there should only be one pair
				assertEquals(1, matches.size());
				
				for (Person match : matches) {
					
					// match cannot have occured in the past
					assertFalse(culmulativePairings.get(key).contains(match));
					
					culmulativePairings.get(key).add(match);
				}
			}
		}		
	}	
	
	/**
	 * Test for 8 member organization with three teams of four members. Four of
	 * the members are members of two teams.
	 * 
	 * solve is called 3 teams. Each time, the pairing should generate a pairing
	 * for to members who that member has not been paired with before.
	 */
	@Test
	public void threeTeamsEightMembersTest() {
		
		// create team structure
		Team team1 = new Team("team1");
		team1.addMember(p1);
		team1.addMember(p2);
		team1.addMember(p3);
		team1.addMember(p4);

		Team team2 = new Team("team2");
		team2.addMember(p5);
		team2.addMember(p6);
		team2.addMember(p7);
		team2.addMember(p8);
		
		Team team3 = new Team("team3");
		team3.addMember(p3);
		team3.addMember(p4);
		team3.addMember(p5);
		team3.addMember(p6);
		
		for (int i = 0; i < 3; i++) {
			PairingAssignment solution = tester.solve();
			HashMap<Person, HashSet<Person>> pairingMap = 
					solution.getCopyOfPairings();
						
			for (Person key : pairingMap.keySet()) {
				
				HashSet<Person> matches = pairingMap.get(key);
								
				for (Person match : matches) {
					
					// match cannot have occurred in the past
					assertFalse(culmulativePairings.get(key).contains(match));
					
					culmulativePairings.get(key).add(match);
				}
			}
		}		
	}	
}



