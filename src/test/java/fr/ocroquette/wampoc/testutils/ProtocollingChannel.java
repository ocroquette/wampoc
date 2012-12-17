package fr.ocroquette.wampoc.testutils;

import java.util.ArrayList;
import java.util.List;

import fr.ocroquette.wampoc.common.Channel;

public class ProtocollingChannel implements Channel {
	public List<String> handledMessages = new ArrayList<String>();

	@Override
	public void handle(String message) {
		// System.out.println("Channel:handle " + message);
		handledMessages.add(message);
	}
	
	public String last() {
		return handledMessages.get(handledMessages.size()-1);
	}

}