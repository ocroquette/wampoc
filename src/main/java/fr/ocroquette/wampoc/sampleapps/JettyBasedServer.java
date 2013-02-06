package fr.ocroquette.wampoc.sampleapps;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import fr.ocroquette.wampoc.adapters.jetty.JettyServerHandler;
import fr.ocroquette.wampoc.common.Channel;
import fr.ocroquette.wampoc.exceptions.BadArgumentException;
import fr.ocroquette.wampoc.messages.MessageMapper;
import fr.ocroquette.wampoc.messages.PublishMessage;
import fr.ocroquette.wampoc.server.SessionId;


public class JettyBasedServer 
{

	public static void main( String[] args )
	{
		int tcpPort = 8081;

		Server jettyServer = new Server(tcpPort);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		jettyServer.setHandler(context);

		fr.ocroquette.wampoc.server.WampServer wampocServer = new fr.ocroquette.wampoc.server.WampServer();
		JettyServerHandler webSocketHandler = new JettyServerHandler(wampocServer);
		webSocketHandler.setHandler(new DefaultHandler());
		jettyServer.setHandler(webSocketHandler);

		System.err.println("Starting the WS server on TCP port: " + tcpPort);
		try {
			jettyServer.start();
			startPostman(wampocServer);
			jettyServer.join();
		} catch (Exception e) {
			System.err.println("Failed to start the WS server:\n" +e);
		}
	}

	public static class Postman implements Runnable {
		Postman(fr.ocroquette.wampoc.server.WampServer server) {
			this.server = server;
		}
		@Override
		public void run() {
			SessionId clientId;
			try {
				clientId = server.connectClient(new Channel() {
					@Override
					public void handle(String message) throws IOException {
					}});
				PublishMessage msg = new PublishMessage("http://example.com/simple");
				msg.setPayload("Hello world!");
				String json = MessageMapper.toJson(msg);
				while( true ) {
					try {
						Thread.sleep(5000);
						System.out.println("Postman says:"+json);
						server.handleIncomingMessage(clientId, json);
					} catch (InterruptedException e) {
						return;
					} catch (BadArgumentException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		protected fr.ocroquette.wampoc.server.WampServer server;
	}

	public static void startPostman(fr.ocroquette.wampoc.server.WampServer server) {
		new Thread(new Postman(server)).start();
	}
}
