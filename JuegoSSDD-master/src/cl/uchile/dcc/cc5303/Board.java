package cl.uchile.dcc.cc5303;

import java.awt.*;

/**
 * Created by sebablasko on 9/11/15.
 */
public class Board extends Canvas {

    public int width, height;

    public Player p1, p2;
    public Bench[] bases;
    public Image img;
    public Graphics buffer;
    public int levels = 11;

    public Board(int w, int h){
        this.width = w;
        this.height = h;
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

        buffer.setColor(Color.red);
        p1.draw(buffer);
        buffer.setColor(Color.blue);
        p2.draw(buffer);

        buffer.setColor(Color.white);
        for(Bench base : bases){
            base.draw(buffer);
        }

        g.drawImage(img, 0, 0, null);
    }

    @Override
    public String toString(){
        String ret = "Tablero: dimensions " + this.width + "x" + this.height + "\n";
        ret += p1.toString() + "\n" + p2.toString();
        return ret;
    }

    public void setBenches(Bench[] benches) {
        this.bases = benches;
    }

    public void levelsDown() {
        for(Bench base: bases) {
            base.levelDown(levels);
        }
        p1.levelDown();
        p2.levelDown();
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
		
		if( p1.loseLife(height) )
		{
			if( Math.abs(p1.posX - aux.posX) < Math.abs(p1.posX - (aux.posX+aux.w) ) )
			{
				p1.reubicar(aux.posX + 2, aux.posY - 20);
			}else
			{
				p1.reubicar(aux.posX + aux.w - 10, aux.posY - 20);
			}
		}
		
		if( p2.loseLife(height) )
		{
			if( Math.abs(p2.posX - aux.posX) < Math.abs(p2.posX - (aux.posX + aux.w)) )
			{
				p2.reubicar(aux.posX + 2, aux.posY - 20);
			}else
			{
				p2.reubicar(aux.posX + aux.w - 10, aux.posY - 20);
			}
		}
		return !(p1.stillLife() &&  p2.stillLife());
		
	}




}
