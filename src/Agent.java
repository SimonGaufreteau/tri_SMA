
import java.util.LinkedList;

import java.util.Random;

public class Agent {
    private final double K_PLUS = 0.1;
    private final double K_MINUS = 0.3;
    private final int MEMORY_SIZE = 10;
    private final boolean ERRORS = false;
    private final double ERROR_RATE = 0.1;

    private static int count = 0;

    private final int id;
    private boolean isCarrying = false;
    private char object;
    private LinkedList<Character> memory = new LinkedList<>();

    private Environnement env;

    public Agent(Environnement env) {
        this.id = count++;
        this.env = env;
    }

    public void move()  {
        Random rnd = new Random();
        int mvmt = rnd.nextInt(Directions.values().length);
        Directions dir = Directions.values()[mvmt];
        //System.out.println("Moving Agent "+id+" in "+dir);
        env.moveAgent(this,dir);
    }

    public void action() throws Exception {
        Random rnd = new Random();
        long fA = memory.stream().filter(character -> character=='A').count();
        long fB = memory.stream().filter(character -> character=='B').count();
        double f = 0;
        char tile = env.getTile(this);
        if(isCarrying && tile == '0') {

            if(ERRORS){
                if(object=='A'){
                    f = (fA+ERROR_RATE*fB)/MEMORY_SIZE;
                }else{
                    f = (fB+ERROR_RATE*fA)/MEMORY_SIZE;
                }
            }
            else {
                if(object == 'A')
                    f = fA/(double)MEMORY_SIZE;
                else f = fB/(double)MEMORY_SIZE;
            }
            double prob_dep = Math.pow(f/(K_MINUS+f),2);
            if(rnd.nextDouble() < prob_dep) {
                isCarrying = false;
                env.dropItem(this, object);
                object = ' ';
            }

        }else if(!isCarrying && tile != '0'){
            f = tile=='A'?fA:fB;
            double prob_get = Math.pow(K_PLUS/(K_PLUS+f),2);
            if(rnd.nextDouble() < prob_get) {
                isCarrying = true;
                object = env.getItem(this);
            }
        }
    }

    public void run() throws Exception {
        //Store last tile encountered before moving
        if(memory.size()>=MEMORY_SIZE) {
            memory.removeFirst();
        }
        memory.add(env.getTile(this));
//        if(id==1){
//            System.out.println("agent n°"+id+" : "+memory+ "(adding : "+env.getTile(this));
//        }

        move();
        action();
    }



    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "id=" + id +
                '}';
    }
}
