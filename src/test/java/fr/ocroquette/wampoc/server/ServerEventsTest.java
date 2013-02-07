package fr.ocroquette.wampoc.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import fr.ocroquette.wampoc.exceptions.BadArgumentException;
import fr.ocroquette.wampoc.messages.EventMessage;
import fr.ocroquette.wampoc.messages.Message;
import fr.ocroquette.wampoc.messages.MessageMapper;
import fr.ocroquette.wampoc.messages.PublishMessage;
import fr.ocroquette.wampoc.messages.SubscribeMessage;
import fr.ocroquette.wampoc.messages.UnsubscribeMessage;
import fr.ocroquette.wampoc.server.Session;
import fr.ocroquette.wampoc.server.WampServer;
import fr.ocroquette.wampoc.testutils.ProtocollingChannel;

public class ServerEventsTest {
	final String serverIdent = "SERVER IDENT";

	@Test
	public void subscribeAndPublish() throws IOException, BadArgumentException {
		String payload = "Publish payload";
		
		WampServer server = new WampServer(serverIdent);
		String topicId = "http://host/topicId";

		ProtocollingChannel channel1 = new ProtocollingChannel();
		Session session1 = server.openSession(channel1);
		SubscribeMessage subscribeMessage = new SubscribeMessage(topicId);
		server.handleIncomingMessage(session1, subscribeMessage);

		ProtocollingChannel channel2 = new ProtocollingChannel();
		Session session2 = server.openSession(channel2);
		server.handleIncomingMessage(session2, subscribeMessage);


		PublishMessage publishMessage = new PublishMessage(topicId);
		publishMessage.setPayload(payload);

		// System.out.println(MessageMapper.toJson(publishMessage));
		server.handleIncomingMessage(session1, publishMessage);

		assertEquals(2, channel1.handledMessages.size());
		assertEquals(2, channel2.handledMessages.size());

		// System.out.println(channel.handledMessages.get(1));
		Message message = MessageMapper.fromJson(channel1.last());
		EventMessage eventMessage = (EventMessage) message;
		assertEquals(payload, eventMessage.getPayload(String.class));
		
		publishMessage.excludeMe = true;
		server.handleIncomingMessage(session1, publishMessage);
		assertEquals(2, channel1.handledMessages.size());
		assertEquals(3, channel2.handledMessages.size());
	}

	@Test
	public void unsubscribe() throws IOException, BadArgumentException {
		String payload = "Publish payload";
		
		WampServer server = new WampServer(serverIdent);
		String topicId = "http://host/topicId";

		ProtocollingChannel channel1 = new ProtocollingChannel();
		Session session1 = server.openSession(channel1);
		SubscribeMessage subscribeMessage = new SubscribeMessage(topicId);
		server.handleIncomingMessage(session1, subscribeMessage);

		PublishMessage publishMessage = new PublishMessage(topicId);
		publishMessage.setPayload(payload);

		server.handleIncomingMessage(session1, publishMessage);
		assertEquals(2, channel1.handledMessages.size());
		assertEquals("[8,\"http://host/topicId\",\"Publish payload\"]", channel1.last());

		UnsubscribeMessage unsubscribeMessage = new UnsubscribeMessage(topicId);
		server.handleIncomingMessage(session1, unsubscribeMessage);

		server.handleIncomingMessage(session1, publishMessage);
		assertEquals(2, channel1.handledMessages.size());
	}
}
