package cl.uchile.dcc.cc5303;

public class MainThreadServer extends Thread {
	
	private PublicObject po;
	private final static int UPDATE_RATE = 60;

	public MainThreadServer(PublicObject po) {
		this.po = po;
	}
	
	@Override
	public void run(){
		while(true){
			//update barras
            boolean levelsDown = false;
            for (Bench barra : po.benches) {
            	for(Player p : po.players){
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
            
            // Update board
            if (levelsDown) {
                po.levelsDown();
            }
            
            boolean finish = true;
            for(Player player : po.players){
            	if(player.stillLife()){
            		finish = false;
            		break;
            	}
            }
            if (finish)
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