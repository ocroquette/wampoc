package fr.ocroquette.wampoc.server;

import java.math.BigInteger;
import java.util.UUID;


public class SessionIdFactory {
	public SessionId getNew() {
		String s = (UUID.randomUUID() + "-" + UUID.randomUUID()).replace("-", "");
		return new SessionId(s);
	}
}
