package fr.ocroquette.wampoc.server;

import fr.ocroquette.wampoc.common.Channel;


public class SessionFactory {
	public Session getNew(Channel channel) {
		return new Session(channel);
	}
}
