package fr.ocroquette.wampoc.adapters.jetty;

import java.io.IOException;

import org.eclipse.jetty.websocket.WebSocket.OnTextMessage;

import fr.ocroquette.wampoc.common.Channel;
import fr.ocroquette.wampoc.server.ClientId;
import fr.ocroquette.wampoc.server.Server;

public class WampocJettyWebSocket implements OnTextMessage {
	private Server wampServer;
	private ClientId clientId;

	private static class ChannelToConnectionAdapter implements Channel {
		Connection connection;
		ChannelToConnectionAdapter(Connection connection) {
			this.connection = connection;
		}
		@Override
		public void handle(String message) throws IOException {
			connection.sendMessage(message);
		}

	}

	@Override
	public void onOpen(Connection connection) {
		System.out.println("ChatWebSocket.onOpen");
		clientId = wampServer.addClient(new ChannelToConnectionAdapter(connection));
	}

	@Override
	public void onClose(int closeCode, String message) {
		System.out.println("ChatWebSocket.onClose");
	}

	public WampocJettyWebSocket(Server wampServer) {
		this.wampServer = wampServer; 
	}

	public void onMessage(String data) {
		System.out.println("ChatWebSocket.onMessage" + data);
		try {
			wampServer.handleIncomingMessage(clientId, data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}	
