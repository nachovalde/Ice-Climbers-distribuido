package cl.uchile.dcc.cc5303;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {
	

	public static void main(String[] args) throws InterruptedException {
		
		try {
			String ip = args[0];
			System.setProperty("java.rmi.server.hostname", ip); 
			System.out.println(Server.getURL(ip));
			IPublicObject objeto = (IPublicObject) Naming.lookup(Server.getURL(ip));
			int id = objeto.createPlayer();
			System.out.println("player id: " + id);
			
			while(!objeto.isReady()){}
			
			while(objeto.getAllPlay()){
				MainThreadClient m = new MainThreadClient(objeto, id);
				m.start();
				
				while(m.isAlive()){}
				while(!objeto.gameOver()){}
				System.out.println("Resultados Finales:");
		        System.out.println(objeto.displayFinalScores());
				Scanner sc = new Scanner(System.in);
				System.out.println("Revancha? Si:1 No:-1");
				int res = sc.nextInt();
				objeto.sendResponse(id, res);
				System.out.println("esperando otras respuestas");
				objeto.waitResponses();
				while(!objeto.isReady()){}
				sc.close();
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
