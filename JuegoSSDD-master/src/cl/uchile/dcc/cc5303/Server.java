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
	
	private IPublicObject po;
	
	public String ip;

	boolean migrated;
	
	public String url;
	
	public ArrayList<IServer> servers;
	public ArrayList<IClient> clients;
	
	public static String getURL(String ip){
		return "rmi://"+ ip +":" + port + "/" + gameName;
	}
	public Server(String ip, int lifes, int numberOfPlayers) throws RemoteException {
		migrated = false;
		this.ip = ip;
		this.url = "rmi://"+ ip +":" + port + "/";
		this.servers = new ArrayList<IServer>();
		this.clients= new ArrayList<IClient>();
		for (int i = 0; i < numberOfPlayers; i++) {
			Client c=new Client();
			c.setId(i);
			clients.add(c);
		}
		this.po = new PublicObject(lifes, numberOfPlayers);
	}
	
	public Server(String ip, int lifes, int numberOfPlayers, String ipVecino) throws RemoteException, MalformedURLException, NotBoundException{
		this(ip, lifes, numberOfPlayers);
		migrated = false;
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
			for (IClient c : s.clients){
				System.setProperty("java.rmi.server.hostname", ip);
				Naming.rebind(Server.getURL(s.getIp())+"client/"+c.getId(),c);				
			}
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
							try {
								if (load >0.70 && server.servers.size()>0 && (server.po.getPlayers().size() == server.clients.size())){
									server.migrate();
									break;
								}
							} catch (RemoteException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}.setServer(s)).start();

			try {
				System.setProperty("java.rmi.server.hostname", ip); 
				Naming.rebind(Server.getURL(s.ip), s.po);
				System.out.println("Objeto publicado en "+s.url);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
						
			System.out.println("Esperando " + numberOfPlayers + " jugadores para empezar...");
			while(s.po.getPlayers().size() != numberOfPlayers) {
				try {
					Thread.sleep(1000 / 60);
				} catch (InterruptedException ex) {
				}
			}
			s.setAllPlayPO(true);

			while(s.getAllPlayPO()){
				System.out.println("Iniciando Juego de SSDD...");
				
				//s.po.init();
				s.initAll();

			    MainThreadServer m = new MainThreadServer(s);
			    m.start();

			    s.setReadyPO(true);
			    while(m.isAlive()){}
			    s.setReadyPO(false);
				System.out.println("Esperando Respuesta");
			    s.waitResponsesPO();
			    
			    s.setAllPlayPO(s.responseAllPlayerPO());
			}
			s.setReadyPO(true);
		        
		}else {
			System.out.println("Deben ir tres argumentos: IP, NumeroDeVidas y NumeroDeJugadores");
		}
	}
	public void initAll() throws RemoteException{
		po.init();
	}

	public void setAllPlayPO(boolean t) throws RemoteException{
		po.setAllPlay(t);
	}
	public void setReadyPO(boolean t) throws RemoteException{
		po.setReady(t);
	}
	public boolean getAllPlayPO() throws RemoteException{
		return po.getAllPlay();
	}
	public void waitResponsesPO() throws RemoteException{
		po.waitResponses();
	}
	public boolean responseAllPlayerPO() throws RemoteException{
		return po.responseAllPlayer();
	}

	@Override
	public IPublicObject getPO() throws RemoteException {
		return po;
	}

	@Override
	public boolean getMigrated() throws RemoteException {
		return migrated;
	}

	@Override
	public void setMigrated(boolean b) throws RemoteException {
		migrated = b;
	}

	@Override
	public IPublicObject getObjeto() throws RemoteException {
		return po;
	}

	public void migrate() {
		IServer newServer = null;
		try {
			newServer = minLoadServer();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		try {
			System.out.println("migrando a server: " + newServer.getIp());
			newServer.setPublicObjects(po.makeClone());
			po.migrate(newServer, this.getIp());
			po = new PublicObject(5, clients.size());
			migrated = true;
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
		IServer resultado = this;
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
			System.setProperty("java.rmi.server.hostname", ip); 
			Naming.rebind(Server.getURL(this.ip), this.po);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
