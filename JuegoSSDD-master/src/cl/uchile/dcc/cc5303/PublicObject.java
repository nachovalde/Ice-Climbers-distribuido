package cl.uchile.dcc.cc5303;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by luism on 03-10-15.
 */
public class PublicObject extends UnicastRemoteObject implements IPublicObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Player> players;
	private int lastPlayer = 0; 
    private int numberOfPlayers;
    private final static int WIDTH = 800, HEIGHT = 600;
    
    private ArrayList<IClient> clients;
    
    private boolean isReady;
    private boolean AllPlay;
    
    private boolean[] responses;
    private int countResponse;
    
    private int lifes;
    private int levels = 11;
    
    private Bench[] benches = {
            new Bench(200, 200, 0),
            new Bench(450, 200, 0),
            new Bench(100, 200, 1),
            new Bench(400, 200, 1),
            new Bench(300, 100, 2),
            new Bench(600, 200, 2),
            new Bench(150, 200, 3),
            new Bench(700, 100, 3),
            new Bench(75, 200, 4),
            new Bench(350, 350, 4),
            new Bench(200, 200, 5),
            new Bench(400, 400, 6),
            new Bench(200, 400, 7),
            new Bench(150, 200, 8),
            new Bench(75, 100, 9),
            new Bench(50, 100, 10)
    };
    
    private Color[] colors = {
    		Color.blue,
    		Color.green,
    		Color.orange,
    		Color.red
    		};
    
    private  int[] positions = {WIDTH/3, WIDTH/3-20, 2*WIDTH/3, 2*WIDTH/3+20};

    public PublicObject(int lifes, int numberOfPlayers) throws RemoteException{
        setPlayers(new ArrayList<Player>());
        this.numberOfPlayers = numberOfPlayers;
        this.lifes=lifes;
        setReady(false);
        responses = new boolean[numberOfPlayers];
        countResponse = 0;
        clients = new ArrayList<IClient>(numberOfPlayers);
        
    }

    public ArrayList<Player> getPublicPlayers() throws RemoteException{
        return getPlayers();
    }

    public void setPublicPlayers(ArrayList<Player> publicPlayers) throws RemoteException{
        this.setPlayers(publicPlayers);
    }

    public Bench[] getPublicBench() throws RemoteException{
        return getBenches();
    }

	public boolean isReady() throws RemoteException{
        return isReady;
	}

	public int createPlayer() throws RemoteException{
        initPlayer();        
		return players.get(getPlayers().size()-1).getId();
	}
	
	private void initPlayer() throws RemoteException{
		Color color = colors[lastPlayer];
        int position = positions[lastPlayer];
        Player p = new Player(position, 550, lifes, KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, color);
        lastPlayer++;
        p.setId(players.size());
        if (players.size()<numberOfPlayers){
            players.add(p);
        }else {
            IndexOutOfBoundsException exception = new IndexOutOfBoundsException();
            throw exception;
        }

	}

	public void checkCollisionAllPlayers() throws RemoteException{
        for (Player p : getPlayers()){
            this.checkCollision(p,getPlayers().indexOf(p));
        }
    }

    private void checkCollision(Player p, int i) throws RemoteException{
        for (int j = i; j < getPlayers().size(); j++) {
            if (p.collideWithPlayer(getPlayers().get(j))){
                p.rebounding(getPlayers().get(j));
            }
        }
    }

	public void levelsDown() throws RemoteException{
        for(Bench base: getBenches()) {
            base.levelDown(levels);
        }
        for(Player p : getPlayers() ){
        	p.levelDown();
        }
	}
	
    public Player getPlayerbyId(int id) throws RemoteException{
        return getPlayers().get(id);
    }

    @Override
    public void updatePlayer(int id, Player p) throws  RemoteException{
        players.set(id, p);
    }
    public static int randInt(int min, int max) {
    	return min + (int)(Math.random()*((max - min) + 1));
    }
    @Override
    public boolean playerDie() throws RemoteException {
        //mejorar busqueda de bases de lvl 0
        Bench aux = null;
        for (int i = 0; i < benches.length; i++) {
            if(benches[i].level == 0)
            {
                aux = benches[i];
                break;
            }
        }
        for(Player p : players){
            if( p.loseLife(HEIGHT) )
            {
            	p.reubicar(randInt(aux.posX,aux.posX+aux.w),aux.posY - 20);
            }
        }
        int res = 0;
        for(Player p: players){
            if(p.stillLife())
            	res++;
        }
        //Arreglar: se termina si un solo jugador perdio
        return res==1;
    }

    @Override
    public String displayFinalScores() throws RemoteException {
        ArrayList<Player> sortedPlayers = (ArrayList<Player>) players.clone();
        Collections.sort(sortedPlayers);
        String result = "";
        for (int i = 0; i < sortedPlayers.size(); i++) {
            result+="Jugador "+ sortedPlayers.get(i).getId()+" Puntaje: " + sortedPlayers.get(i).getScore() + "\n";
        }
        return result;
    }

    public ArrayList<Player> getPlayers() throws RemoteException{
		return players;
	}

	public void setPlayers(ArrayList<Player> players) throws RemoteException{
		this.players = players;
	}
	
	@Override
	public void setLastPlayer(int lastPlayer) throws RemoteException {
		this.lastPlayer = lastPlayer;
	}

	public void setReady(boolean isReady) throws RemoteException{
		this.isReady = isReady;
	}
	
	public boolean getReady() throws RemoteException{
		return this.isReady;
	}

	public Bench[] getBenches() throws RemoteException{
		return benches;
	}

	public void setBenches(Bench[] benches) throws RemoteException{
		this.benches = benches;
	}

	@Override
	public void init() throws RemoteException {
		lastPlayer = 0;
		setPlayers(new ArrayList<Player>());
		for (int i = 0; i < numberOfPlayers; i++) {
			initPlayer();
		}
		countResponse = 0;
	}

	@Override
	public boolean responseAllPlayer() {
		boolean res = true;
		for (int i = 0; i < responses.length; i++) {
			res = res && responses[i];
		}
		return res;
	}

	@Override
	public void waitResponses() throws RemoteException{
		while(countResponse < numberOfPlayers){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setAllPlay(boolean arg) throws RemoteException {
		this.AllPlay = arg;
	}

	@Override
	public boolean getAllPlay() throws RemoteException {
		return this.AllPlay;
	}

	@Override
	public void sendResponse(int id, int res) throws RemoteException {
		responses[id] = res == 1 ? true : false;
		countResponse++;
	}

	@Override
	public boolean gameOver() throws RemoteException {
		boolean flag = true;
		for(Player player:players){
			flag &= !player.stillLife();
		}
		return flag;
	}

	@Override
	public void addClient(IClient c) throws RemoteException{
		clients.add(c);
	}

	@Override
	public void migrate(IServer newServer, String ipOld) throws RemoteException {
		for(IClient client:clients){
			try {
				IClient client2 = (IClient) Naming.lookup(Server.getURL(ipOld)+"client/"+client.getId());
				client2.migrate(Server.getURL(newServer.getIp()));
			} catch (NotBoundException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public IPublicObject makeClone() throws RemoteException {
		PublicObject newPO = new PublicObject(this.lifes, this.numberOfPlayers);
		//Clonar Players
		ArrayList<Player> players = new ArrayList<Player>();
		for(Player player:this.players){
			players.add(player.makeClone());
		}
		newPO.setPlayers(players);
		//Update LastPlayer
		newPO.setLastPlayer(this.lastPlayer);
		//Clonar Benches
		Bench[] benches = new Bench[this.benches.length];
		for (int i = 0; i < benches.length; i++) {
			benches[i] = this.benches[i].makeClone();
		}
		newPO.setBenches(benches);

	    newPO.isReady = this.isReady;
	    newPO.AllPlay = this.AllPlay;
	    /* Clonar Clientes (?)
		ArrayList<IClient> oldClients = new ArrayList<IClient>();
		for(IClient client:clients){
			oldClients.add(client.makeClone());
		}*/
		newPO.clients = this.clients ;
	    
		return (IPublicObject)newPO;
	}



}
