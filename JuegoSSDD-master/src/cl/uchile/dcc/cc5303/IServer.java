package cl.uchile.dcc.cc5303;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IServer extends Remote {
	
	String getIp() throws RemoteException;
	public ArrayList<IServer> addServer(IServer newServer) throws RemoteException;
	public void netRegister(IServer server, IServer neighbour) throws RemoteException;
	public double getLoad() throws RemoteException;
	public IServer minLoadServer(ArrayList<IServer> servers) throws RemoteException;
	void setServers(ArrayList<IServer> nbs) throws RemoteException;
	ArrayList<IServer> getServers() throws RemoteException;
}
