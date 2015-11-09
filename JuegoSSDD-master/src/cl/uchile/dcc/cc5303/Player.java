package cl.uchile.dcc.cc5303;

import java.awt.*;
import java.io.Serializable;

/**
 * Created by sebablasko on 9/11/15.
 */
public class Player implements Serializable, Comparable<Player>{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int posX;
    int posY;
    int w = 7;
    int h = 10;
    int vida;
    private int id;
    long score;
    int ButtonUp, ButtonRight, ButtonLeft;
    double speed = 0.4;
    public boolean standUp = false;
	private Color color;

    public Player(int x, int y, int vida, int ButtonUP, int ButtonRight, int ButtonLeft, Color color){
        this.posX = x;
        this.posY = y;
        this.vida = vida;
        this.score=0;
        this.ButtonUp = ButtonUP;
        this.ButtonRight = ButtonRight;
        this.ButtonLeft = ButtonLeft;
        this.color = color;
    }

    public void jump(){
        if(this.standUp)
            this.speed = -0.9;
        this.standUp = false;
    }

    public void moveRight() {
        this.posX += 2;
    }

    public void moveLeft() {
        this.posX -= 2;
    }

    public void update(int dx){
        this.posY += this.speed*dx;
        this.speed += this.speed < 0.8 ? 0.02: 0;
        this.standUp = (this.speed == 0);
    }

    public void draw(Graphics g){
        g.fillRect(this.posX, this.posY, this.w, this.h);
    }

    @Override
    public String toString(){
        return "player "+ id +": Score " + score;
    }

    public boolean collide(Bench b){
        return Math.abs(bottom() - b.top()) < 5 && right() <= b.right() && left() >= b.left();
    }
    
    public boolean collideWithPlayer(Player p){
    	return (isBetweenLR(p.left()) || isBetweenLR(p.right())) && (isBetweenTB(p.bottom()) || (isBetweenTB(p.top())));
    }

	private boolean isBetweenLR(int candidate) {
		return isBetween(this.left(), this.right(), candidate);
	}

	private boolean isBetweenTB(int candidate) {
		return isBetween(this.top(), this.bottom(), candidate);
	}

	private boolean isBetween(int min, int max, int candidate) {
		return candidate>=min && candidate<=max;
	}

	public boolean hit(Bench b){
        return Math.abs(top() - b.bottom()) < 5 && right() <= b.right() && left() >= b.left();
    }

    public int top() {
        return this.posY;
    }

    public int left() {
        return this.posX;
    }

    public int bottom() {
        return this.posY + this.h;
    }

    public int right() {
        return this.posX + this.w;
    }

	public void levelDown() {
		this.posY += 100;
	}

    public void reboundingLeft(){
        this.posX -= 20;
    }

    public void rebounding(Player player){
        if (this.left()<player.left()) {
            this.reboundingLeft();
            player.reboundingRight();
        }else{
            player.reboundingLeft();
            this.reboundingRight();
        }
    }

    private void reboundingRight() {
        this.posX +=20;
    }
    public void quitarVida(){
    	this.vida--;
    }

	public boolean loseLife(int i) {
		if(this.posY > i && this.stillLife())
		{
			quitarVida();
            this.score=this.score-10;
			return true;
		}
		return false;
	}

	public boolean stillLife() {
		return this.vida > 0;
	}

	public void reubicar(int new_posX, int new_posY) {
		this.posX = new_posX;
		this.posY = new_posY;
	}
    public boolean collideUpper(Bench bench){
        if (bench.top()>this.bottom()){
            return true;
        }
        return false;
    }

    public long getScore() {
        return score;
    }

	public Color getColor() {
		return this.color;
	}

    public void addScore(long score) {
        this.score += score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

	@Override
	public int compareTo(Player o) {
		return -Long.compare(score, o.getScore());
	}

	public Player makeClone() {
		Player newPlayer = new Player(this.x, this.y, this.vida, this.ButtonUp, this.ButtonRight, this.ButtonLeft, this.color);
		return newPlayer;
	}
}
