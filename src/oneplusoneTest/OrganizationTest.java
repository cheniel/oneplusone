/**
 * JUnit test for Organization class in oneplusone
 * 
 * Run unit tests from TestDriver.java
 */

package oneplusoneTest;

import static org.junit.Assert.*;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import oneplusone.Organization;

/**
 * @author danielchen
 *
 */
public class OrganizationTest {
	private static PrintStream original;
	private static Organization tester;
	
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
		
		tester = new Organization("test");	
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
	public void addMemberTeamForcedShouldAddMembersAndTeam() {
		tester.addMemberToTeam("person", "team", true);
		
		assertTrue(tester.hasTeam("team"));
		assertTrue(tester.hasMember("person"));
	}
	
	@Test
	public void addMemberTeamUnforcedShouldNotAddNewMembersAndNewTeams() {
		assertFalse(tester.addMemberToTeam("person", "team", false));

		assertFalse(tester.hasTeam("team"));
		assertFalse(tester.hasMember("person"));
	}
	
	@Test
	public void removeMemberShouldRemoveMemberFromTeamButNotTeam() {		
		tester.addMemberToTeam("person", "team", true);

		tester.removeMember("person");
		
		assertFalse(tester.hasMember("person"));
		assertTrue(tester.hasTeam("team"));
	}
	
	@Test
	public void removeTeamShouldRemoveTeamButNotMember() {		
		tester.addMemberToTeam("person", "team", true);
		
		tester.removeTeam("team");
		
		assertTrue(tester.hasMember("person"));
		assertFalse(tester.hasTeam("team"));	
	}
	
	@Test
	public void membersAndTeamsShouldBeRetrievable() {		
		tester.addMemberToTeam("person", "team", true);
		
		assertNotNull(tester.getMember("person"));
		assertNotNull(tester.getTeam("team"));
	}
	
	@Test
	public void getPairingsShouldGetResult() {
		assertNotNull(tester.getPairings());
	}
	
}
