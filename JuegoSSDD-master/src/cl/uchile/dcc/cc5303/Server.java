package cl.uchile.dcc.cc5303;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class Server {
	
	private static String port = "1099";
	public static String ip = "172.20.10.4";
	public static String urlServer = getURL();
	
	private static String getURL(){
		return "rmi://"+ ip +":" + port + "/IceClimbers";
	}
	
	public static void main(String[] args) throws RemoteException {
		if(args.length == 4)
		{
			int lifes = new Integer(args[1]);
			int numberOfPlayers=new Integer(args[3]);
			IPublicObject po = new PublicObject(lifes, numberOfPlayers);
			try {
				System.setProperty("java.rmi.server.hostname", ip); 
				Naming.rebind(urlServer, po);
				System.out.println("Objeto publicado en "+urlServer);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			System.out.println("Esperando " + numberOfPlayers + " jugadores para empezar...");
			while(po.getPlayers().size()!=numberOfPlayers){
				try {
	                Thread.sleep(1000/60);
	            } catch (InterruptedException ex) {
	
	            }
			}
			po.setReady(true);
			while(true){
				System.out.println("Iniciando Juego de SSDD...");
		        MainThreadServer m = new MainThreadServer(po);
		        m.start();
		        while(m.isAlive()){}
		        po.init();
		        while(!po.isReadyRematch()){}
		        if(!po.rematch()){
		        	break;
		        }
			}
		}else
		{
			System.out.println("Deben ir dos argumentos: -N NumeroDeVidas y -n NumeroDeJugadores");
		}
	}	
}
