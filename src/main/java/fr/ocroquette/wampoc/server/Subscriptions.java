package fr.ocroquette.wampoc.server;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class Subscriptions {

	public interface ActionOnSubscriber {
		void execute(SessionId clientId);
	}
	
	public Subscriptions() {
		subscriptions = new ConcurrentHashMap<String, Set<SessionId>>();
	}

	public void subscribe(SessionId clientId, String topicUri) {
		Set<SessionId> clientList = subscriptions.get(topicUri);
		if ( clientList == null ) {
			clientList = newClientSet();
			subscriptions.put(topicUri, clientList);
		}
		clientList.add(clientId);
	}

	public void unsubscribe(SessionId clientId, String topicUri) {
		Set<SessionId> clientList = subscriptions.get(topicUri);
		if ( clientList == null ) {
			return;
		}
		clientList.remove(clientId);
	}
	
	public long forAllSubscribers(String topic, ActionOnSubscriber action) {
		long performed = 0;
		Set<SessionId> set = subscriptions.get(topic);
		if (set == null)
			return performed;
		for (SessionId clientId: set) {
			action.execute(clientId);
			performed++;
		}
		return performed;
	}


	private Set<SessionId> newClientSet() {
		return Collections.newSetFromMap(new ConcurrentHashMap<SessionId, Boolean>());
	}
	
	protected Map<String, Set<SessionId>> subscriptions;
}
