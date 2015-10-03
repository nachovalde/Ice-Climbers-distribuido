package cl.uchile.dcc.cc5303;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JFrame;

public class MainThreadClient extends Thread{
	
    public boolean[] keys;
    private final static String TITLE = "Juego - CC5303";
    private final static int WIDTH = 800, HEIGHT = 600;
    private final static int UPDATE_RATE = 60;
    private final static int DX = 5;
    private final static int framesToNewBench = 100;
    
    private int id;
    private PublicObject objeto;
    
    private JFrame frame;
    private Board tablero;
    
    int frames = new Random().nextInt(2 * framesToNewBench);

	public MainThreadClient(PublicObject objeto, int id) {
		
		this.id = id;
		this.objeto = objeto;
		
		keys = new boolean[KeyEvent.KEY_LAST];
		
        frame = new JFrame(TITLE);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        tablero = new Board(WIDTH, HEIGHT);
        // Agregar jugadores y benches
        
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
		while(true){
			
			
			try {
                this.sleep(1000 / UPDATE_RATE);
            } catch (InterruptedException ex) {

            }
		}
	}

}
