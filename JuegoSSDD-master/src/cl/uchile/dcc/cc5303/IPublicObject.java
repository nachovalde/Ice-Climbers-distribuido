package cl.uchile.dcc.cc5303;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IPublicObject extends Remote{


	int createPlayer() throws RemoteException;

	boolean isReady() throws RemoteException;

	ArrayList<Player> getPublicPlayers() throws RemoteException;

	Bench[] getPublicBench() throws RemoteException;
	
	ArrayList<Player> getPlayers() throws RemoteException;
	
	void setReady(boolean isReady) throws RemoteException;
	
	boolean getReady() throws RemoteException;
	
	void setAllPlay(boolean isReady) throws RemoteException;
	
	boolean getAllPlay() throws RemoteException;

	void checkCollisionAllPlayers() throws RemoteException;

	void levelsDown() throws RemoteException;

	Player getPlayerbyId(int id) throws RemoteException;

	void updatePlayer(int id, Player p) throws RemoteException;

	boolean playerDie() throws RemoteException;

	void displayFinalScores() throws RemoteException;

	void init() throws RemoteException;

	boolean responseAllPlayer() throws RemoteException;

	void waitResponses() throws RemoteException;

	void sendResponse(int id, int res) throws RemoteException;

}
