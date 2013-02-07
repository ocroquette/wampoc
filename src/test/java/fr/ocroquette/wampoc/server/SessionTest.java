package fr.ocroquette.wampoc.server;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class SessionTest {

	@Test
	public void subsequentIdsShallBeDifferent() {
		SessionFactory f1 = new SessionFactory();

		Session s1 = f1.getNew(null); 
		Session s2 = f1.getNew(null); 

		assertFalse(s1.getId().equals(s2.getId()));
	}

	@Test
	public void idsFromDifferentFactoryShallBeDifferent() {
		SessionFactory f1 = new SessionFactory();
		SessionFactory f2 = new SessionFactory();

		Session s1 = f1.getNew(null); 
		Session s2 = f2.getNew(null); 

		assertFalse(s1.getId().equals(s2.getId()));
	}
}
