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
			
			while(!objeto.isReady()){}
			
			while(objeto.getAllPlay()){
				MainThreadClient m = new MainThreadClient(objeto, id);
				m.start();
				
				while(m.isAlive()){}
				
				Scanner sc = new Scanner(System.in);
				System.out.println("Revancha? Si:1 No:-1");
				int res = sc.nextInt();
				objeto.sendResponse(id, res);
				System.out.println("esperando otras respuestas");
				objeto.waitResponses();
				while(!objeto.isReady()){}
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
