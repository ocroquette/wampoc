package fr.ocroquette.wampoc.messages;

import static fr.ocroquette.wampoc.testutils.Utils.q;
import static fr.ocroquette.wampoc.testutils.Utils.rndStr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class EventMessageTest implements TestsNotToForget {
	@Test
	public void constructMessage() {
		String topicUri = rndStr(); 
		EventMessage callMessage = new EventMessage(topicUri);
		assertEquals(topicUri, callMessage.topicUri);
	}
	
	public EventMessage randomMessage() {
		return new EventMessage(rndStr());
	}

	@Test
	public void testEquals() {
		EventMessage msg1 = new EventMessage("1"); 
		EventMessage msg1bis = new EventMessage("1"); 
		EventMessage msg2 = new EventMessage("2"); 
		assertTrue(msg1.equals(msg1bis));
		assertFalse(msg1.equals(msg2));
		
		SimplePayload payload1 = new SimplePayload("String", 1); 
		SimplePayload payload1bis = new SimplePayload("String", 1); 
		SimplePayload payload2 = new SimplePayload("String", 2); 
		msg1.setPayload(payload1);
		assertFalse(msg1.equals(msg1bis));
		msg1bis.setPayload(payload1bis);
		assertTrue(msg1.equals(msg1bis));
		msg1bis.setPayload(payload2);
		assertFalse(msg1.equals(msg1bis));
	}

	@Test
	public void unserializeWithPrimitivePayload() {
		String topicUri = rndStr(); 
		int payload = new Random().nextInt(); 
		EventMessage callMessage = new EventMessage(topicUri);
		callMessage.setPayload(payload);
		String json = MessageMapper.toJson(callMessage);
		assertEquals("[8," + q(topicUri) + ","+payload+"]", json);

		EventMessage clone = (EventMessage) MessageMapper.fromJson(json);
		assertEquals(MessageType.EVENT, clone.getType());
		assertEquals(topicUri, clone.topicUri);
		int clonePayload = clone.getPayload(Integer.class);
		assertEquals(payload, clonePayload);

		try {
			clone.getPayload(SimplePayload.class);
			assertTrue("getPayload shall throw an exception in case of wrong type", false);
		}
		catch(Exception e) {
		}
	}

	@Test
	public void unserializeWithComplexPayload() {
		String topicUri = rndStr(); 
		SimplePayload payload = new SimplePayload(rndStr(), new Random().nextInt() );
		EventMessage callMessage = new EventMessage(topicUri);
		callMessage.setPayload(payload);
		String json = MessageMapper.toJson(callMessage);
		assertEquals("[8," + q(topicUri) + ",{\"s\":"+q(payload.s)+",\"i\":"+payload.i+"}]", json);

		EventMessage clone = (EventMessage) MessageMapper.fromJson(json);
		assertEquals(MessageType.EVENT, clone.getType());
		assertEquals(topicUri, clone.topicUri);
		SimplePayload clonePayload = clone.getPayload(SimplePayload.class);
		assertEquals(payload, clonePayload);
	}

	@Test
	public void unserializeWithNullPayload() {
		String topicUri = rndStr(); 
		SimplePayload payload = null;
		EventMessage callMessage = new EventMessage(topicUri);
		callMessage.setPayload(payload);
		String json = MessageMapper.toJson(callMessage);
		assertEquals("[8," + q(topicUri) + ",null]", json);

		EventMessage clone = (EventMessage) MessageMapper.fromJson(json);
		assertEquals(MessageType.EVENT, clone.getType());
		assertEquals(topicUri, clone.topicUri);
		SimplePayload clonePayload = clone.getPayload(SimplePayload.class);
		assertNull(clonePayload);
	}

	@Test
	public void unserializeInvalidMessages() {
		MessagesTest.verifyFailingParsing(EventMessage.class, new String[] {
			"?",
			"[8]",
			"[8,\"ID\"]",
		});
	}

}
