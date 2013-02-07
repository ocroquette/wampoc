package fr.ocroquette.wampoc.testutils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.ocroquette.wampoc.common.Channel;

public class ErrorChannel implements Channel {
	@Override
	public void handle(String message) throws IOException {
		throw new IOException("Forced exception from ErrorChannel");
	}
	
}