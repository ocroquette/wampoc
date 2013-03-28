package fr.ocroquette.wampoc.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import fr.ocroquette.wampoc.exceptions.BadArgumentException;
import fr.ocroquette.wampoc.messages.CallMessage;
import fr.ocroquette.wampoc.messages.MessageMapper;
import fr.ocroquette.wampoc.testutils.ProtocollingChannel;

public class TextFrameEavesdropperTest {
	
	public class Frame {
		Frame(String sessionId, String frame) {
			this.sessionId = sessionId;
			this.frame = frame;
		}
		public String sessionId;
		public String frame;
	};
	
	@Test
	public void serverMustWelcomeClient() throws IOException, BadArgumentException {
		WampServer server = new WampServer(UUID.randomUUID().toString());
		ProtocollingChannel channel = new ProtocollingChannel(); 
		Session session = server.openSession(channel);
		final String sessionId = session.getId();
		
		final List<Frame> frames = new ArrayList<Frame>();
		
		TextFrameEavesdropper incomingEavesdropper = new TextFrameEavesdropper() {
			@Override
			public void handler(String id, String frame) {
				frames.add(new Frame(id, frame));
			}

		};

		server.addIncomingFramesEavesdropper(incomingEavesdropper);

		CallMessage callMessage = new CallMessage("ProcedureId", "Payload");
		server.handleIncomingMessage(session, callMessage);

		assertEquals(1, frames.size());
		assertEquals(sessionId, frames.get(0).sessionId);
		assertEquals(MessageMapper.toJson(callMessage), frames.get(0).frame);
	}


}
