/**
 * Instances of this class represent a message with a sender, receiver, id, subject and a message body.
 */
package se.kth.anderssonljung.twittbook.entities;

public class Message {
	private String receiver;
	private String sender;
	private int id;
	private String message;
	private String subject;

	public Message() {
	}

	public Message(int messageid, String receiver, String sender,
			String message, String subject) {
		this.setId(messageid);
		this.setReceiver(receiver);
		this.setSender(sender);
		this.setMessage(message);
		this.setSubject(subject);
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
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
	public String toString() {
		return sender + ": " + subject;

	}
}
