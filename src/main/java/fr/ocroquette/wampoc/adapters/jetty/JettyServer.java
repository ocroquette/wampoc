package fr.ocroquette.wampoc.adapters.jetty;

import java.io.IOException;

import org.eclipse.jetty.websocket.WebSocket.OnTextMessage;

import fr.ocroquette.wampoc.server.ClientId;
import fr.ocroquette.wampoc.server.WampServer;

public class JettyServer implements OnTextMessage {
	public JettyServer(WampServer wampServer) {
		this.wampServer = wampServer; 
	}

	@Override
	public void onOpen(Connection connection) {
		clientId = wampServer.addClient(new ChannelToConnectionAdapter(connection));
	}

	@Override
	public void onClose(int closeCode, String message) {
	}

	public void onMessage(String data) {
		try {
			wampServer.handleIncomingMessage(clientId, data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private WampServer wampServer;
	private ClientId clientId;
}	
