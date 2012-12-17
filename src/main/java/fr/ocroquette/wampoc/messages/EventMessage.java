package fr.ocroquette.wampoc.messages;

import java.lang.reflect.Type;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EventMessage extends Message {
	static final MessageType concreteMessageType = MessageType.EVENT;

	public String topicUri;
	public JsonElement payload;

	public EventMessage() {
		super(concreteMessageType);
	}

	public EventMessage(String topicUri) {
		super(concreteMessageType);
		this.topicUri = topicUri;
	}

	public static class Serializer implements JsonSerializer<EventMessage> {
		@Override
		public JsonElement serialize(EventMessage msg, Type arg1,
				JsonSerializationContext context) {
			JsonArray array = new JsonArray();
			array.add(context.serialize(msg.getType().getCode()));
			array.add(context.serialize(msg.topicUri));
			array.add(msg.payload);
			return array;
		}
	}

	public static class Deserializer implements JsonDeserializer<EventMessage> {
		@Override
		public EventMessage deserialize(JsonElement element, Type arg1,
				JsonDeserializationContext context) throws JsonParseException {

			JsonArray array = element.getAsJsonArray();

			if ( MessageType.fromInteger(array.get(0).getAsInt()) != concreteMessageType)
				return null;

			EventMessage msg = new EventMessage();
			msg.topicUri = array.get(1).getAsString();
			msg.payload = array.get(2);
			return msg;
		}
	}

	public <PayloadType> PayloadType getPayload(Class<PayloadType> type) {
		Gson gson = new Gson();
		return gson.fromJson(payload, type);
	}

	public <PayloadType> void setPayload(PayloadType payload, Class<PayloadType> type) {
		Gson gson = new Gson();
		this.payload = gson.toJsonTree(payload);
	}

	public void setPayload(JsonElement payload) {
		this.payload = payload;
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
