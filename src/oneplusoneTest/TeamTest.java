package oneplusoneTest;

import static org.junit.Assert.*;
import java.io.PrintStream;
import java.util.Set;
import oneplusone.Person;
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
		
	}
}
