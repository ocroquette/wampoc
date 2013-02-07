package fr.ocroquette.wampoc.server;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class Subscriptions {

	public interface ActionOnSubscriber {
		void execute(String clientId);
	}
	
	public Subscriptions() {
		subscriptions = new ConcurrentHashMap<String, Set<String>>();
	}

	public void subscribe(String clientId, String topicUri) {
		Set<String> clientList = subscriptions.get(topicUri);
		if ( clientList == null ) {
			clientList = newClientSet();
			subscriptions.put(topicUri, clientList);
		}
		clientList.add(clientId);
	}

	public void unsubscribe(String clientId, String topicUri) {
		Set<String> clientList = subscriptions.get(topicUri);
		if ( clientList == null ) {
			return;
		}
		clientList.remove(clientId);
	}
	
	public long forAllSubscribers(String topic, ActionOnSubscriber action) {
		long performed = 0;
		Set<String> set = subscriptions.get(topic);
		if (set == null)
			return performed;
		for (String clientId: set) {
			action.execute(clientId);
			performed++;
		}
		return performed;
	}


	private Set<String> newClientSet() {
		return Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	}
	
	protected Map<String, Set<String>> subscriptions;
}
