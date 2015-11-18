package cl.uchile.dcc.cc5303;

import java.awt.*;
import java.rmi.Remote;
import java.util.ArrayList;

/**
 * Created by sebablasko on 9/11/15.
 */
public class Board extends Canvas implements Remote {

	private static final long serialVersionUID = 1L;

	public int width, height;

    public ArrayList<Player> players;
    public Bench[] bases;
    public Image img;
    public Graphics buffer;
    public int levels = 11;

    public Board(int w, int h){
        this.width = w;
        this.height = h;
        players=new ArrayList<>();
    }

    public void setBenches(Bench[] benches){
        this.bases=benches;
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
        	if(!p.stillLife())
        		continue;
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

    public void addPlayer(Player player) {
        players.add(player);
	}

    public void setPlayers(ArrayList<Player> players){
        this.players.clear();
        this.players.addAll(players);
    }
}
