package cl.uchile.dcc.cc5303;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by luism on 03-10-15.
 */
public class PublicObject extends UnicastRemoteObject implements IPublicObject {
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Player> players;
    private boolean isReady;
    private final static int WIDTH = 800, HEIGHT = 600;
    private ArrayList<Color> colors;
    private  ArrayList<Integer> positions;
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

    public PublicObject(int lifes) throws RemoteException{
        setPlayers(new ArrayList<Player>());
        setReady(false);
        this.lifes=lifes;
        colors=new ArrayList<>(4);
        colors.add(Color.blue);
        colors.add(Color.green);
        colors.add(Color.orange);
        colors.add(Color.red);
        positions=new ArrayList<>(4);
        positions.add(WIDTH/3);
        positions.add(2*WIDTH/3);
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
		// TODO Auto-generated method stub
        Color color=colors.get(0);
        int position=positions.get(0);
        Player p=new Player(position, 550, lifes, KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, color);
        colors.remove(0);
        positions.remove(0);
        getPlayers().add(p);
		return getPlayers().size()-1;
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
        boolean res = true;
        for(Player p: players){
            res = res && p.stillLife();
        }
        //Arreglar: se termina si un solo jugador perdio
        return !res;
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
}
