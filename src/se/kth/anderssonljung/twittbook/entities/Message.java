package se.kth.anderssonljung.twittbook.entities;

public class Message {
	private int receiver;
	private int sender;
	private int id;
	private String message;
	private String subject;

	public Message(int messageid,int receiver, int sender, String message, String subject) {
		this.setId(messageid);
		this.setReceiver(receiver);
		this.setSender(sender);
		this.setMessage(message);
		this.setSubject(subject);
	}

	public int getReceiver() {
		return receiver;
	}

	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getSender() {
		return sender;
	}

	public void setSender(int sender) {
		this.sender = sender;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@Override
	public String toString(){
		return sender+": "+subject;
		
	}
}
