package cl.uchile.dcc.cc5303;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by luism on 08-11-15.
 */
public interface IClient extends Remote {
   void migrate(String ip) throws RemoteException;
}
