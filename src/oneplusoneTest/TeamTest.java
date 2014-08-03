/**
 * JUnit test for the Team class in oneplusone.
 * 
 * Run unit tests from TestDriver.java
 */

package oneplusoneTest;

import static org.junit.Assert.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;
import oneplusone.Person;
import oneplusone.Person.Teammate;
import oneplusone.Team;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TeamTest {
	private static PrintStream original;
	private static Team tester;
	
	/**
	 * Called before every test.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		// silences print statements. 
		// thanks: http://stackoverflow.com/a/18804033/3739861
		original = System.out;
		System.setOut(new NullPrintStream());
		
		tester = new Team("test");	
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
	public void nameShouldBeRetrievedCorrectly() {
		assertEquals("test", tester.getName());
	}
	
	@Test
	public void memberCanBeRetrievedAfterAdding() {
		Person newPerson = new Person("person");
		tester.addMember(newPerson);
		
		Person retrievedPerson = tester.getMember("person");
		
		assertEquals(newPerson, retrievedPerson);
	}
	
	@Test
	public void memberCannotBeAddedWithSameName() {
		Person newPerson = new Person("person");
		Person anotherPerson = new Person("person");
		
		assertTrue(tester.addMember(newPerson));
		assertFalse(tester.addMember(anotherPerson));
	}

	@Test
	public void memberIsDetectedCorrectly() {		
		assertFalse(tester.hasMember("person"));
		
		Person newPerson = new Person("person");
		tester.addMember(newPerson);
				
		assertTrue(tester.hasMember("person"));
	}
	
	@Test
	public void memberNamesCanBeRetrieved() {
		tester.addMember(new Person("person1"));
		tester.addMember(new Person("person2"));
		tester.addMember(new Person("person3"));
		
		Set<String> retrievedMembers = tester.getMembers();
		
		assertEquals(3, retrievedMembers.size());
		
		retrievedMembers.contains("person1");
		retrievedMembers.contains("person2");
		retrievedMembers.contains("person3");
	}
	
	@Test
	public void addingPersonToTeamAddsTeammates() {
		Person p1 = new Person("p1");
		Person p2 = new Person("p2");
		Person p3 = new Person("p3");
		
		tester.addMember(p1);
		tester.addMember(p2);
		tester.addMember(p3);
		
		ArrayList<Teammate> teammates = p1.getTeammates();
		
		assertEquals(teammates.size(), 2);
		
		boolean p2found = false;
		boolean p3found = false;
		
		for (Teammate tm : teammates) {
			boolean correctAndUnique = false;
			
			if (!p2found && tm.person == p2) {
				correctAndUnique = true;
			}
			
			if (!p3found && tm.person == p3) {
				correctAndUnique = true;
			}
			
			assertTrue(correctAndUnique);
		}
	}
}
