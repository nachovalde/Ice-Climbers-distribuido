package cl.uchile.dcc.cc5303;

import java.rmi.RemoteException;

public class MainThreadServer extends Thread {
	
	private IPublicObject po;
	private final static int UPDATE_RATE = 60;

	public MainThreadServer(IPublicObject po) {
		this.po = po;
	}
	
	@Override
	public void run(){
		while(true){
			//update barras
            boolean levelsDown = false;
            try {
				for (Bench barra : po.getPublicBench()) {
					for(Player p : po.getPlayers()){
				        if (p.hit(barra)) {
				            p.speed = 0.8;
				        }else if (p.collide(barra)) {
				            p.speed = 0.01;
				            p.standUp = true;
				            if (barra.getLevel() > 2){
				                levelsDown = true;
				            }
				        }
					}
				    po.checkCollisionAllPlayers();
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            // Update board
            if (levelsDown) {
                try {
					po.levelsDown();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            try {
				if (po.gameOver())
					break;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            try {
                this.sleep(1000 / UPDATE_RATE);

            } catch (InterruptedException ex) {

            }
		}
		System.out.println("Juego Terminado");
		try {
			System.out.println(po.displayFinalScores());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		try {
            this.sleep(1000);
        } catch (InterruptedException ex) {

        }
	}

}