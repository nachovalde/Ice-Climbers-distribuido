package cl.uchile.dcc.cc5303;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class Server {
	
	private static String port = "1099";
	
	public static String url = "IceClimbers";
	
	public static String getURL(String ip){
		return "rmi://"+ ip +":" + port + "/" + url;
	}
	
	public static void main(String[] args) throws RemoteException {
		if(args.length == 5)
		{
			String ip = args[0];
			int lifes = new Integer(args[2]);
			int numberOfPlayers=new Integer(args[4]);
			IPublicObject po = new PublicObject(lifes, numberOfPlayers);
			try {
				System.setProperty("java.rmi.server.hostname", ip); 
				Naming.rebind(getURL(ip), po);
				System.out.println("Objeto publicado en "+getURL(ip));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			System.out.println("Esperando " + numberOfPlayers + " jugadores para empezar...");
			while(po.getPlayers().size() != numberOfPlayers){
				try {
	                Thread.sleep(1000/60);
	            } catch (InterruptedException ex) {}
			}
			
			
			po.setAllPlay(true);
			while(po.getAllPlay()){
				System.out.println("Iniciando Juego de SSDD...");
				
				po.init();
				
			    MainThreadServer m = new MainThreadServer(po);
			    m.start();
			    po.setReady(true);
			    while(m.isAlive()){}
			    po.setReady(false);
			    
			    System.out.println("Esperando Respuesta");
			    po.waitResponses();
			    
			    po.setAllPlay(po.responseAllPlayer());
			}
			po.setReady(true);
		        
		}else
		{
			System.out.println("Deben ir tres argumentos: IP, -N NumeroDeVidas y -n NumeroDeJugadores");
		}
	}	
}
