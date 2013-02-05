package fr.ocroquette.wampoc.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;


import org.junit.Test;

import fr.ocroquette.wampoc.messages.CallErrorMessage;
import fr.ocroquette.wampoc.messages.CallMessage;
import fr.ocroquette.wampoc.messages.CallResultMessage;
import fr.ocroquette.wampoc.messages.Message;
import fr.ocroquette.wampoc.messages.MessageMapper;
import fr.ocroquette.wampoc.server.SessionId;
import fr.ocroquette.wampoc.server.RpcCall;
import fr.ocroquette.wampoc.server.RpcHandler;
import fr.ocroquette.wampoc.server.WampServer;
import fr.ocroquette.wampoc.testutils.ProtocollingChannel;

public class ServerRpcHandlingTest {
	final String serverIdent = "SERVER IDENT";

	/***
	 * RPC results shall be sent correctly to the client
	 * @throws IOException 
	 */
	@Test
	public void serverExecuteSuccessfullRpcFromSingleClient() throws IOException {
		WampServer server = new WampServer(serverIdent);
		String procedureId = "http://host/procedureId";
		server.registerRpcHandler(procedureId, newRpcHandler());

		ProtocollingChannel channel = new ProtocollingChannel();
		SessionId clientId = server.addClient(channel);

		CallMessage callMessage = newCallMessage(procedureId, new RpcInput());
		server.handleIncomingMessage(clientId, MessageMapper.toJson(callMessage));

		assertEquals(2, channel.handledMessages.size());

		Message message = MessageMapper.fromJson(channel.handledMessages.get(1));
		CallResultMessage callResultMessage = (CallResultMessage) message;
		assertNotNull(callResultMessage);
		assertEquals("Dns3wuQo0ipOX1Xc", callResultMessage.callId);
		RpcOutput rpcOutput = callResultMessage.getPayload(RpcOutput.class);
		assertEquals("Hello back!", rpcOutput.s2);

	}
	/***
	 * RPC results shall be  sent to the right client
	 * @throws IOException 
	 */
	@Test
	public void serverExecuteSuccessfullRpcWithMultipleClients() throws IOException {
		WampServer server = new WampServer(serverIdent);
		String procedureId = "http://host/procedureId";
		server.registerRpcHandler(procedureId, newRpcHandler());

		ProtocollingChannel channel1 = new ProtocollingChannel();
		SessionId clientId1 = server.addClient(channel1);

		ProtocollingChannel channel2 = new ProtocollingChannel();
		SessionId clientId2 = server.addClient(channel2);

		CallMessage callMessage = newCallMessage(procedureId, new RpcInput());
		server.handleIncomingMessage(clientId1, MessageMapper.toJson(callMessage));

		assertEquals(2, channel1.handledMessages.size());
		assertEquals(1, channel2.handledMessages.size());

		server.handleIncomingMessage(clientId2, MessageMapper.toJson(callMessage));

		assertEquals(2, channel2.handledMessages.size());
	}

	/***
	 * RPC results shall be sent correctly to the client
	 * @throws IOException 
	 */
	@Test
	public void serverExecuteFailingRpcFromSingleClient() throws IOException {
		WampServer server = new WampServer(serverIdent);
		String procedureId = "http://host/procedureId";
		server.registerRpcHandler(procedureId, newRpcHandler());

		ProtocollingChannel channel = new ProtocollingChannel();
		SessionId clientId = server.addClient(channel);

		RpcInput input = new RpcInput();
		input.pleaseFail("http://error.com", "Error description", "Error details");
		CallMessage callMessage = newCallMessage(procedureId, input);
		server.handleIncomingMessage(clientId, MessageMapper.toJson(callMessage));

		assertEquals(2, channel.handledMessages.size());

		Message message = MessageMapper.fromJson(channel.handledMessages.get(1));
		CallErrorMessage callErrorMessage = (CallErrorMessage) message;
		assertNotNull(callErrorMessage);
		assertEquals(callMessage.callId, callErrorMessage.callId);
		assertEquals(input.failWithErrorUri, callErrorMessage.errorUri);
		assertEquals(input.failWithErrorDesc, callErrorMessage.errorDesc);
		assertEquals(input.failWithErrorDetails, callErrorMessage.getErrorDetails(String.class));
	}

	public class RpcInput {
		RpcInput() {
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

	public class RpcOutput {
		public String s2;
	};

	private RpcHandler newRpcHandler() {
		return new RpcHandler() {

			@Override
			public void execute(RpcCall rpcCall) {
				RpcInput input = rpcCall.getInput(RpcInput.class);
				assertNotNull(input);
				assertEquals(input.s, "Hello");
				assertEquals(input.i, 10);
				if ( ! input.pleaseFail ) {
					RpcOutput rpcOutput = new RpcOutput();
					rpcOutput.s2 = "Hello back!";
					rpcCall.setOutput(rpcOutput, RpcOutput.class);
				}
				else {
					// rpcCall.setError("http://error.com", "Some error occured");
					rpcCall.setError(input.failWithErrorUri, input.failWithErrorDesc, input.failWithErrorDetails);
				}
			}

		};
	}


	private CallMessage newCallMessage(String procedureId, RpcInput input) {
		CallMessage callMessage = new CallMessage();
		callMessage.callId = "Dns3wuQo0ipOX1Xc";
		callMessage.procedureId = procedureId;
		callMessage.setPayload(input, RpcInput.class);
		return callMessage;
	}
}
