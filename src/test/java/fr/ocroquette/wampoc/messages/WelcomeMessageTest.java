package fr.ocroquette.wampoc.messages;

import static fr.ocroquette.wampoc.testutils.Utils.q;
import static fr.ocroquette.wampoc.testutils.Utils.rndStr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WelcomeMessageTest implements TestsNotToForget {
	@Override
	public void constructMessage() {
		String sessionId = rndStr(); 
		String serverIdent = rndStr(); 
		WelcomeMessage welcomeMessage = new WelcomeMessage(sessionId, serverIdent);
		assertEquals(sessionId, welcomeMessage.sessionId);
		assertEquals(serverIdent, welcomeMessage.serverIdent);
	}

	@Override
	public void testEquals() {
		WelcomeMessage msg1 = new WelcomeMessage("1","2"); 
		WelcomeMessage msg1bis = new WelcomeMessage("1","2"); 
		WelcomeMessage msg2 = new WelcomeMessage("1","3"); 
		WelcomeMessage msg3 = new WelcomeMessage("2","2"); 
		assertTrue(msg1.equals(msg1bis));
		assertFalse(msg1.equals(msg2));
		assertFalse(msg1.equals(msg3));
	}

	@Test
	public void validWelcomeMessage() {
		String sessionId = rndStr(); 
		String serverIdent = rndStr(); 

		WelcomeMessage welcomeMessage = new WelcomeMessage(sessionId, serverIdent);
		String json = MessageMapper.toJson(welcomeMessage);
		assertEquals("[0," + q(sessionId) + ",1,"+q(serverIdent)+"]", json);

		WelcomeMessage clone = (WelcomeMessage) MessageMapper.fromJson(json);
		assertTrue("Welcome message from a valid string must be valid", welcomeMessage.isValid());
		assertEquals(1,clone.protocolVersion);
		assertEquals(serverIdent, clone.serverIdent);
		assertEquals(sessionId, clone.sessionId);
	}

	@Test
	public void unserializeInvalidMessages() {
		MessagesTest.verifyFailingParsing(WelcomeMessage.class, new String[] {
			"?",
			"[0]",
			"[0,\"\"]",
			"[0,\"\",1]",
		});
	}

}
