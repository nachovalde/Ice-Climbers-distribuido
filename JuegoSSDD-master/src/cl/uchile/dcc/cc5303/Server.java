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
	
	public static IPublicObject po;
	
	public static String getURL(String ip){
		return "rmi://"+ ip +":" + port + "/" + gameName;
	}
	public Server(String ip, int lifes, int numberOfPlayers) throws RemoteException {
		this.ip = ip;
		this.url = "rmi://"+ ip +":" + port + "/";
		this.servers = new ArrayList<IServer>();
		this.po = new PublicObject(lifes, numberOfPlayers);
	}
	
	public Server(String ip, int lifes, int numberOfPlayers, String ipVecino) throws RemoteException, MalformedURLException, NotBoundException{
		this(ip, lifes, numberOfPlayers);
		String urlVecino = "rmi://"+ ipVecino +":" + port + "/server";
		IServer nb = (IServer)Naming.lookup(urlVecino); 
		this.addServer(nb);
		System.out.println("Registrando este servidor en la red...");
		netRegister(this, nb);
	}
	
	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
		if(args.length == 3 || args.length == 4)
		{
			String ip = args[0];
			Server s = null;
			int lifes = new Integer(args[1]);
			int numberOfPlayers=new Integer(args[2]);
			if (args.length==3)
				s = new Server(ip, lifes, numberOfPlayers);
			else
				s = new Server(ip, lifes, numberOfPlayers, args[3]);
			System.out.println(s.url);
			System.out.println(s.getServers());
			
			
			System.setProperty("java.rmi.server.hostname", ip); 
			Naming.rebind(s.url + "server", (IServer)s);
			
			new Thread(new Runnable() {
				Server server;
				Runnable setServer(Server server) {
					this.server=server;
					return this;
				}
				@Override
				public void run() {
						while(true){
							double load = CpuData.getCpuUsage();
							if (load >0.70){
								server.migrate();
								break;
							}
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
				}
			}.setServer(s)).start();
			try {
				System.setProperty("java.rmi.server.hostname", ip); 
				Naming.rebind(s.getURL(s.ip), po);
				System.out.println("Objeto publicado en "+s.url);
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
	
	public void migrate() {
		IServer newServer = minLoadServer();
		try {
			//TODO Aqui deberia agregar nacho las firmas correctas de los metodos de po xD
			newServer.setPublicObject(po.makeClone());
			po.migrate(newServer);
		} catch(RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getIp() throws RemoteException {
		return ip;
	}

	@Override
	public ArrayList<IServer> addServerToNeightbours(IServer newServer) throws RemoteException{
		for (IServer neighbour : servers) {
			ArrayList<IServer> nbs = neighbour.getServers();
			nbs.add(newServer);
			neighbour.setServers(nbs);
		}
		return servers;
	}

	@Override
	public void netRegister(IServer server, IServer neighbour) throws RemoteException{
		ArrayList<IServer> otherServers = neighbour.addServerToNeightbours(server);
		server.getServers().addAll(otherServers);
		neighbour.addServer(server);
	}

	@Override
	public double getLoad() throws RemoteException {
		return CpuData.getCpuUsage();
	}

	@Override
	public IServer minLoadServer() throws RemoteException {
		double min = Double.MAX_VALUE;
		IServer resultado = null;
		for(IServer s : servers){
			try{
				double load= s.getLoad();
				if(load < min){
					min = load;
					resultado = s;
				}
			} catch(RemoteException e){
				e.getStackTrace();
			}
		}
		return resultado;
	}
	@Override
	public void setServers(ArrayList<IServer> servers) throws RemoteException {
		this.servers = servers;
		
	}
	@Override
	public ArrayList<IServer> getServers() throws RemoteException {
		return servers;
	}
	
	@Override
	public String toString() {
		return ip;
	}
	@Override
	public boolean addServer(IServer newServer) throws RemoteException {
		try{
			servers.add(newServer);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	@Override
	public boolean setPublicObjects(IPublicObject po) throws RemoteException {
		try{
			this.po = po;
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
