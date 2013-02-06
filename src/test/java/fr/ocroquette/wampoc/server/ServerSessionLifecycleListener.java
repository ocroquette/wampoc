package fr.ocroquette.wampoc.server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import fr.ocroquette.wampoc.testutils.ProtocollingChannel;

public class ServerSessionLifecycleListener {
	class MySessionLifeCycleListener implements SessionLifecycleListener {
		public List<SessionId> created = new ArrayList<SessionId>();
		public List<SessionId> discarded = new ArrayList<SessionId>();
		@Override
		public void onCreation(SessionId sessionId) {
			created.add(sessionId);
		}
		@Override
		public void onDiscard(SessionId sessionId) {
			discarded.add(sessionId);
		}
	}

	@Test
	public void test() throws IOException {
		String serverIdent = UUID.randomUUID().toString();
		WampServer server = new WampServer(serverIdent);
		ProtocollingChannel channel = new ProtocollingChannel();
		MySessionLifeCycleListener listener = new MySessionLifeCycleListener();
		server.addSessionLifecycleListener(listener);

		assertEquals(0, listener.created.size());
		assertEquals(0, listener.discarded.size());

		SessionId sessionId = server.connectClient(channel);

		assertEquals(1, listener.created.size());
		assertEquals(sessionId, listener.created.get(0));
		assertEquals(0, listener.discarded.size());
		
		server.discardClient(sessionId);

		assertEquals(1, listener.discarded.size());
		assertEquals(sessionId, listener.discarded.get(0));

	}

}
