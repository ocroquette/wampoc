package fr.ocroquette.wampoc.testutils;

import java.util.UUID;

public class Utils {
	static public String q(String s) {
		return "\"" + s + "\"";
	}
	
	static public String rndStr() {
		return UUID.randomUUID().toString();
	}

}
