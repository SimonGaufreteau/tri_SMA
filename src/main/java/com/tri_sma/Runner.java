package com.tri_sma;

import java.util.Set;

public class Runner extends Thread {
    private Environnement environnement;
    private int ITER;


    public Runner(Environnement ev, int ITER){
        environnement = ev;
        this.ITER = ITER;
    }

    @Override
    public void run() {
        System.out.println("Starting the simulation on "+ ITER + " steps");
        System.out.println(environnement);
        Set<Agent> agentList = environnement.getAgents();
        for(int i=0;i<ITER;i++){
            for(Agent agent : agentList){
                try {
                    agent.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            environnement.evaporate();
        }
        System.out.println("\nEnd of simulation : ");
        System.out.println(environnement);
    }

}
