package cl.uchile.dcc.cc5303;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class Server {
	
	private static String port = "1099";
	
	public static String urlServer = getURL();
	
	private static String getURL(){
		String ip = "";
		try {
			ip = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "rmi://"+ ip +":" + port + "/bancoServer";
	}
	
	public static void main(String[] args) throws RemoteException {
		PublicObject po = new PublicObject(5);
		try {
			Naming.rebind(urlServer, (Remote) po);
			System.out.println("Objeto publicado en "+urlServer);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(po.players.size()!=2){
			System.out.println("Esperando jugadores para empezar...");
			try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException ex) {

            }
		}
		po.isReady = true;
		System.out.println("Iniciando Juego de SSDD...");
        MainThreadServer m = new MainThreadServer(po);
        m.start();
	}	
}
