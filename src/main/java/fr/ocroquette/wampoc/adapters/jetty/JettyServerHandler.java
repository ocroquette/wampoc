package fr.ocroquette.wampoc.adapters.jetty;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

import fr.ocroquette.wampoc.server.WampServer;

public class JettyServerHandler extends WebSocketHandler {
	
	public JettyServerHandler() {
		this.wampocServer = new WampServer();
	}

	public JettyServerHandler(WampServer wampocServer) {
		this.wampocServer = wampocServer;
	}
	
	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest request,
			String protocol) {
		return new JettyServerWebSocketProxy(wampocServer);
	}

	protected WampServer wampocServer;
}
