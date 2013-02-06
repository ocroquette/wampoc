package fr.ocroquette.wampoc.server;

public interface SessionLifecycleListener {
	void onCreation(SessionId sessionId);
	void onDiscard(SessionId sessionId);
}
