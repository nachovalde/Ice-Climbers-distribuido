package cl.uchile.dcc.cc5303;

import javax.swing.*;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

/**
 * Created by franchoco on 9/20/15.
 */
public class MainThread extends Thread {
    public boolean[] keys;
    private final static String TITLE = "Juego - CC5303";
    private final static int WIDTH = 800, HEIGHT = 600;
    private final static int UPDATE_RATE = 60;
    private final static int DX = 5;
    private final static double DV = 0.1;
    private final static int framesToNewBench = 100;
    private final double vy = 0.3;

    private JFrame frame;
    private Board tablero;
    private Player player1, player2;

    int frames = new Random().nextInt(2 * framesToNewBench);

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

    public MainThread() {
        keys = new boolean[KeyEvent.KEY_LAST];
        int N = 5;
        //Jugadores
        player1 = new Player(WIDTH/3, 550, N, KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, Color.red);
        player2 = new Player(2*WIDTH/3, 550, N, KeyEvent.VK_W, KeyEvent.VK_D, KeyEvent.VK_A, Color.green);

        //resumen
        System.out.println(tablero);

        frame = new JFrame(TITLE);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tablero = new Board(WIDTH, HEIGHT);
        tablero.addPlayer(player1);
        tablero.addPlayer(player2);
        tablero.bases = benches;


        frame.add(tablero);
        tablero.setSize(WIDTH, HEIGHT);

        frame.pack();
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                keys[e.getKeyCode()] = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keys[e.getKeyCode()] = false;
            }
        });

    }

    @Override
    public void run() {
        while (true) { // Main loop
            //Check controls
        	for(Player p : tablero.players){
	            if (keys[p.ButtonUp]) {
	                p.jump();
	            }
	            if (keys[p.ButtonRight]) {
	                p.moveRight();
	            }
	            if (keys[p.ButtonLeft]) {
	                p.moveLeft();
	            }
        	}
            //update players
            for(Player p : tablero.players){
            	p.update(DX);
            }
            //update barras
            boolean levelsDown = false;
            for (Bench barra : tablero.bases) {
            	for(Player p : tablero.players){
	                if (p.hit(barra)) {
                        p.speed = 0.8;
                    }else if (p.collide(barra)) {
                        if (p.collideUpper(barra)){
                            p.score++;
                        }
	                    p.speed = 0.01;
	                    p.standUp = true;
	                    if (barra.getLevel() > 2){
	                        levelsDown = true;
	                    }
	                }
            	}
                tablero.checkCollisionAllPlayers();
            }

            // Update board
            if (levelsDown) {
                tablero.levelsDown();
            }
            
            //revisar muerte de alg�n jugador
            if( tablero.playerDie())
            {
            	System.out.println("Se termino el Juego");
                for (Player p : tablero.players){
                    System.out.println(p.getScore());
                }
            	frame.dispose();
            	break;
            }
            tablero.repaint();

            try {
                this.sleep(1000 / UPDATE_RATE);
            } catch (InterruptedException ex) {

            }
        }


    }
}
