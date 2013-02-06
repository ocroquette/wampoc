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


public class CallErrorMessage extends Message {
	// [ TYPE_ID_CALLERROR , callID , errorURI , errorDesc , errorDetails ]
	public String callId;
	public String errorUri;
	public String errorDesc;
	public JsonElement errorDetails;

	private CallErrorMessage() {
		super(MessageType.CALLERROR);
	}

	CallErrorMessage(String callId, String errorUri, String errorDesc) {
		super(MessageType.CALLERROR);
		this.callId = callId;
		this.errorUri = errorUri;
		this.errorDesc = errorDesc;
	}

	public CallErrorMessage(String callId, String errorUri, String errorDesc, JsonElement errorDetails) {
		super(MessageType.CALLERROR);
		this.callId = callId;
		this.errorUri = errorUri;
		this.errorDesc = errorDesc;
		this.errorDetails = errorDetails;
	}

	public static class Serializer implements JsonSerializer<CallErrorMessage> {
		@Override
		public JsonElement serialize(CallErrorMessage msg, Type arg1,
				JsonSerializationContext context) {
			JsonArray array = new JsonArray();
			array.add(context.serialize(msg.getType().getCode()));
			array.add(context.serialize(msg.callId));
			array.add(context.serialize(msg.errorUri));
			array.add(context.serialize(msg.errorDesc));
			if ( msg.errorDetails != null)
				array.add(msg.errorDetails);
			return array;
		}
	}

	public static class Deserializer implements JsonDeserializer<CallErrorMessage> {
		@Override
		public CallErrorMessage deserialize(JsonElement element, Type arg1,
				JsonDeserializationContext context) throws JsonParseException {

			JsonArray array = element.getAsJsonArray();

			if ( MessageType.fromInteger(array.get(0).getAsInt()) != MessageType.CALLERROR)
				return null;

			CallErrorMessage msg = new CallErrorMessage();
			msg.callId = array.get(1).getAsString();
			msg.errorUri = array.get(2).getAsString();
			msg.errorDesc = array.get(3).getAsString();
			if ( array.size() >= 5 )
				msg.errorDetails = array.get(4);
			return msg;
		}
	}

	public <ErrorDetailsType> ErrorDetailsType getErrorDetails(Class<ErrorDetailsType> type) {
		if ( errorDetails == null )
			throw new NullPointerException("No payload has been set");
		Gson gson = new Gson();
		return gson.fromJson(errorDetails, type);
	}
	
	public void setErrorDetails(Object errorDetails) {
		Gson gson = new Gson();
		this.errorDetails = gson.toJsonTree(errorDetails);
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
