package fr.ocroquette.wampoc.messages;

import static fr.ocroquette.wampoc.testutils.Utils.q;
import static fr.ocroquette.wampoc.testutils.Utils.rndStr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class CallResultMessageTest implements TestsNotToForget {
	@Test
	public void constructMessage() {
		String callId = rndStr(); 
		CallResultMessage callMessage = new CallResultMessage(callId);
		assertEquals(callId, callMessage.callId);
	}
	
	public CallResultMessage randomMessage() {
		return new CallResultMessage(rndStr());
	}

	@Test
	public void testEquals() {
		CallResultMessage msg1 = new CallResultMessage("1"); 
		CallResultMessage msg1bis = new CallResultMessage("1"); 
		CallResultMessage msg2 = new CallResultMessage("2"); 
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
		String callId = rndStr(); 
		int payload = new Random().nextInt(); 
		CallResultMessage callMessage = new CallResultMessage(callId);
		callMessage.setPayload(payload);
		String json = MessageMapper.toJson(callMessage);
		assertEquals("[3," + q(callId) + ","+payload+"]", json);

		CallResultMessage clone = (CallResultMessage) MessageMapper.fromJson(json);
		assertEquals(MessageType.CALLRESULT, clone.getType());
		assertEquals(callId, clone.callId);
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
		String callId = rndStr(); 
		SimplePayload payload = new SimplePayload(rndStr(), new Random().nextInt() );
		CallResultMessage callMessage = new CallResultMessage(callId);
		callMessage.setPayload(payload);
		String json = MessageMapper.toJson(callMessage);
		assertEquals("[3," + q(callId) + ",{\"s\":"+q(payload.s)+",\"i\":"+payload.i+"}]", json);

		CallResultMessage clone = (CallResultMessage) MessageMapper.fromJson(json);
		assertEquals(MessageType.CALLRESULT, clone.getType());
		assertEquals(callId, clone.callId);
		SimplePayload clonePayload = clone.getPayload(SimplePayload.class);
		assertEquals(payload, clonePayload);
	}

	@Test
	public void unserializeWithNullPayload() {
		String callId = rndStr(); 
		SimplePayload payload = null;
		CallResultMessage callMessage = new CallResultMessage(callId);
		callMessage.setPayload(payload);
		String json = MessageMapper.toJson(callMessage);
		assertEquals("[3," + q(callId) + ",null]", json);

		CallResultMessage clone = (CallResultMessage) MessageMapper.fromJson(json);
		assertEquals(MessageType.CALLRESULT, clone.getType());
		assertEquals(callId, clone.callId);
		SimplePayload clonePayload = clone.getPayload(SimplePayload.class);
		assertNull(clonePayload);
	}

	@Test
	public void unserializeInvalidMessages() {
		MessagesTest.verifyFailingParsing(CallResultMessage.class, new String[] {
			"?",
			"[3]",
			"[3,\"ID\"]",
		});
	}

}
