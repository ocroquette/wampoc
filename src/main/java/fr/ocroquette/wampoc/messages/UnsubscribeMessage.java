package fr.ocroquette.wampoc.messages;

import java.lang.reflect.Type;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class UnsubscribeMessage extends Message {
	static final MessageType concreteMessageType = MessageType.UNSUBSCRIBE;
	
	public String topicUri;
	
	public UnsubscribeMessage() {
		super(concreteMessageType);
	}

	public UnsubscribeMessage(String topicUri) {
		super(concreteMessageType);
		this.topicUri = topicUri;
	}
	public static class Serializer implements JsonSerializer<UnsubscribeMessage> {
		@Override
		public JsonElement serialize(UnsubscribeMessage msg, Type arg1,
				JsonSerializationContext context) {
			JsonArray array = new JsonArray();
			array.add(context.serialize(msg.getType().getCode()));
			array.add(context.serialize(msg.topicUri));
			return array;
		}
	}

	public static class Deserializer implements JsonDeserializer<UnsubscribeMessage> {
		@Override
		public UnsubscribeMessage deserialize(JsonElement element, Type arg1,
				JsonDeserializationContext context) throws JsonParseException {

			JsonArray array = element.getAsJsonArray();
			
			if ( MessageType.fromInteger(array.get(0).getAsInt()) != concreteMessageType)
				return null;
			
			UnsubscribeMessage msg = new UnsubscribeMessage();
			msg.topicUri = array.get(1).getAsString();
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
