/**
 * Instances of this class represent a Twittbook user
 */
package se.kth.anderssonljung.twittbook.entities;

public class User {
	private String username;
	private String description;
	
	public User(String username, String description){
		this.setUsername(username);
		this.setDescription(description);
	}
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
