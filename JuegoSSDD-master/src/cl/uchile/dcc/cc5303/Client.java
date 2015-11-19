package cl.uchile.dcc.cc5303;

import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements IClient{

	private static final long serialVersionUID = 1L;
	private IPublicObject objeto;

	public boolean migrated;

	public boolean getMigrated() {
		return migrated;
	}

	public void setMigrated(boolean hasMigrated) {
		this.migrated = hasMigrated;
	}

	private int id;

	
	public Client() throws RemoteException{
		super();
		this.migrated = false;
	}

	public void migrate(String url) throws RemoteException{
		try {
			System.out.println("Cambiando public object al de la url: " + url);
			IPublicObject po = (IPublicObject)Naming.lookup(url);
			po.setName("hola");
			this.setObjeto(po);
			migrated = true;
			System.out.println("Se cambio la referencia");
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	public int getId() throws RemoteException{
		return id;
	}

	public void setId(int id) throws RemoteException{
		this.id = id;
	}
	
	public IPublicObject getObjeto() throws RemoteException{
		return objeto;
	}

	public void setObjeto(IPublicObject obj) throws RemoteException{
		this.objeto = obj;
	}

	public static void main(String[] args) throws InterruptedException {
		
		try {
			String ip = args[0];
			System.setProperty("java.rmi.server.hostname", ip); 
			System.out.println(Server.getURL(ip));
			IPublicObject objeto = (IPublicObject) Naming.lookup(Server.getURL(ip));
			int id = objeto.createPlayer();
			System.out.println("player id: " + id);
			IClient c = (IClient) Naming.lookup(Server.getURL(ip) + "client/" + id);
			c.setId(id);
			c.setObjeto(objeto);
			objeto.addClient(c);
			
			while(!objeto.isReady()){}
			
			while(objeto.getAllPlay()){
				MainThreadClient m = new MainThreadClient(c);
				m.start();
				
				while(m.isAlive()){}
				while(!c.getObjeto().gameOver()){}
				objeto = c.getObjeto();
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
				if(!objeto.getAllPlay())
					break;
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

	@Override
	public IClient makeClone() throws RemoteException {
		Client newClient = new Client();
		newClient.id = this.id;
		return (IClient)newClient;
	}
}
