import java.util.Set;

public class Runner {
    private Environnement environnement;

    public Runner(){
        environnement = new Environnement(50,50,200,200,20);

    }

    public void run(int steps) throws Exception {
        System.out.println("Starting the simulation on "+ steps + " steps");
        System.out.println(environnement);
        Set<Agent> agentList = environnement.getAgents();
        for(int i=0;i<steps;i++){
            for(Agent agent : agentList){
                agent.run();
            }
        }
        System.out.println("\nEnd of simulation : ");
        System.out.println(environnement);
    }
}
