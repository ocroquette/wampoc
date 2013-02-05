package fr.ocroquette.wampoc.adapters.jetty;

import java.io.IOException;

import org.eclipse.jetty.websocket.WebSocket.OnTextMessage;

import fr.ocroquette.wampoc.server.SessionId;
import fr.ocroquette.wampoc.server.WampServer;

/***
 * Proxy between a specific WebSocket session on the server side and the internal WampServer.
 *
 * Typically you want to use this as a template to create your own class, and keep track
 * of sessions with onOpen and onClose.
 */
public class JettyServerWebSocketProxy implements OnTextMessage {
	public JettyServerWebSocketProxy(WampServer wampServer) {
		this.wampServer = wampServer; 
	}

	@Override
	public void onOpen(Connection connection) {
		sessionId = wampServer.addClient(new ChannelToConnectionAdapter(connection));
		System.out.println("JettyServer: Got connection: " + sessionId);
	}

	@Override
	public void onClose(int closeCode, String message) {
		System.out.println("JettyServer: Lost connection: " + sessionId);
	}

	@Override
	public void onMessage(String data) {
		try {
			wampServer.handleIncomingMessage(sessionId, data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private WampServer wampServer;
	private SessionId sessionId;
}	
