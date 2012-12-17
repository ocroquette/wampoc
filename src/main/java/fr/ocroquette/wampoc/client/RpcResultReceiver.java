package fr.ocroquette.wampoc.client;

public abstract class RpcResultReceiver {

	abstract void onSuccess();

	abstract void onError();

}
