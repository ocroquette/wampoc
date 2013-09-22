package fr.ocroquette.wampoc.client;

import com.google.gson.JsonElement;

import fr.ocroquette.wampoc.payload.GsonPayload;

public abstract class EventReceiver {

	abstract void onReceive();
	
	public <PayloadType> PayloadType getPayload(Class<PayloadType> payloadType) {
		return payload.get(payloadType); 
	}
	
	void setPayloadElement(JsonElement jsonElement) {
		payload.setFromGson(jsonElement);
	}
	
	private GsonPayload payload = new GsonPayload();
}
