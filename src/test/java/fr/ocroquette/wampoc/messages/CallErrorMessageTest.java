package fr.ocroquette.wampoc.messages;

import static fr.ocroquette.wampoc.testutils.Utils.q;
import static fr.ocroquette.wampoc.testutils.Utils.rndStr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class CallErrorMessageTest implements TestsNotToForget {
	@Test
	public void constructMessage() {
		String callId = rndStr(); 
		String errorUri = rndStr(); 
		String errorDesc = rndStr(); 
		CallErrorMessage callMessage = new CallErrorMessage(callId,errorUri, errorDesc);
		assertEquals(callId, callMessage.callId);
		assertEquals(errorUri, callMessage.errorUri);
		assertEquals(errorDesc, callMessage.errorDesc);
	}
	
	public CallErrorMessage randomMessage() {
		return new CallErrorMessage(rndStr(),rndStr(),rndStr());
	}

	@Test
	public void testEquals() {
		CallErrorMessage msg1 = new CallErrorMessage("1","2","3"); 
		CallErrorMessage msg1bis = new CallErrorMessage("1","2","3"); 
		CallErrorMessage msg2 = new CallErrorMessage("1","2","4"); 
		assertTrue(msg1.equals(msg1bis));
		assertFalse(msg1.equals(msg2));
		
		SimplePayload payload1 = new SimplePayload("String", 1); 
		SimplePayload payload1bis = new SimplePayload("String", 1); 
		SimplePayload payload2 = new SimplePayload("String", 2); 
		msg1.setErrorDetails(payload1);
		assertFalse(msg1.equals(msg1bis));
		msg1bis.setErrorDetails(payload1bis);
		assertTrue(msg1.equals(msg1bis));
		msg1bis.setErrorDetails(payload2);
		assertFalse(msg1.equals(msg1bis));
	}

	@Test
	public void unserializeWithNoErrorDetails() {
		String callId = rndStr(); 
		String errorUri = rndStr(); 
		String errorDesc = rndStr(); 
		CallErrorMessage callMessage = new CallErrorMessage(callId, errorUri, errorDesc);
		String json = MessageMapper.toJson(callMessage);
		assertEquals("[4," + q(callId) + ","+ q(errorUri) + ","+ q(errorDesc) + "]", json);

		CallErrorMessage clone = (CallErrorMessage) MessageMapper.fromJson(json);
		assertEquals(MessageType.CALLERROR, clone.getType());
		assertEquals(callId, clone.callId);
		assertEquals(errorUri, clone.errorUri);
		assertEquals(errorDesc, clone.errorDesc);

		try {
			clone.getErrorDetails(SimplePayload.class);
			assertTrue("getPayload shall throw an exception in case of wrong type", false);
		}
		catch(Exception e) {
		}
	}

	@Test
	public void unserializeWithPrimitiveErrorDetails() {
		String callId = rndStr(); 
		String errorUri = rndStr(); 
		String errorDesc = rndStr(); 
		int errorDetails = new Random().nextInt(); 
		CallErrorMessage callMessage = new CallErrorMessage(callId, errorUri, errorDesc);
		callMessage.setErrorDetails(new Integer(errorDetails));
		String json = MessageMapper.toJson(callMessage);
		assertEquals("[4," + q(callId) + ","+ q(errorUri) + ","+ q(errorDesc) + ","+ errorDetails + "]", json);

		CallErrorMessage clone = (CallErrorMessage) MessageMapper.fromJson(json);
		assertEquals(MessageType.CALLERROR, clone.getType());
		assertEquals(callId, clone.callId);
		assertEquals(errorUri, clone.errorUri);
		assertEquals(errorDesc, clone.errorDesc);
		int cloneerrorDetails = clone.getErrorDetails(Integer.class);
		assertEquals(errorDetails, cloneerrorDetails);

		try {
			clone.getErrorDetails(SimplePayload.class);
			assertTrue("getPayload shall throw an exception in case of wrong type", false);
		}
		catch(Exception e) {
		}
	}

	@Test
	public void unserializeWithComplexPayload() {
		String callId = rndStr(); 
		String errorUri = rndStr(); 
		String errorDesc = rndStr(); 
		SimplePayload errorDetails = new SimplePayload(rndStr(), new Random().nextInt()); 
		CallErrorMessage callMessage = new CallErrorMessage(callId, errorUri, errorDesc);
		callMessage.setErrorDetails(errorDetails);
		String json = MessageMapper.toJson(callMessage);
		assertEquals("[4," + q(callId) + ","+ q(errorUri) + ","+ q(errorDesc) + ","+ "{\"s\":"+q(errorDetails.s)+",\"i\":"+errorDetails.i+"}]", json);

		CallErrorMessage clone = (CallErrorMessage) MessageMapper.fromJson(json);
		assertEquals(MessageType.CALLERROR, clone.getType());
		assertEquals(callId, clone.callId);
		assertEquals(errorUri, clone.errorUri);
		assertEquals(errorDesc, clone.errorDesc);
		SimplePayload cloneErrorDetails = clone.getErrorDetails(SimplePayload.class);
		assertEquals(errorDetails, cloneErrorDetails);
	}

	@Test
	public void unserializeWithNullPayload() {
		String callId = rndStr(); 
		String errorUri = rndStr(); 
		String errorDesc = rndStr(); 
		CallErrorMessage callMessage = new CallErrorMessage(callId, errorUri, errorDesc);
		callMessage.setErrorDetails(null);
		String json = MessageMapper.toJson(callMessage);
		assertEquals("[4," + q(callId) + ","+ q(errorUri) + ","+ q(errorDesc) + ",null]", json);

		CallErrorMessage clone = (CallErrorMessage) MessageMapper.fromJson(json);
		assertEquals(MessageType.CALLERROR, clone.getType());
		assertEquals(callId, clone.callId);
		assertEquals(errorUri, clone.errorUri);
		assertEquals(errorDesc, clone.errorDesc);
		SimplePayload clonePayload = clone.getErrorDetails(SimplePayload.class);
		assertNull(clonePayload);
	}

	@Test
	public void unserializeInvalidMessages() {
		MessagesTest.verifyFailingParsing(CallErrorMessage.class, new String[] {
			"[4]",
			"[4,\"ID\"]",
			"[4,\"CALLID\", \"URI\"]",
		});
	}

}
