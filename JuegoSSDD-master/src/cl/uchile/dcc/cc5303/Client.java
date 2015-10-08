package cl.uchile.dcc.cc5303;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
	

	public static void main(String[] args) {
		
		try {
			System.setProperty("java.rmi.server.hostname", Server.ip); 
			System.out.println(Server.urlServer);
			IPublicObject objeto = (IPublicObject) Naming.lookup(Server.urlServer);
			int id = objeto.createPlayer();
			System.out.println("player id: " + id);
			while(!objeto.isReady()){
				
			}
			
			MainThreadClient m = new MainThreadClient(objeto, id);
			m.start();	
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IndexOutOfBoundsException e){
			System.out.println("No se puede agregar mas jugadores!");
			System.exit(0);
		}
		
	}

}
