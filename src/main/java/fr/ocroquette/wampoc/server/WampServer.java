package fr.ocroquette.wampoc.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fr.ocroquette.wampoc.common.Channel;
import fr.ocroquette.wampoc.exceptions.BadArgumentException;
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
		sessionLifeCycleListeners = Collections.synchronizedList(new ArrayList<SessionLifecycleListener>());

	}

	public SessionId connectClient(Channel outgoingChannel) {
		SessionId sessionId = sessionIdFactory.getNew();
		outgoingClientChannels.put(sessionId, outgoingChannel);
		try {
			outgoingChannel.handle(MessageMapper.toJson(new WelcomeMessage(sessionId.toString(), serverIdent)));
		} catch (IOException e) {
			// How sad: we could not even greet this client
			return null;
		}
		
		for(SessionLifecycleListener l: sessionLifeCycleListeners)
			l.onCreation(sessionId);
		
		return sessionId;
	}

	public void handleIncomingMessage(SessionId sessionId, String jsonText) throws IOException, BadArgumentException {
		Message message = MessageMapper.fromJson(jsonText);
		if ( message == null ) {
			throw new BadArgumentException("Could not parse the input jsonText");
		}
		handleIncomingMessage(sessionId, message);
	}

	public void handleIncomingMessage(SessionId sessionId, Message message) throws IOException, BadArgumentException {
		if ( ! isValidSession(sessionId) ) {
			throw new BadArgumentException("Unknown session " + sessionId);
		}
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
			throw new BadArgumentException("Unsupported message type: " + message.getType());
		}
	}

	protected void handleIncomingSubscribeMessage(SessionId sessionId, SubscribeMessage message) {
		subscriptions.subscribe(sessionId, message.topicUri);
	}

	protected void handleIncomingUnsubscribeMessage(SessionId sessionId, UnsubscribeMessage message) {
		subscriptions.unsubscribe(sessionId, message.topicUri);
	}

	protected void handleIncomingCallMessage(SessionId sessionId, CallMessage message) throws IOException, BadArgumentException {
		String procedureId = message.procedureId;
		RpcCall rpcCall = new RpcCall(sessionId, message);
		RpcHandler handler= rpcHandlers.get(procedureId);
		if ( handler != null ) {
			handler.execute(rpcCall);
			sendMessageToClient(sessionId, rpcCall.getCallResultAsJson());
		}
		else {
			rpcCall.setError("http://ocroquette.fr/noHandlerForProcedure", "No handler defined for " + procedureId);
			sendMessageToClient(sessionId, rpcCall.getCallResultAsJson());
		}
	}

	protected void handleIncomingPublishMessage(final SessionId sessionId, final PublishMessage message) throws IOException {
		final EventMessage eventMessage = new EventMessage(message.topicUri);
		eventMessage.setPayload(message.payload);
		Subscriptions.ActionOnSubscriber action = new Subscriptions.ActionOnSubscriber() {
			@Override
			public void execute(SessionId subscriberClientId) {
				if ( shallSendPublish(message.excludeMe, sessionId, subscriberClientId)) {
					try {
						sendMessageToClient(subscriberClientId, MessageMapper.toJson(eventMessage));
					} catch (BadArgumentException e) {
						// The session has been discarded in the meantime, there is not much we can do about it 
					}
				}
			}
		};
		subscriptions.forAllSubscribers(message.topicUri, action);
	}

	protected boolean shallSendPublish(Boolean excludeMe, SessionId from, SessionId to) {
		return excludeMe == null || ! excludeMe.booleanValue() || from != to;
	}

	protected void sendMessageToClient(SessionId sessionId, String message) throws BadArgumentException {
		Channel channel = outgoingClientChannels.get(sessionId);
		if ( channel != null ) {
			try {
				channel.handle(message);
			}
			catch(IOException e) {
				// TODO
				System.out.println("Looks like client " + sessionId + " is not reachable anymore. Discarding.");
				discardClient(sessionId);
			}
		}
		else
			throw new BadArgumentException("Unknown sessionId \"" + sessionId + "\"");
	}

	public void discardClient(SessionId sessionId) {
		outgoingClientChannels.remove(sessionId);
		for(SessionLifecycleListener l: sessionLifeCycleListeners)
			l.onDiscard(sessionId);
	}

	public boolean isValidSession(SessionId sessionId) {
		return outgoingClientChannels.get(sessionId) != null;
	}

	public void registerRpcHandler(String procedureId, RpcHandler rpcHandler) {
		rpcHandlers.put(procedureId, rpcHandler);
	}

	public void addSessionLifecycleListener(SessionLifecycleListener listener) {
		sessionLifeCycleListeners.add(listener);
	}

	public void removeSessionLifecycleListener(SessionLifecycleListener listener) {
		sessionLifeCycleListeners.remove(listener);
	}

	protected Map<SessionId, Channel> outgoingClientChannels;
	protected Map<String, RpcHandler> rpcHandlers;
	protected Subscriptions subscriptions;
	protected String serverIdent;
	protected SessionIdFactory sessionIdFactory;
	protected List<SessionLifecycleListener> sessionLifeCycleListeners;

}
