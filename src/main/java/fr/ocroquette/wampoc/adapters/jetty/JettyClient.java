package fr.ocroquette.wampoc.adapters.jetty;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

import fr.ocroquette.wampoc.client.WampClient;

public class JettyClient {

	public void connect(URI uri, String protocol) throws IOException {

		WebSocketClientFactory factory = new WebSocketClientFactory();
		try {
			factory.start();
		} catch (Exception e) {
			throw new IOException("Failed to start the WebSocketClientFactory");
		}

		WebSocketClient client = factory.newWebSocketClient();
		client.setProtocol(protocol);

		jettyClientAdapter = new JettyClientAdapter();
		try {
			connection = client.open(uri, jettyClientAdapter).get(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new IOException("Failed to open the URI: " + uri, e);
		}
	}

	public WampClient getWampClient() {
		return jettyClientAdapter.getWampClient();
	}

	private JettyClientAdapter jettyClientAdapter;
	@SuppressWarnings("unused")
	private WebSocket.Connection connection;
}
