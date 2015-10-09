package cl.uchile.dcc.cc5303;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
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
	private boolean isReady;
    private final static int WIDTH = 800, HEIGHT = 600;
    
    private int lifes;
    private int levels = 11;
    
    private int revanchas = 0;
    private boolean estado_revancha = true;

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
        setReady(false);
        this.lifes=lifes;
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
		// TODO Auto-generated method stub
        return isReady;
		
	}

	public int createPlayer() throws RemoteException{
        initPlayer();
        int id=getPlayers().size()-1;
        players.get(id).setId(id);
		return id;
	}
	
	private void initPlayer() throws RemoteException{
		Color color = colors[lastPlayer];
        int position = positions[lastPlayer];
        Player p = new Player(position, 550, lifes, KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, color);
        incrementPlayer();
        getPlayers().add(p);
	}

    private void incrementPlayer() {
    	lastPlayer++;
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
                if( Math.abs(p.posX - aux.posX) < Math.abs(p.posX - (aux.posX+aux.w) ) )
                {
                    p.reubicar(aux.posX + 2, aux.posY - 20);
                }else
                {
                    p.reubicar(aux.posX + aux.w - 10, aux.posY - 20);
                }
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
    public void displayFinalScores() throws RemoteException {
        ArrayList<Player> sortedPlayers = (ArrayList<Player>) players.clone();
        Collections.sort(sortedPlayers);
        for (int i = 0; i < sortedPlayers.size(); i++) {
            System.out.println("Jugador "+ sortedPlayers.get(i).getId()+" Puntaje: " + sortedPlayers.get(i).getScore());
        }
    }

    public ArrayList<Player> getPlayers() throws RemoteException{
		return players;
	}

	public void setPlayers(ArrayList<Player> players) throws RemoteException{
		this.players = players;
	}

	public void setReady(boolean isReady) throws RemoteException{
		this.isReady = isReady;
	}

	public Bench[] getBenches() throws RemoteException{
		return benches;
	}

	public void setBenches(Bench[] benches) throws RemoteException{
		this.benches = benches;
	}

	@Override
	public void responseRematch(int res) throws RemoteException {
		if(res == -1)
			estado_revancha = false;
		revanchas++;
	}

	@Override
	public boolean isReadyRematch() throws RemoteException {
		return players.size() == revanchas;
	}

	@Override
	public boolean rematch() throws RemoteException {
		return estado_revancha;
	}

	@Override
	public int getRevanchas() throws RemoteException {
		return revanchas;
	}

	@Override
	public void init() throws RemoteException {
		lastPlayer = 0;
		setPlayers(new ArrayList<Player>());
		for (int i = 0; i < numberOfPlayers; i++) {
			initPlayer();
		}
	}


}
