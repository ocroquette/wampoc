package fr.ocroquette.wampoc.messages;

public class SimplePayload {
	public SimplePayload(String s, int i) {
		this.s = s;
		this.i = i;
	}

	@Override
	public boolean equals(Object obj) {
		if ( ! (obj instanceof SimplePayload ) || obj == null ) {
			return false;
		}
		SimplePayload other = (SimplePayload)obj;

		return other.s.equals(s) && other.i == i; 
	}

	public String s;
	public int i;

}