package oneplusoneTest;

import static org.junit.Assert.*;
import java.io.PrintStream;
import java.util.ArrayList;
import oneplusone.Person;
import oneplusone.Person.Teammate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PersonTest {
	private static PrintStream original;
	private static Person tester;
	private static final int SHARED_TEAMS = 3;

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
		
		tester = new Person("test");	
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
	public void addTeammateAddsATeammateAndIncreasesLinks() {
		Person anotherPerson = new Person("tm1");
				
		// equivalent to tester sharing 3 teams with anotherPerson
		for (int i=0;i<SHARED_TEAMS;i++) { tester.addTeammate(anotherPerson); }
		
		assertEquals(1, tester.teammateCount());
		
		ArrayList<Teammate> tms = tester.getTeammates();
		
		assertEquals(SHARED_TEAMS, tms.get(0).getLinks());
	}
	
	@Test
	public void removeTeammateDecreasesLinksThenRemoves() {
		Person anotherPerson = new Person("tm1");
		for (int i=0;i<SHARED_TEAMS;i++) { tester.addTeammate(anotherPerson); }
		
		// remove two links
		for (int i=1;i<SHARED_TEAMS;i++) {
			tester.removeTeammate(anotherPerson);
			ArrayList<Teammate> tms = tester.getTeammates();
			assertEquals(SHARED_TEAMS-i, tms.get(0).getLinks());
		}
		
		tester.removeTeammate(anotherPerson);
		
		// all teammate shared teams removed, should no longer
		// be listed as teammates
		assertEquals(0, tester.teammateCount());
	}
	
	@Test
	public void teammateCountRecordedCorrectly() {
		Person tm1 = new Person("tm1");
		Person tm2 = new Person("tm2");
		Person tm3 = new Person("tm3");

		tester.addTeammate(tm1);
		tester.addTeammate(tm1);
		tester.addTeammate(tm2);
		tester.addTeammate(tm2);
		tester.addTeammate(tm2);
		tester.addTeammate(tm3);
		
		assertEquals(3, tester.teammateCount());
	}
	
	@Test
	public void teammatesRetrievedCorrectly() {
		Person tm1 = new Person("tm1");
		Person tm2 = new Person("tm2");
		Person tm3 = new Person("tm3");

		tester.addTeammate(tm1);
		tester.addTeammate(tm1);
		tester.addTeammate(tm2);
		tester.addTeammate(tm2);
		tester.addTeammate(tm2);
		tester.addTeammate(tm3);
		
		ArrayList<Teammate> teammates = tester.getTeammates();
		assertEquals(3, tester.teammateCount());

		boolean tm1found = false;
		boolean tm2found = false;
		boolean tm3found = false;
		
		// check that all items are unique and are tm1, tm2, or tm3
		for (Teammate tm : teammates) {
			boolean existsNotDuplicate = false;
			
			if ((!tm1found && tm.person == tm1)
					|| (!tm2found && tm.person == tm2)
					|| (!tm3found && tm.person == tm3)) {
				existsNotDuplicate = true;
			}
			
			assertTrue(existsNotDuplicate);
		}
	}
	
	// other functions deeply tied to either Team or PairingAssignment.
	// see TeamTest and PairingAssignmentTest. Or they are getter/setter
	// functions.

}




