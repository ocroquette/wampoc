package fr.ocroquette.wampoc.payload;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class PayloadTest {
	@Test
	public void testConstructor() {
		String string = UUID.randomUUID().toString();
		JsonElement element = new JsonPrimitive(string);
		GsonPayload payload = new GsonPayload(element);
		assertEquals(element, payload.getGsonElement());
	}
	
	@Test
	public void testNull() {
		GsonPayload payload = new GsonPayload();
		assertEquals(null, payload.get(String.class));
	}

	@Test
	public void testEquals() {
		String string = UUID.randomUUID().toString();
		
		GsonPayload payload1 = new GsonPayload();
		payload1.setFromObject(string);
		
		GsonPayload payload2 = new GsonPayload();
		payload2.setFromObject(string);
		
		assertTrue(payload1.equals(payload2));
	}
	
	@Test
	public void testSetAndGet() {
		GsonPayload payload = new GsonPayload();
		String string = UUID.randomUUID().toString();
		payload.setFromGson(new JsonPrimitive(string));
		assertEquals(string, payload.get(String.class));
	}
}
