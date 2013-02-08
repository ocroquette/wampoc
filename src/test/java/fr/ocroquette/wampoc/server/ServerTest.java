package fr.ocroquette.wampoc.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

import fr.ocroquette.wampoc.exceptions.BadArgumentException;
import fr.ocroquette.wampoc.messages.MessageMapper;
import fr.ocroquette.wampoc.messages.WelcomeMessage;
import fr.ocroquette.wampoc.testutils.ErrorChannel;
import fr.ocroquette.wampoc.testutils.ProtocollingChannel;

public class ServerTest {

	@Test
	public void serverMustWelcomeClient() throws IOException {
		String serverIdent = UUID.randomUUID().toString();
		WampServer server = new WampServer(serverIdent);
		ProtocollingChannel channel = new ProtocollingChannel(); 
		assertEquals(0, channel.handledMessages.size());
		Session session = server.openSession(channel);
		assertEquals(1, channel.handledMessages.size());
		WelcomeMessage welcomeMessage = (WelcomeMessage) MessageMapper.fromJson(channel.handledMessages.get(0));

		assertTrue(session.isOpen());

		assertTrue("Welcome message from the server must be valid", welcomeMessage.isValid());
		assertEquals(serverIdent, welcomeMessage.serverIdent);
		assertTrue("Session id must be set", welcomeMessage.sessionId != null);
		assertTrue("Session id must not be empty", welcomeMessage.sessionId.length() > 0);
	}

	@Test(expected=BadArgumentException.class)
	public void invalidInputMessage() throws IOException, BadArgumentException {
		String serverIdent = UUID.randomUUID().toString();
		WampServer server = new WampServer(serverIdent);
		ProtocollingChannel channel = new ProtocollingChannel(); 
		Session session = server.openSession(channel);
		server.handleIncomingString(session, "[]");
	}

	@Test(expected=BadArgumentException.class)
	public void invalidSession() throws IOException, BadArgumentException {
		String serverIdent = UUID.randomUUID().toString();
		WampServer server = new WampServer(serverIdent);
		ProtocollingChannel channel = new ProtocollingChannel(); 
		Session session = new Session(channel);
		server.handleIncomingString(session, "[7, \"http://example.com/simple\", \"Hello, world!\"]");
	}

	@Test
	public void connectionInterrupted()  {
		String serverIdent = UUID.randomUUID().toString();
		WampServer server = new WampServer(serverIdent);
		ErrorChannel channel = new ErrorChannel(); 
		Session session = server.openSession(channel);
		assertFalse(session.isOpen());
	}
}
