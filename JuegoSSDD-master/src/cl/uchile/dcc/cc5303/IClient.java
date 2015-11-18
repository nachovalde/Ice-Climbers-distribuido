package cl.uchile.dcc.cc5303;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by luism on 08-11-15.
 */
public interface IClient extends Remote {
   void migrate(String ip) throws RemoteException;

   IClient makeClone() throws RemoteException;

   int getId() throws RemoteException;

   void setId(int id) throws  RemoteException;
   
   public IPublicObject getObjeto() throws RemoteException;

	public void setObjeto(IPublicObject obj) throws RemoteException;

	public boolean getMigrated() throws RemoteException;

	public void setMigrated(boolean hasMigrated) throws RemoteException;
}
