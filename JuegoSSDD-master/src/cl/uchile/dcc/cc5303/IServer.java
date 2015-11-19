package cl.uchile.dcc.cc5303;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IServer extends Remote {
	
	String getIp() throws RemoteException;
	ArrayList<IServer> addServerToNeightbours(IServer newServer) throws RemoteException;
	void netRegister(IServer server, IServer neighbour) throws RemoteException;
	double getLoad() throws RemoteException;
	IServer minLoadServer() throws RemoteException;
	void setServers(ArrayList<IServer> nbs) throws RemoteException;
	ArrayList<IServer> getServers() throws RemoteException;
	boolean addServer(IServer newServer) throws RemoteException;
	boolean setPublicObjects(IPublicObject po) throws RemoteException;
	void initAll() throws RemoteException;
	void setAllPlayPO(boolean t) throws RemoteException;
	boolean getAllPlayPO() throws RemoteException;
	void setReadyPO(boolean t) throws RemoteException;
	void waitResponsesPO() throws RemoteException;
	boolean responseAllPlayerPO() throws RemoteException;

	IPublicObject getPO() throws RemoteException;

	boolean getMigrated() throws RemoteException;

	void setMigrated(boolean b) throws RemoteException;

	IPublicObject getObjeto() throws RemoteException;
}
