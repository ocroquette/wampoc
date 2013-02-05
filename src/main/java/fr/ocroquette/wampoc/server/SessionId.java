package fr.ocroquette.wampoc.server;

import org.apache.commons.lang.builder.EqualsBuilder;

public class SessionId {

	SessionId(String s) {
		if ( s == null )
			throw new NullPointerException("String cannot be null as identifier for ClientId");
		this.stringId = s;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return stringId;
	}

	private final String stringId;
}
