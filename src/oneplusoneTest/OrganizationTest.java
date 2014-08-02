/**
 * 
 */
package oneplusoneTest;

import static org.junit.Assert.*;
import java.io.PrintStream;
import org.junit.Test;
import oneplusone.Organization;

/**
 * @author danielchen
 *
 */
public class OrganizationTest {

	@Test
	public void nameShouldBeRetrievedCorrectly() {
		Organization tester = new Organization("test");
		
		assertEquals("test", tester.getName());
	}
	
	@Test
	public void addMemberTeamForcedShouldAddMembersAndTeam() {
		Organization tester = new Organization("test");
		
		tester.addMemberToTeam("person", "team", true);
		
		assertTrue(tester.hasTeam("team"));
		assertTrue(tester.hasMember("person"));
	}
	
	@Test
	public void addMemberTeamUnforcedShouldNotAddNewMembersAndNewTeams() {
		Organization tester = new Organization("test");
		
		// silence print statements:
		// thanks stack overflow: http://stackoverflow.com/a/18804033/3739861
		PrintStream original = System.out;
		System.setOut(new NullPrintStream());
		
		assertFalse(tester.addMemberToTeam("person", "team", false));
		System.setOut(original);

		assertFalse(tester.hasTeam("team"));
		assertFalse(tester.hasMember("person"));
	}
	
	@Test
	public void removeMemberShouldRemoveMemberFromTeamButNotTeam() {
		Organization tester = new Organization("test");
		
		tester.addMemberToTeam("person", "team", true);
		
		PrintStream original = System.out;
		System.setOut(new NullPrintStream());
		tester.removeMember("person");
		System.setOut(original);
		
		assertFalse(tester.hasMember("person"));
		assertTrue(tester.hasTeam("team"));
	}
	
	@Test
	public void removeTeamShouldRemoveTeamButNotMember() {
		Organization tester = new Organization("test");
		
		tester.addMemberToTeam("person", "team", true);
		
		PrintStream original = System.out;
		System.setOut(new NullPrintStream());
		tester.removeTeam("team");
		System.setOut(original);
		
		assertTrue(tester.hasMember("person"));
		assertFalse(tester.hasTeam("team"));	
	}
	
	@Test
	public void membersAndTeamsShouldBeRetrievable() {
		Organization tester = new Organization("test");
		
		tester.addMemberToTeam("person", "team", true);
		
		assertNotNull(tester.getMember("person"));
		assertNotNull(tester.getTeam("team"));
	}
	
	@Test
	public void getPairingsShouldGetResult() {
		Organization tester = new Organization("test");
		assertNotNull(tester.getPairings());
	}
	
}
