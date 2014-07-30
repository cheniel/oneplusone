package oneplusone;

import java.util.HashSet;

public class Organization {
	private String name;
	private HashSet<Person> people;
	private HashSet<Team> teams;
	
	public Organization(String organizationName) {
		name = organizationName;
		people = new HashSet<Person>();
		teams = new HashSet<Team>();
	}
	
	
	
	public boolean hasTeam(String teamName) {
		return true;
	}
	
	

}
