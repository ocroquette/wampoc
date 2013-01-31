package fr.ocroquette.wampoc.messages;

import static fr.ocroquette.wampoc.testutils.Utils.q;
import static fr.ocroquette.wampoc.testutils.Utils.rndStr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UnsubscribeMessageTest implements TestsNotToForget {
	@Test
	public void constructMessage() {
		String topicUri = rndStr(); 
		UnsubscribeMessage msg = new UnsubscribeMessage(topicUri);
		assertEquals(topicUri, msg.topicUri);
	}

	public UnsubscribeMessage randomMessage() {
		return new UnsubscribeMessage(rndStr());
	}

	@Test
	@Override
	public void testEquals() {
		UnsubscribeMessage msg1 = new UnsubscribeMessage("1"); 
		UnsubscribeMessage msg1bis = new UnsubscribeMessage("1"); 
		UnsubscribeMessage msg2 = new UnsubscribeMessage("2"); 
		assertTrue(msg1.equals(msg1bis));
		assertFalse(msg1.equals(msg2));
	}
	
	@Test
	public void validUnsubscribe() {
		String topicUrl = rndStr(); 
		UnsubscribeMessage message = new UnsubscribeMessage(topicUrl);
		String json = MessageMapper.toJson(message);
		assertEquals("[6," + q(topicUrl) + "]", json);

		UnsubscribeMessage clone = (UnsubscribeMessage) MessageMapper.fromJson(json);
		assertEquals(MessageType.UNSUBSCRIBE, clone.getType());
		assertEquals(topicUrl, clone.topicUri);
	}

	@Test
	@Override
	public void unserializeInvalidMessages() {
		MessagesTest.verifyFailingParsing(CallErrorMessage.class, new String[] {
				"[6]",
		});
	}

}
