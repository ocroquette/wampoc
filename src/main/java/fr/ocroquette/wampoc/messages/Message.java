package fr.ocroquette.wampoc.messages;


public abstract class Message {
	Message(MessageType type) {
		this.type = type;
	}
	
	public MessageType getType() {
		return type;
	}

	protected final MessageType type;
}
