package fr.ocroquette.wampoc.server;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class SessionIdFactoryTest {

	@Test
	public void subsequentIdsShallBeDifferent() {
		SessionIdFactory f1 = new SessionIdFactory();

		SessionId f1_id1 = f1.getNew(); 
		SessionId f1_id2 = f1.getNew(); 

		assertFalse(f1_id1.equals(f1_id2));
	}

	@Test
	public void idsFromDifferentFactoryShallBeDifferent() {
		SessionIdFactory f1 = new SessionIdFactory();
		SessionIdFactory f2 = new SessionIdFactory();

		SessionId f1_id1 = f1.getNew(); 
		SessionId f2_id1 = f2.getNew();

		assertFalse(f1_id1.equals(f2_id1));
	}
}
