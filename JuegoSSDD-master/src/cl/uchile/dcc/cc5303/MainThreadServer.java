package cl.uchile.dcc.cc5303;

import java.rmi.RemoteException;

public class MainThreadServer extends Thread {
	
	private IPublicObject po;
	private final static int UPDATE_RATE = 60;
	private final static int DX = 5;

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
            
            int alive = 0;
            try {
				for(Player player : po.getPlayers()){
					if(player.stillLife()){
						alive++;
					}
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if (alive==1)
            	break;
            
            try {
                this.sleep(1000 / UPDATE_RATE);
            } catch (InterruptedException ex) {

            }
		}
		System.out.println("Juego Terminado");
		try {
            this.sleep(1000);
        } catch (InterruptedException ex) {

        }
	}

}