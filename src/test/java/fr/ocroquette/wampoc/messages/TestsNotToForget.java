package fr.ocroquette.wampoc.messages;

/***
 * Implement this class for each Message type, to avoid the given tests to get forgotten 
 *
 */
public interface TestsNotToForget {
	public abstract void constructMessage();

	public abstract void testEquals();
	
	public abstract void unserializeInvalidMessages();

}
