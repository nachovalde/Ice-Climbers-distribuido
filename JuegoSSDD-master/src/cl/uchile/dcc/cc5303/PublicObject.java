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
	public ArrayList<Player> players;
    public boolean isReady;
    private final static int WIDTH = 800, HEIGHT = 600;
    private ArrayList<Color> colors;
    private  ArrayList<Integer> positions;
    private int lifes;
    private int levels = 11;

    public Bench[] benches = {
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
        players=new ArrayList<>();
        isReady=false;
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
        return players;
    }

    public void setPublicPlayers(ArrayList<Player> publicPlayers){
        this.players = publicPlayers;
    }

    public Bench[] getPublicBench() throws RemoteException{
        return benches;
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
        players.add(p);
		return players.size()-1;
	}

    public void checkCollisionAllPlayers(){
        for (Player p : players){
            this.checkCollision(p,players.indexOf(p));
        }
    }

    private void checkCollision(Player p, int i) {
        for (int j = i; j < players.size(); j++) {
            if (p.collideWithPlayer(players.get(j))){
                p.rebounding(players.get(j));
            }
        }
    }

	public void levelsDown() {
        for(Bench base: benches) {
            base.levelDown(levels);
        }
        for(Player p : players ){
        	p.levelDown();
        }
	}
	
    public Player getPlayerbyId(int id){
        return players.get(id);
    }
}
