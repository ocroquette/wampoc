package fr.ocroquette.wampoc.server;

import java.math.BigInteger;


public class ClientIdFactory {
	final BigInteger zero = new BigInteger("0");
	final BigInteger one = new BigInteger("1");
	protected BigInteger next = zero;
	
	public ClientId getNext() {
		next = next.add(one);
		return new ClientId(next.toString());
	}
}
