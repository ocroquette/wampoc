package fr.ocroquette.wampoc.adapters.jetty;

import org.eclipse.jetty.websocket.WebSocket;

import fr.ocroquette.wampoc.client.WampClient;

public class JettyClientAdapter implements WebSocket.OnTextMessage {
	@Override
	public void onOpen(Connection connection) {
		client = new WampClient(new ChannelToConnectionAdapter(connection));
		System.out.println("JettyClientAdapter:onOpen " + client);
	}

	@Override
	public void onClose(int closeCode, String message) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMessage(String data) {
		client.handleIncomingMessage(data);
	}
	
	public WampClient getWampClient() {
		return client;
	}

	private WampClient client;

}

