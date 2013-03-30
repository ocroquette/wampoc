package fr.ocroquette.wampoc.messages;

import java.lang.reflect.Type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PublishMessage extends Message {
	static final MessageType concreteMessageType = MessageType.PUBLISH;

	public String topicUri;
	public JsonElement payload;
	public Boolean excludeMe;

	public PublishMessage() {
		super(concreteMessageType);
	}

	public PublishMessage(String topicUri) {
		super(concreteMessageType);
		this.topicUri = topicUri;
	}

	public static class Serializer implements JsonSerializer<PublishMessage> {
		@Override
		public JsonElement serialize(PublishMessage msg, Type arg1,
				JsonSerializationContext context) {
			JsonArray array = new JsonArray();
			array.add(context.serialize(msg.getType().getCode()));
			array.add(context.serialize(msg.topicUri));
			array.add(msg.payload);
			if ( msg.excludeMe != null)
				array.add(context.serialize(msg.excludeMe.booleanValue()));
			return array;
		}
	}

	public static class Deserializer implements JsonDeserializer<PublishMessage> {
		@Override
		public PublishMessage deserialize(JsonElement element, Type arg1,
				JsonDeserializationContext context) throws JsonParseException {

			JsonArray array = element.getAsJsonArray();

			if ( MessageType.fromInteger(array.get(0).getAsInt()) != concreteMessageType)
				return null;

			PublishMessage msg = new PublishMessage();
			msg.topicUri = array.get(1).getAsString();
			if ( ! ( array.get(2) instanceof JsonNull ) )
				msg.payload = array.get(2);
			if ( array.size() == 4 ) {
				if ( array.get(3) instanceof JsonPrimitive)
					msg.excludeMe = array.get(3).getAsBoolean();
				else
					return null; // exclude and eligible are not supported yet
			}
			else if ( array.size() == 5 )
				return null; // exclude and eligible are not supported yet
			return msg;
		}
	}

	public <PayloadType> PayloadType getPayload(Class<PayloadType> type) {
		Gson gson = new Gson();
		return gson.fromJson(payload, type);
	}

	public void setPayload(Object payload) {
		Gson gson = new Gson();
		this.payload = gson.toJsonTree(payload);
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
