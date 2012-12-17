package fr.ocroquette.wampoc.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

import fr.ocroquette.wampoc.messages.MessageMapper;
import fr.ocroquette.wampoc.messages.WelcomeMessage;
import fr.ocroquette.wampoc.testutils.ProtocollingChannel;

public class ServerTest {

	@Test
	public void serverMustWelcomeClient() throws IOException {
		String serverIdent = UUID.randomUUID().toString();
		Server server = new Server(serverIdent);
		ProtocollingChannel channel = new ProtocollingChannel(); 
		assertEquals(0, channel.handledMessages.size());
		server.addClient(channel);
		assertEquals(1, channel.handledMessages.size());
		WelcomeMessage welcomeMessage = (WelcomeMessage) MessageMapper.fromJson(channel.handledMessages.get(0));

		assertTrue("Welcome message from the server must be valid", welcomeMessage.isValid());
		assertEquals(serverIdent, welcomeMessage.serverIdent);
		assertTrue("Session id must be set", welcomeMessage.sessionId != null);
		assertTrue("Session id must not be empty", welcomeMessage.sessionId.length() > 0);
	}

}
