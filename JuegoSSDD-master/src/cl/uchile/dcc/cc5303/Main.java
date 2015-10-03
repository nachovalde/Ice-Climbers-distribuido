package cl.uchile.dcc.cc5303;

public class Main {

    public static String urlServer = "rmi://localhost:1099/juego";
    public static void main(String[] args){
        System.out.println("Iniciando Juego de SSDD...");
        MainThread m = new MainThread();
        m.start();
    }
}
