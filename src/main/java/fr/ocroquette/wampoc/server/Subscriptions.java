package fr.ocroquette.wampoc.server;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class Subscriptions {

	public interface ActionOnSubscriber {
		void execute(ClientId clientId);
	}
	
	public Subscriptions() {
		subscriptions = new ConcurrentHashMap<String, Set<ClientId>>();
	}

	public void subscribe(ClientId clientId, String topicUri) {
		Set<ClientId> clientList = subscriptions.get(topicUri);
		if ( clientList == null ) {
			clientList = newClientSet();
			subscriptions.put(topicUri, clientList);
		}
		clientList.add(clientId);
	}

	public void unsubscribe(ClientId clientId, String topicUri) {
		Set<ClientId> clientList = subscriptions.get(topicUri);
		if ( clientList == null ) {
			return;
		}
		clientList.remove(clientId);
	}
	
	public long forAllSubscribers(String topic, ActionOnSubscriber action) {
		long performed = 0;
		Set<ClientId> set = subscriptions.get(topic);
		if (set == null)
			return performed;
		for (ClientId clientId: set) {
			action.execute(clientId);
			performed++;
		}
		return performed;
	}


	private Set<ClientId> newClientSet() {
		return Collections.newSetFromMap(new ConcurrentHashMap<ClientId, Boolean>());
	}
	
	protected Map<String, Set<ClientId>> subscriptions;
}
