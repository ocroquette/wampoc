package fr.ocroquette.wampoc.adapters.jetty;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

import fr.ocroquette.wampoc.server.Server;

public class WampocJettyWebSocketHandler extends WebSocketHandler {
	
	public WampocJettyWebSocketHandler() {
		this.wampocServer = new Server();
	}

	public WampocJettyWebSocketHandler(Server wampocServer) {
		this.wampocServer = wampocServer;
	}
	
	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest request,
			String protocol) {
		return new WampocJettyWebSocket(wampocServer);
	}

	protected Server wampocServer;
}
