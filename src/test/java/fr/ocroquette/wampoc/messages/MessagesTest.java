package fr.ocroquette.wampoc.messages;

import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Test;


public class MessagesTest {

	static public <Type> void verifyFailingParsing(Class<Type> type, String[] invalidJsonTexts) {
		for (String invalidJsonText : Arrays.asList(invalidJsonTexts) ) {
			@SuppressWarnings("unchecked")
			Type msg = (Type) MessageMapper.fromJson(invalidJsonText);
			assertNull(msg);
		}
	}


	@Test
	public void unserializeInvalidMessages() {
		verifyFailingParsing(WelcomeMessage.class, new String[] {
			"",
			"1",
			"[-1]",
			"[9]"
		});
	}

}
