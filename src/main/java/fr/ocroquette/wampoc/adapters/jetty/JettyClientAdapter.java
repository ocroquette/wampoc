package fr.ocroquette.wampoc.adapters.jetty;

import org.eclipse.jetty.websocket.WebSocket;

import fr.ocroquette.wampoc.client.WampClient;

public class JettyClientAdapter implements WebSocket.OnTextMessage {
	@Override
	public void onOpen(Connection connection) {
		client = new WampClient(new ChannelToConnectionAdapter(connection));
		System.out.println("JettyClientAdapter:onOpen " + client);
		// Jetty doesn't count our traffic as such, and therefore always close
		// the connection after this time. As a workaround, we use a huge value
		connection.setMaxIdleTime(7*24*3600*1000);
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
