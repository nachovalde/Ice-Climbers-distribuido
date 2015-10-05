package cl.uchile.dcc.cc5303;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class Server {
	
	private static String port = "1099";
	
	public static String urlServer = getURL();
	
	private static String getURL(){
		String ip = "";
		ip = "localhost";
		return "rmi://"+ ip +":" + port + "/bancoServer";
	}
	
	public static void main(String[] args) throws RemoteException {
		IPublicObject po = new PublicObject(5);
		try {
			Naming.rebind(urlServer, po);
			System.out.println("Objeto publicado en "+urlServer);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Esperando jugadores para empezar...");	
		while(po.getPlayers().size()!=2){
			try {
                Thread.sleep(1000/60);
            } catch (InterruptedException ex) {

            }
		}
		po.setReady(true);
		System.out.println("Iniciando Juego de SSDD...");
        MainThreadServer m = new MainThreadServer(po);
        m.start();
	}	
}
