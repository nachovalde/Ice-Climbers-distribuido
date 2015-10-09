package cl.uchile.dcc.cc5303;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {
	

	public static void main(String[] args) throws InterruptedException {
		
		try {
			System.setProperty("java.rmi.server.hostname", Server.ip); 
			System.out.println(Server.urlServer);
			IPublicObject objeto = (IPublicObject) Naming.lookup(Server.urlServer);
			int id = objeto.createPlayer();
			System.out.println("player id: " + id);
			while(!objeto.isReady()){
				
			}
			while(true){
				MainThreadClient m = new MainThreadClient(objeto, id);
				m.start();
				while(m.isAlive()){}
				
				System.out.println("\nRevancha?: Si:1 No:-1");
				Scanner sc = new Scanner(System.in);
				int res = sc.nextInt();
				objeto.responseRematch(res);
				while(!objeto.isReadyRematch()){}
				if(!objeto.rematch()){
		        	break;
		        }
			}
			
			
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
