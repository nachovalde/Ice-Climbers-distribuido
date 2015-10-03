package cl.uchile.dcc.cc5303;

import java.util.ArrayList;

/**
 * Created by luism on 03-10-15.
 */
public class PublicObject {
    private ArrayList<Player> publicPlayers;

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

    public PublicObject(){
        publicPlayers=new ArrayList<>();
    }

    public ArrayList<Player> getPublicPlayers() {
        return publicPlayers;
    }

    public void setPublicPlayers(ArrayList<Player> publicPlayers) {
        this.publicPlayers = publicPlayers;
    }

    public Bench[] getPublicBench() {
        return benches;
    }

}
