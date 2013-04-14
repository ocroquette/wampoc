package fr.ocroquette.wampoc.messages;

import java.lang.reflect.Type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;


public class CallMessage extends Message {
	public String callId;
	public String procedureId;
	public JsonElement payload;

	public CallMessage() {
		super(MessageType.CALL);
	}

	public CallMessage(String callId, String procedureId) {
		super(MessageType.CALL);
		this.callId = callId;
		this.procedureId = procedureId;
	}

	public static class Serializer implements JsonSerializer<CallMessage> {
		@Override
		public JsonElement serialize(CallMessage msg, Type arg1,
				JsonSerializationContext context) {
			JsonArray array = new JsonArray();
			array.add(context.serialize(msg.getType().getCode()));
			array.add(context.serialize(msg.callId));
			array.add(context.serialize(msg.procedureId));
			if ( msg.payload != null )
				array.add(msg.payload);
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
				msg.payload = array.get(3);
			return msg;
		}
	}

	public <PayloadType> PayloadType getPayload(Class<PayloadType> type) {
		if ( payload == null )
			return null;
		Gson gson = new Gson();
		try {
			return gson.fromJson(payload, type);
		}
		catch(JsonSyntaxException e) {
			return null;
		}
	}

	public void setPayload(Object payload) {
		Gson gson = new Gson();
		this.payload = gson.toJsonTree(payload);
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
