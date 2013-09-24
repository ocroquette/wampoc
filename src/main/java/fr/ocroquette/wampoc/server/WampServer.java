package fr.ocroquette.wampoc.server;

import java.io.IOException;
import java.util.ArrayList;
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
		this.serverIdent = serverIdent; 
	}

	protected void init() {
		openSessions = new ConcurrentHashMap<String, Session>();
		rpcHandlers = new ConcurrentHashMap<String, RpcHandler>();
		serverIdent = "<UNIDENTIFIED SERVER>";
		subscriptions = new Subscriptions();
		sessionFactory = new SessionFactory();
	}

	public Session openSession(Channel outgoingChannel) {
		Session session = sessionFactory.getNew(outgoingChannel);
		openSessions.put(session.getId(), session);
		sendMessageToClient(session, MessageMapper.toJson(new WelcomeMessage(session.getId(), serverIdent)));
		return session;
	}

	public void closeSession(Session session) {
		openSessions.remove(session);
		session.close();
	}

	public void handleIncomingString(Session session, String jsonText) throws IOException, BadArgumentException {
		Message message = MessageMapper.fromJson(jsonText);
		if ( message == null ) {
			throw new BadArgumentException("Could not parse the input jsonText");
		}
		handleIncomingMessage(session, message);
	}

	public void handleIncomingMessage(Session session, Message message) throws IOException, BadArgumentException {
		if ( incomingFrameEavesDropper.size() > 0)
			notifyIncomingFramesEavesdroppers(session.getId(), MessageMapper.toJson(message));
		if ( ! isValidSession(session) ) {
			throw new BadArgumentException("Invalid session " + session);
		}
		switch(message.getType()) {
		case CALL:
			handleIncomingCallMessage(session, (CallMessage)message);
			break;
		case SUBSCRIBE:
			handleIncomingSubscribeMessage(session, (SubscribeMessage)message);
			break;
		case UNSUBSCRIBE:
			handleIncomingUnsubscribeMessage(session, (UnsubscribeMessage)message);
			break;
		case PUBLISH:
			handleIncomingPublishMessage(session, (PublishMessage)message);
			break;
		default:
			throw new BadArgumentException("Unsupported message type: " + message.getType());
		}
	}

	protected void handleIncomingSubscribeMessage(Session session, SubscribeMessage message) {
		subscriptions.subscribe(session.getId(), message.topicUri);
	}

	protected void handleIncomingUnsubscribeMessage(Session session, UnsubscribeMessage message) {
		subscriptions.unsubscribe(session.getId(), message.topicUri);
	}

	protected void handleIncomingCallMessage(Session session, CallMessage message) throws IOException, BadArgumentException {
		String procedureId = message.procedureId;
		RpcCall rpcCall = new RpcCall(session.getId(), message);
		RpcHandler handler= rpcHandlers.get(procedureId);
		if ( handler != null ) {
			handler.execute(rpcCall);
			sendMessageToClient(session, rpcCall.getCallResultAsJson());
		}
		else {
			rpcCall.setError("http://ocroquette.fr/noHandlerForProcedure", "No handler defined for " + procedureId);
			sendMessageToClient(session, rpcCall.getCallResultAsJson());
		}
	}

	protected void handleIncomingPublishMessage(final Session session, final PublishMessage message) throws IOException {

		EventMessage eventMessage = new EventMessage(message.topicUri);
		eventMessage.setPayloadJsonElement(message.payload);
		final String eventMessageJson = MessageMapper.toJson(eventMessage);

		Subscriptions.ActionOnSubscriber action = new Subscriptions.ActionOnSubscriber() {
			@Override
			public void execute(String subscriberClientId) {
				if ( shallSendPublish(message.excludeMe, session.getId(), subscriberClientId)) {
					try {
						sendMessageToClient(getSession(subscriberClientId), eventMessageJson);
					} catch (BadArgumentException e) {
						// The session has been discarded in the meantime, there is not much we can do about it 
					}
				}
			}
		};
		subscriptions.forAllSubscribers(message.topicUri, action);
	}

	protected boolean shallSendPublish(Boolean excludeMe, String from, String to) {
		return excludeMe == null || ! excludeMe.booleanValue() || from != to;
	}

	protected void sendMessageToClient(Session session, String message) {
		try {
			session.sendMessage(message);
		}
		catch(IOException e) {
			// TODO
			System.out.println("Looks like client " + session + " is not reachable anymore. Discarding.");
			closeSession(session);
		}
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getSession(String sessionId) throws BadArgumentException {
		Session s = openSessions.get(sessionId);
		if (s == null )
			throw new BadArgumentException("Unknown session id: " + sessionId);
		return s;
	}

	public boolean isValidSession(Session session) {
		return openSessions.containsValue(session);
	}

	public boolean isValidSession(String sessionId) {
		return openSessions.containsKey(sessionId);
	}


	public void registerRpcHandler(String procedureId, RpcHandler rpcHandler) {
		rpcHandlers.put(procedureId, rpcHandler);
	}

	public void addIncomingFramesEavesdropper(TextFrameEavesdropper incomingEavesdropper) {
		incomingFrameEavesDropper.add(incomingEavesdropper);
	}

	public void removeIncomingFramesEavesdropper(TextFrameEavesdropper incomingEavesdropper) {
		incomingFrameEavesDropper.remove(incomingEavesdropper);
	}

	public void notifyIncomingFramesEavesdroppers(String session, String frame) {
		for ( TextFrameEavesdropper eavesdropper : incomingFrameEavesDropper )
			eavesdropper.handler(session, frame);
	}


	protected Map<String, Session> openSessions;
	protected Map<String, RpcHandler> rpcHandlers;
	protected Subscriptions subscriptions;
	protected String serverIdent;
	protected SessionFactory sessionFactory;
	protected List<TextFrameEavesdropper> incomingFrameEavesDropper = new ArrayList<TextFrameEavesdropper>();
}
