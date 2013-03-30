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

public class SubscribeMessage extends Message {
	static final MessageType concreteMessageType = MessageType.SUBSCRIBE;
	
	public String topicUri;
	public Boolean excludeMe;
	
	public SubscribeMessage() {
		super(concreteMessageType);
	}

	public SubscribeMessage(String topicUri) {
		super(concreteMessageType);
		this.topicUri = topicUri;
	}
	public static class Serializer implements JsonSerializer<SubscribeMessage> {
		@Override
		public JsonElement serialize(SubscribeMessage msg, Type arg1,
				JsonSerializationContext context) {
			JsonArray array = new JsonArray();
			array.add(context.serialize(msg.getType().getCode()));
			array.add(context.serialize(msg.topicUri));
			if ( msg.excludeMe != null )
				array.add(context.serialize(msg.excludeMe));
			return array;
		}
	}

	public static class Deserializer implements JsonDeserializer<SubscribeMessage> {
		@Override
		public SubscribeMessage deserialize(JsonElement element, Type arg1,
				JsonDeserializationContext context) throws JsonParseException {

			JsonArray array = element.getAsJsonArray();
			
			if ( MessageType.fromInteger(array.get(0).getAsInt()) != concreteMessageType)
				return null;
			
			SubscribeMessage msg = new SubscribeMessage();
			msg.topicUri = array.get(1).getAsString();
			if ( array.size() >= 3)
				msg.excludeMe = array.get(2).getAsBoolean();
			return msg;
		}
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
