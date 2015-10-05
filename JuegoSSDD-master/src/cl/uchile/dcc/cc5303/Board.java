package cl.uchile.dcc.cc5303;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by sebablasko on 9/11/15.
 */
public class Board extends Canvas implements Remote {

    public int width, height;

    public ArrayList<Player> players;
    public Bench[] bases;
    public Image img;
    public Graphics buffer;
    public int levels = 11;
    IPublicObject publicObject;

    public Board(int w, int h, IPublicObject publicObject){
        this.width = w;
        this.height = h;
        this.publicObject=publicObject;
        players=new ArrayList<>();
    }

    @Override
    public void update(Graphics g) { paint(g); }

    @Override
    public void paint(Graphics g){
        if(buffer==null){
            img = createImage(getWidth(),getHeight() );
            buffer = img.getGraphics();
        }

        buffer.setColor(Color.black);
        buffer.fillRect(0, 0, getWidth(), getHeight());;
        
        //Arreglar hardcode
        for(Player p: players){
	        buffer.setColor(p.getColor());
	        p.draw(buffer);
        }

        buffer.setColor(Color.white);
        for(Bench base : bases){
            base.draw(buffer);
        }

        g.drawImage(img, 0, 0, null);
    }

    @Override
    public String toString(){
        String ret = "Tablero: dimensions " + this.width + "x" + this.height + "\n";
        //Arreglar hardcode
        ret += players.get(0).toString() + "\n" + players.get(1).toString();
        return ret;
    }

    public void setBenches(Bench[] benches) {
        this.bases = benches;
    }

    public void levelsDown() {
        for(Bench base: bases) {
            base.levelDown(levels);
        }
        for(Player p : players ){
        	p.levelDown();
        }
    }

	public boolean playerDie() {
		//mejorar busqueda de bases de lvl 0
		Bench aux = null;
		for (int i = 0; i < bases.length; i++) {
			if(bases[i].level == 0)
			{
				aux = bases[i];
				break;
			}
		}
		
		for(Player p : players){
			if( p.loseLife(height) )
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
    public void addPlayer(Player player) {
        players.add(player);
	}

    public void setPlayers(ArrayList<Player> players){
        this.players.clear();
        this.players.addAll(players);
    }
}
