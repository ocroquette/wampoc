package fr.ocroquette.wampoc.messages;

import java.lang.reflect.Type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import fr.ocroquette.wampoc.payload.GsonPayload;


public class CallMessage extends Message {
	public String callId;
	public String procedureId;
	public GsonPayload payload;

	public CallMessage() {
		super(MessageType.CALL);
		init();
	}

	public CallMessage(String callId, String procedureId) {
		super(MessageType.CALL);
		this.callId = callId;
		this.procedureId = procedureId;
		init();
	}

	private void init() {
		this.payload = new GsonPayload();
	}
	
	public static class Serializer implements JsonSerializer<CallMessage> {
		@Override
		public JsonElement serialize(CallMessage msg, Type arg1,
				JsonSerializationContext context) {
			JsonArray array = new JsonArray();
			array.add(context.serialize(msg.getType().getCode()));
			array.add(context.serialize(msg.callId));
			array.add(context.serialize(msg.procedureId));
			
			JsonElement payloadElement = msg.payload.getGsonElement(); 
			if ( payloadElement != null )
				array.add(payloadElement);
			return array;
		}
	}

	public static class Deserializer implements JsonDeserializer<CallMessage> {
		@Override
		public CallMessage deserialize(JsonElement element, Type arg1,
				JsonDeserializationContext context) throws JsonParseException {

			JsonArray array = element.getAsJsonArray();

			if ( MessageType.fromInteger(array.get(0).getAsInt()) != MessageType.CALL)
				return null;

			CallMessage msg = new CallMessage();
			msg.callId = array.get(1).getAsString();
			msg.procedureId = array.get(2).getAsString();
			if ( array.size() >=4 )
				msg.payload = new GsonPayload(array.get(3));
			return msg;
		}
	}

	public <PayloadType> PayloadType getPayload(Class<PayloadType> type) {
		return payload.get(type);
	}

	public void setPayload(Object payload) {
		this.payload.setFromObject(payload);
	}

	public boolean hasPayload() {
		return payload != null;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
