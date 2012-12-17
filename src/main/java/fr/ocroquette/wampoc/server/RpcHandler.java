package fr.ocroquette.wampoc.server;

/***
 * Classes implementing this interface can handle RPC call that are received by the server.
 *
 */
public abstract class RpcHandler {
	
	public abstract void execute(RpcCall rpcCall);
	
}
