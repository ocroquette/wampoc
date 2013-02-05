package fr.ocroquette.wampoc.adapters.jetty;

import java.io.IOException;

import org.eclipse.jetty.websocket.WebSocket.Connection;

import fr.ocroquette.wampoc.common.Channel;

public class ChannelToConnectionAdapter implements Channel {
	public ChannelToConnectionAdapter(Connection connection) {
		this.connection = connection;
	}
	@Override
	public void handle(String message) throws IOException {
		connection.sendMessage(message);
	}
	private Connection connection;
}