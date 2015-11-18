package cl.uchile.dcc.cc5303;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Random;

import javax.swing.JFrame;

public class MainThreadClient extends Thread implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public boolean[] keys;
    private final static String TITLE = "Juego - CC5303";
    private final static int WIDTH = 800, HEIGHT = 600;
    private final static int UPDATE_RATE = 60;
    private final static int DX = 5;
    private final static int framesToNewBench = 100;
    
    private int id;
    private IPublicObject objeto;
    private IClient client;

    private JFrame frame;
    private Board tablero;
    
    int frames = new Random().nextInt(2 * framesToNewBench);
    
    public void migrate(IPublicObject obj) throws RemoteException{
    	objeto = obj;
    }

    public MainThreadClient(IClient c) throws RemoteException {
		
		this.id = c.getId();
		this.objeto = c.getObjeto();
		this.client = c;
		keys = new boolean[KeyEvent.KEY_LAST];

        frame = new JFrame(TITLE);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        tablero = new Board(WIDTH, HEIGHT);
        for(Player p: objeto.getPublicPlayers()){
        	tablero.addPlayer(p);
        }
        tablero.bases = objeto.getPublicBench();
                
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
        Player p = null;
        long inicio = System.currentTimeMillis() / 1000;
        while(true){
        	try {
				if (client.getMigrated()){
					client.setMigrated(false);
					this.migrate(client.getObjeto());
				}
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        		
            try {
                p = objeto.getPlayerbyId(this.id);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (keys[p.ButtonUp]) {
                p.jump();
            }
            if (keys[p.ButtonRight]) {
                p.moveRight();
            }
            if (keys[p.ButtonLeft]) {
                p.moveLeft();
            }
            p.update(DX);
            try {
                objeto.updatePlayer(id,p);
                if( objeto.playerDie()){
                    System.out.println("Se termino el Juego");
                    break;
                }
                if(!objeto.getPlayerbyId(this.id).stillLife())
                	break;
                
                tablero.setPlayers(objeto.getPlayers());
                tablero.setBenches(objeto.getPublicBench());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            tablero.repaint();
            try {
                this.sleep(1000 / UPDATE_RATE);
            } catch (InterruptedException ex) {

            }
		}
        long fin = System.currentTimeMillis()/1000;
        try{
	        p = objeto.getPlayerbyId(this.id);        
	        if(p.stillLife()){
	        	p.addScore(5*(fin-inicio)+10);
				System.out.println("Ganaste con puntaje = " + p.getScore());
				p.vida=0;
				Thread.sleep(1000/ UPDATE_RATE);
	        }
			else{
				p.addScore(5*(fin-inicio));
				System.out.println("Perdiste con puntaje = " + p.getScore());
			}
	        objeto.updatePlayer(this.id, p);
        }
        catch(RemoteException | InterruptedException e) {
            e.printStackTrace();
        }
        frame.dispose();
	}

}
