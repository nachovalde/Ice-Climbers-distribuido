package cl.uchile.dcc.cc5303;

import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements IClient{

	private static IPublicObject objeto;

	
	public Client() throws RemoteException{
		super();
	}

	public void migrate(String ip) throws RemoteException{
		try {
			IPublicObject po = (IPublicObject)Naming.lookup(ip);
			this.objeto=po;
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		
		try {
			String ip = args[0];
			System.setProperty("java.rmi.server.hostname", ip); 
			System.out.println(Server.getURL(ip));
			objeto = (IPublicObject) Naming.lookup(Server.getURL(ip));
			Client c = new Client();
			objeto.addClient(c);
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
				int res=0;
				int reply = JOptionPane.showConfirmDialog(null, "¿Desea jugar una Revancha?", "Revancha", JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					res=1;
				}
				else {
					res=-1;
				}
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
