package fr.ocroquette.wampoc.server;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fr.ocroquette.wampoc.common.Channel;
import fr.ocroquette.wampoc.messages.CallMessage;
import fr.ocroquette.wampoc.messages.EventMessage;
import fr.ocroquette.wampoc.messages.Message;
import fr.ocroquette.wampoc.messages.MessageMapper;
import fr.ocroquette.wampoc.messages.PublishMessage;
import fr.ocroquette.wampoc.messages.SubscribeMessage;
import fr.ocroquette.wampoc.messages.UnsubscribeMessage;
import fr.ocroquette.wampoc.messages.WelcomeMessage;

public class WampServer {

	public WampServer() {
		init();
	}

	public WampServer(String serverIdent) {
		init();
		this.serverIdent =serverIdent; 
	}

	protected void init() {
		outgoingClientChannels = new ConcurrentHashMap<SessionId, Channel>();
		rpcHandlers = new ConcurrentHashMap<String, RpcHandler>();
		serverIdent = "<UNIDENTIFIED SERVER>";
		subscriptions = new Subscriptions();
		sessionIdFactory = new SessionIdFactory();
	}

	public SessionId addClient(Channel outgoingChannel) {
		SessionId sessionId = sessionIdFactory.getNew();
		outgoingClientChannels.put(sessionId, outgoingChannel);
		try {
			outgoingChannel.handle(MessageMapper.toJson(new WelcomeMessage(sessionId.toString(), serverIdent)));
		} catch (IOException e) {
			// How sad: we could not even greet this client
			return null;
		}
		
		return sessionId;
	}

	public void handleIncomingMessage(SessionId sessionId, String jsonText) throws IOException {
		Message message = MessageMapper.fromJson(jsonText);
		switch(message.getType()) {
		case CALL:
			handleIncomingCallMessage(sessionId, (CallMessage)message);
			break;
		case SUBSCRIBE:
			handleIncomingSubscribeMessage(sessionId, (SubscribeMessage)message);
			break;
		case UNSUBSCRIBE:
			handleIncomingUnsubscribeMessage(sessionId, (UnsubscribeMessage)message);
			break;
		case PUBLISH:
			handleIncomingPublishMessage(sessionId, (PublishMessage)message);
			break;
		default:
			// TODO logging
			System.err.println("ERROR: handleIncomingMessage doesn't know how to handle message type " + message.getType() );
		}
	}

	private void handleIncomingSubscribeMessage(SessionId sessionId, SubscribeMessage message) {
		subscriptions.subscribe(sessionId, message.topicUri);
	}

	private void handleIncomingUnsubscribeMessage(SessionId sessionId, UnsubscribeMessage message) {
		subscriptions.unsubscribe(sessionId, message.topicUri);
	}

	private void handleIncomingCallMessage(SessionId sessionId, CallMessage message) throws IOException {
		String procedureId = message.procedureId;
		RpcHandler handler= rpcHandlers.get(procedureId);
		if ( handler != null ) {
			RpcCall rpcCall = new RpcCall(message);
			handler.execute(rpcCall);
			sendMessageToClient(sessionId, rpcCall.getResultingJson());
		}
		else
			// TODO
			System.out.println("No handler registered for "+procedureId);
	}

	private void handleIncomingPublishMessage(final SessionId sessionId, final PublishMessage message) throws IOException {
		final EventMessage eventMessage = new EventMessage(message.topicUri);
		eventMessage.setPayload(message.payload);
		Subscriptions.ActionOnSubscriber action = new Subscriptions.ActionOnSubscriber() {
			@Override
			public void execute(SessionId subscriberClientId) {
				if ( shallSendPublish(message.excludeMe, sessionId, subscriberClientId))
					sendMessageToClient(subscriberClientId, MessageMapper.toJson(eventMessage));
			}
		};
		subscriptions.forAllSubscribers(message.topicUri, action);
	}

	private boolean shallSendPublish(Boolean excludeMe, SessionId from, SessionId to) {
		return excludeMe == null || ! excludeMe.booleanValue() || from != to;
	}

	protected void sendMessageToClient(SessionId sessionId, String message) {
		Channel channel = outgoingClientChannels.get(sessionId);
		if ( channel != null ) {
			try {
				channel.handle(message);
			}
			catch(IOException e) {
				// TODO
				System.out.println("Looks like client " + sessionId + "is disconnecting. Discarding.");
				deleteClient(sessionId);
			}
		}
		else
			// TODO
			System.out.println("Cannot send to client, client ID unknown: "+sessionId);
	}

	private void deleteClient(SessionId sessionId) {
		outgoingClientChannels.remove(sessionId);
	}


	public void cancelAllSubscriptions(SessionId sessionId) {
	}

	public void registerRpcHandler(String procedureId, RpcHandler rpcHandler) {
		rpcHandlers.put(procedureId, rpcHandler);
	}

	protected Map<SessionId, Channel> outgoingClientChannels;
	protected Map<String, RpcHandler> rpcHandlers;
	protected Subscriptions subscriptions;
	protected String serverIdent;
	protected SessionIdFactory sessionIdFactory;

}
