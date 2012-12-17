package fr.ocroquette.wampoc.common;

import java.io.IOException;

public interface Channel {
	void handle(String message) throws IOException;
}
