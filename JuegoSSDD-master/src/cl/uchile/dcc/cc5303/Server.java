package cl.uchile.dcc.cc5303;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


public class Server extends UnicastRemoteObject implements IServer{
	
	private static final long serialVersionUID = 1L;

	private static String port = "1099";
	
	public static String gameName = "IceClimbers";	
	
	public String ip;
	
	public String url;
	
	public ArrayList<IServer> servers;
	
	public static String getURL(String ip){
		return "rmi://"+ ip +":" + port + "/" + gameName;
	}
	
	public Server(String ip) throws RemoteException {
		this.ip = ip;
		this.url = "rmi://"+ ip +":" + port + "/";
		this.servers = new ArrayList<IServer>();
	}
	
	public Server(int numPlayers, String ip, String ipVecino) throws RemoteException, MalformedURLException, NotBoundException{
		this(ip);
		String urlVecino = "rmi://"+ ipVecino +":" + port + "/server";
		IServer nb = (IServer)Naming.lookup(urlVecino); 
		servers.add(nb);
		System.out.println("Registrando este servidor en la red...");
		netRegister(this, nb);
	}
	
	public static void main(String[] args) throws RemoteException, MalformedURLException {
		if(args.length == 5)
		{
			String ip = args[0];
			Server s = new Server(ip);		
			int lifes = new Integer(args[2]);
			int numberOfPlayers=new Integer(args[4]);
			IPublicObject po = new PublicObject(lifes, numberOfPlayers);
			try {
				System.setProperty("java.rmi.server.hostname", ip); 
				Naming.rebind(s.url, po);
				System.out.println("Objeto publicado en "+s.url);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Naming.rebind(s.url + "server", s);
						
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

	@Override
	public String getIp() throws RemoteException {
		return ip;
	}

	@Override
	public ArrayList<IServer> addServer(IServer newServer) throws RemoteException {
		servers.add(newServer);
		return servers;
	}

	@Override
	public void netRegister(IServer server, IServer neighbour) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getLoad() throws RemoteException {
		return 0;
	}

	@Override
	public IServer minLoadServer(ArrayList<IServer> servers) throws RemoteException {
		// TODO Auto-generated method stub
		return servers.get(0);
	}	
}
