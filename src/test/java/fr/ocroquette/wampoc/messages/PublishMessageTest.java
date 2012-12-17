package fr.ocroquette.wampoc.messages;

import static fr.ocroquette.wampoc.testutils.Utils.q;
import static fr.ocroquette.wampoc.testutils.Utils.rndStr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class PublishMessageTest implements TestsNotToForget {
	@Test
	public void constructMessage() {
		String topicUri = rndStr(); 
		PublishMessage msg = new PublishMessage(topicUri);
		assertEquals(topicUri, msg.topicUri);
	}

	public PublishMessage randomMessage() {
		return new PublishMessage(rndStr());
	}

	@Test
	public void testEquals() {
		PublishMessage msg1 = new PublishMessage("1"); 
		PublishMessage msg1bis = new PublishMessage("1"); 
		PublishMessage msg2 = new PublishMessage("2"); 
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
	public void unserializeWithNoPayload() {
		String topicUri = rndStr(); 
		PublishMessage msg = new PublishMessage(topicUri);

		String json = MessageMapper.toJson(msg);
		assertEquals("[7," + q(topicUri) + ",null]", json);

		PublishMessage clone = (PublishMessage) MessageMapper.fromJson(json);
		assertEquals(MessageType.PUBLISH, clone.getType());
		assertEquals(topicUri, clone.topicUri);

		SimplePayload payload = clone.getPayload(SimplePayload.class);
		assertNull("getPayload shall return null if no payload is available", payload);
	}

	@Test
	public void unserializeWithNullPayload() {
		String topicUri = rndStr(); 
		PublishMessage msg = new PublishMessage(topicUri);

		String json = MessageMapper.toJson(msg);
		msg.setPayload(null);
		assertEquals("[7," + q(topicUri) + ",null]", json);
	}

	@Test
	public void unserializeWithPrimitivePayload() {
		String topicUri = rndStr(); 
		PublishMessage msg = new PublishMessage(topicUri);
		int payload = new Random().nextInt();
		msg.setPayload(new Integer(payload));

		String json = MessageMapper.toJson(msg);
		assertEquals("[7," + q(topicUri) + ","+ payload + "]", json);

		PublishMessage clone = (PublishMessage) MessageMapper.fromJson(json);
		assertEquals(msg, clone);

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
		PublishMessage msg = new PublishMessage(topicUri);
		SimplePayload payload = new SimplePayload(rndStr(), new Random().nextInt()); 
		msg.setPayload(payload);

		String json = MessageMapper.toJson(msg);
		assertEquals("[7," + q(topicUri) + "," + "{\"s\":"+q(payload.s)+",\"i\":"+payload.i+"}]", json);

		PublishMessage clone = (PublishMessage) MessageMapper.fromJson(json);
		assertEquals(msg, clone);

		SimplePayload clonePayload = clone.getPayload(SimplePayload.class);
		assertEquals(payload, clonePayload);
	}

	@Test
	public void unserializeExcludeMe() {
		String topicUri = rndStr(); 
		PublishMessage msg = new PublishMessage(topicUri);
		msg.excludeMe = true;

		String json = MessageMapper.toJson(msg);
		assertEquals("[7," + q(topicUri) + ",null,true]", json);

		PublishMessage clone = (PublishMessage) MessageMapper.fromJson(json);
		assertEquals(msg.excludeMe, clone.excludeMe);
		assertEquals(msg, clone);
	}


	@Test
	public void unserializeInvalidMessages() {
		MessagesTest.verifyFailingParsing(PublishMessage.class, new String[] {
			"[7]",
			"[7,\"ID\"]",
			// the following items should work but functionality is not implemented yet:
			// "[7,\"ID\",null,true]", 
			"[7,\"ID\",null,[\"NwtXQ8rdfPsy-ewS\", \"dYqgDl0FthI6_hjb\"]]", 
			"[7,\"ID\",null,[\"NwtXQ8rdfPsy-ewS\", \"dYqgDl0FthI6_hjb\"], [\"NwtXQ8rdfPsy-ewS\"]]", 
		});
	}

}
