package cl.uchile.dcc.cc5303;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IPublicObject extends Remote{

	int createPlayer() throws RemoteException;

	boolean isReady() throws RemoteException;

	ArrayList<Player> getPublicPlayers() throws RemoteException;

	Bench[] getPublicBench() throws RemoteException;

}
