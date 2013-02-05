package fr.ocroquette.wampoc.adapters.jetty;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

import fr.ocroquette.wampoc.client.WampClient;

public class JettyClient {

	public void connect(URI uri, String protocol) throws Exception {

		WebSocketClientFactory factory = new WebSocketClientFactory();
		try {
			factory.start();
		} catch (Exception e) {
			throw new Exception("Failed to start the WebSocketClientFactory");
		}

		WebSocketClient client = factory.newWebSocketClient();
		client.setProtocol(protocol);

		jettyClientAdapter = new JettyClientAdapter();
		connection = client.open(uri, jettyClientAdapter).get(5, TimeUnit.SECONDS);
	}

	public WampClient getWampClient() {
		return jettyClientAdapter.getWampClient();
	}

	private JettyClientAdapter jettyClientAdapter;
	private WebSocket.Connection connection;
}
