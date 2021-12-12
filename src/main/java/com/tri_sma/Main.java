package com.tri_sma;

public class Main {
    public static void main(String[] args) {
        Environnement ev = new Environnement(50,50,200,200,20);
        Runner runner = new Runner(ev,320000);
        try{
            runner.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
