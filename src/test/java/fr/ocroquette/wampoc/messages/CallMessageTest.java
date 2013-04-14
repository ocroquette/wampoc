package fr.ocroquette.wampoc.messages;

import static fr.ocroquette.wampoc.testutils.Utils.q;
import static fr.ocroquette.wampoc.testutils.Utils.rndStr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;



public class CallMessageTest implements TestsNotToForget {
	@Test
	public void constructMessage() {
		String callId = rndStr(); 
		String procedureId = rndStr(); 
		CallMessage callMessage = new CallMessage(callId, procedureId);
		assertEquals(callId, callMessage.callId);
		assertEquals(procedureId, callMessage.procedureId);
	}

	public CallMessage randomCallMessage() {
		return new CallMessage(rndStr(), rndStr());
	}

	@Test
	public void testEquals() {
		CallMessage msg1 = new CallMessage("1","2"); 
		CallMessage msg1bis = new CallMessage("1","2"); 
		CallMessage msg2 = new CallMessage("1","3"); 
		CallMessage msg3 = new CallMessage("2","2"); 
		assertTrue(msg1.equals(msg1bis));
		assertFalse(msg1.equals(msg2));
		assertFalse(msg1.equals(msg3));

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
	public void unserializeWithNoPayload() {
		CallMessage msg = randomCallMessage();

		String json = MessageMapper.toJson(msg);
		assertEquals("[2," + q(msg.callId) + ","+q(msg.procedureId)+"]", json);

		CallMessage clone = (CallMessage) MessageMapper.fromJson(json);
		assertEquals(msg, clone);

		Object payload = clone.getPayload(SimplePayload.class);
		assertNull("getPayload shall return null if there is no payload", payload);
	}

	@Test
	public void unserializeWithPrimitivePayload() {
		CallMessage msg = randomCallMessage();
		Integer payload = new Random().nextInt(); 
		msg.setPayload(payload);
		String json = MessageMapper.toJson(msg);
		assertEquals("[2," + q(msg.callId) + ","+q(msg.procedureId)+","+payload+"]", json);

		CallMessage clone = (CallMessage) MessageMapper.fromJson(json);
		assertEquals(msg, clone);

		try {
			SimplePayload p = clone.getPayload(SimplePayload.class);
			assertEquals("getPayload shall return null in case of wrong type", null, p);
		}
		catch(Exception e) {
		}
	}

	@Test
	public void unserializeWithComplexPayload() {
		CallMessage msg = randomCallMessage();
		SimplePayload payload = new SimplePayload(rndStr(), new Random().nextInt() );
		msg.setPayload(payload);
		String json = MessageMapper.toJson(msg);
		assertEquals("[2," + q(msg.callId) + ","+q(msg.procedureId)+",{\"s\":"+q(payload.s)+",\"i\":"+payload.i+"}]", json);

		CallMessage clone = (CallMessage) MessageMapper.fromJson(json);
		assertEquals(msg, clone);
	}

	@Test
	public void unserializeWithNullPayload() {
		CallMessage msg = randomCallMessage();
		SimplePayload payload = null;
		msg.setPayload(payload);
		String json = MessageMapper.toJson(msg);
		assertEquals("[2," + q(msg.callId) + ","+q(msg.procedureId)+",null]", json);

		CallMessage clone = (CallMessage) MessageMapper.fromJson(json);
		assertEquals(msg, clone);
		SimplePayload clonePayload = clone.getPayload(SimplePayload.class);
		assertNull(clonePayload);
	}

	@Test
	public void unserializeInvalidMessages() {
		MessagesTest.verifyFailingParsing(CallMessage.class, new String[] {
			"?",
			"[2]",
			"[2,\"ID\"]",
		});
	}

}
