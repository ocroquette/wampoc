package fr.ocroquette.wampoc.server;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang.builder.ToStringBuilder;

import fr.ocroquette.wampoc.common.Channel;

/*** WAMP session as seen from the server's perspective
 *  
 */
public class Session {

	public Session(Channel channel) {
		id = generateId();
		this.channel = channel;
		this.isOpen = true;
	}
	
	public String getId() {
		return id;
	}

	protected String generateId() {
		return (UUID.randomUUID() + "-" + UUID.randomUUID()).replace("-", "");
	}

	public Channel getChannel() {
		return channel;
	}
	
	public void sendMessage(String message) throws IOException {
		channel.handle(message);
	}
	
	// When the server considers the session as closed, it will call this method
	public void onClose() {
		isOpen = false;
	}
	
	public boolean isOpen() {
		return isOpen;
	}

	public void close() {
		isOpen = false;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	protected String id;
	protected Channel channel;
	protected boolean isOpen;
}
