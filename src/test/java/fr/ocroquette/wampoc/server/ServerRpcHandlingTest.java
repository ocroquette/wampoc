package fr.ocroquette.wampoc.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;


import org.junit.Test;

import fr.ocroquette.wampoc.exceptions.BadArgumentException;
import fr.ocroquette.wampoc.messages.CallErrorMessage;
import fr.ocroquette.wampoc.messages.CallMessage;
import fr.ocroquette.wampoc.messages.CallResultMessage;
import fr.ocroquette.wampoc.messages.Message;
import fr.ocroquette.wampoc.messages.MessageMapper;
import fr.ocroquette.wampoc.server.Session;
import fr.ocroquette.wampoc.server.RpcCall;
import fr.ocroquette.wampoc.server.RpcHandler;
import fr.ocroquette.wampoc.server.WampServer;
import fr.ocroquette.wampoc.testutils.ProtocollingChannel;

public class ServerRpcHandlingTest {
	final String serverIdent = "SERVER IDENT";

	/***
	 * RPC results shall be sent correctly to the client
	 */
	@Test
	public void serverExecuteSuccessfullRpcFromSingleClient() throws IOException, BadArgumentException {
		WampServer server = new WampServer(serverIdent);
		String procedureId = "http://host/procedureId";
		server.registerRpcHandler(procedureId, myTestRpcHandler());

		ProtocollingChannel channel = new ProtocollingChannel();
		Session session = server.openSession(channel);

		CallMessage callMessage = newCallMessage(procedureId, new MyRpcInputPayload());
		server.handleIncomingMessage(session, callMessage);

		assertEquals(2, channel.handledMessages.size());

		Message message = MessageMapper.fromJson(channel.handledMessages.get(1));
		CallResultMessage callResultMessage = (CallResultMessage) message;
		assertNotNull(callResultMessage);
		assertEquals("Dns3wuQo0ipOX1Xc", callResultMessage.callId);
		MyRpcOutputSimpleString rpcOutput = callResultMessage.getPayload(MyRpcOutputSimpleString.class);
		assertEquals("Hello back!", rpcOutput.s2);

	}
	/***
	 * RPC results shall be  sent to the right client
	 * @throws IOException 
	 * @throws BadArgumentException 
	 */
	@Test
	public void serverExecuteSuccessfullRpcWithMultipleClients() throws IOException, BadArgumentException {
		WampServer server = new WampServer(serverIdent);
		String procedureId = "http://host/procedureId";
		server.registerRpcHandler(procedureId, myTestRpcHandler());

		ProtocollingChannel channel1 = new ProtocollingChannel();
		Session session1 = server.openSession(channel1);

		ProtocollingChannel channel2 = new ProtocollingChannel();
		Session session2 = server.openSession(channel2);

		CallMessage callMessage = newCallMessage(procedureId, new MyRpcInputPayload());
		server.handleIncomingMessage(session1, callMessage);

		assertEquals(2, channel1.handledMessages.size());
		assertEquals(1, channel2.handledMessages.size());

		server.handleIncomingMessage(session2, callMessage);

		assertEquals(2, channel2.handledMessages.size());
	}

	/***
	 * RPC results shall be sent correctly to the client
	 * @throws IOException 
	 * @throws BadArgumentException 
	 */
	@Test
	public void serverExecuteFailingRpcFromSingleClient() throws IOException, BadArgumentException {
		WampServer server = new WampServer(serverIdent);
		String procedureId = "http://host/procedureId";
		server.registerRpcHandler(procedureId, myTestRpcHandler());

		ProtocollingChannel channel = new ProtocollingChannel();
		Session session = server.openSession(channel);

		MyRpcInputPayload input = new MyRpcInputPayload();
		input.pleaseFail("http://error.com", "Error description", "Error details");
		CallMessage callMessage = newCallMessage(procedureId, input);
		server.handleIncomingMessage(session, callMessage);

		assertEquals(2, channel.handledMessages.size());

		Message message = MessageMapper.fromJson(channel.handledMessages.get(1));
		CallErrorMessage callErrorMessage = (CallErrorMessage) message;
		assertNotNull(callErrorMessage);
		assertEquals(callMessage.callId, callErrorMessage.callId);
		assertEquals(input.failWithErrorUri, callErrorMessage.errorUri);
		assertEquals(input.failWithErrorDesc, callErrorMessage.errorDesc);
		assertEquals(input.failWithErrorDetails, callErrorMessage.getErrorDetails(String.class));
	}

	@Test
	public void unkwownProcedure() throws IOException, BadArgumentException {
		WampServer server = new WampServer(serverIdent);
		String procedureId = "http://host/procedureId";

		ProtocollingChannel channel = new ProtocollingChannel();
		Session session = server.openSession(channel);

		CallMessage callMessage = newCallMessage(procedureId, null);
		server.handleIncomingMessage(session, callMessage);

		assertEquals(2, channel.handledMessages.size());

		Message message = MessageMapper.fromJson(channel.handledMessages.get(1));
		CallErrorMessage callErrorMessage = (CallErrorMessage) message;
		assertNotNull(callErrorMessage);
		assertEquals(callMessage.callId, callErrorMessage.callId);
		assertEquals(callErrorMessage.errorUri, "http://ocroquette.fr/noHandlerForProcedure");
	}

	public class MyRpcInputPayload {
		MyRpcInputPayload() {
			s = "Hello";
			i = 10;
		}
		void pleaseFail(String errorUri, String errorDesc, String errorDetails) {
			pleaseFail = true;
			failWithErrorUri = errorUri;
			failWithErrorDesc = errorDesc;
			failWithErrorDetails = errorDetails;
		}
		public String s;
		public int i;
		public boolean pleaseFail;
		public String failWithErrorUri;
		public String failWithErrorDesc;
		public String failWithErrorDetails;
	};

	public class MyRpcOutputSimpleString {
		public String s2;
	};

	private RpcHandler myTestRpcHandler() {
		return new RpcHandler() {

			@Override
			public void execute(RpcCall rpcCall) {
				MyRpcInputPayload input = rpcCall.getInput(MyRpcInputPayload.class);
				assertNotNull(input);
				assertEquals(input.s, "Hello");
				assertEquals(input.i, 10);
				if ( ! input.pleaseFail ) {
					MyRpcOutputSimpleString rpcOutput = new MyRpcOutputSimpleString();
					rpcOutput.s2 = "Hello back!";
					rpcCall.setOutput(rpcOutput, MyRpcOutputSimpleString.class);
				}
				else {
					// rpcCall.setError("http://error.com", "Some error occured");
					rpcCall.setError(input.failWithErrorUri, input.failWithErrorDesc, input.failWithErrorDetails);
				}
			}

		};
	}


	private CallMessage newCallMessage(String procedureId, MyRpcInputPayload input) {
		CallMessage callMessage = new CallMessage();
		callMessage.callId = "Dns3wuQo0ipOX1Xc";
		callMessage.procedureId = procedureId;
		callMessage.setPayload(input, MyRpcInputPayload.class);
		return callMessage;
	}
}
