package com.tri_sma;

import java.util.LinkedList;

import java.util.Observable;
import java.util.Random;

public class Agent {
    private final double K_PLUS = 0.1;
    private final double K_MINUS = 0.3;
    private final int MEMORY_SIZE = 10;
    private final boolean ERRORS = false;
    private final double ERROR_RATE = 0.3;

    private static int count = 0;

    private final int id;
    private boolean isCarrying = false;
    private char object;
    private LinkedList<Character> memory = new LinkedList<>();
    private Agent assistantAgent;
    private boolean isAssisting;

    //Help values
    private boolean isWaiting = false;
    private int waitForHelp = 0;
    private double waitRate = 1.01;
    private int helpCalls = 0;

    private Environnement env;

    public Agent(Environnement env) {
        this.id = count++;
        this.env = env;
        this.isAssisting = false;
    }

    /**
     * The agent moves
     * If he's waiting for help, randomize the decision of staying or going away :
     *      Adding some random with the waiting of the agent : 1/(1.1^n), with n being the number of times the Agent waited
     * Otherwise he can call for help once more, refreshing values in the help grid that may decrease with time
     *      The rate for calling help is 1/(2^n), with n being the number of times he already refreshed values
     */
    public void move()  {
        Random rnd = env.getRandom();
        isWaiting=false;
        waitForHelp=0;
        helpCalls=0;

        int mvmt = rnd.nextInt(Directions.values().length);
        Directions dir = Directions.values()[mvmt];
        env.moveAgent(this, dir);
    }

    public void actionV2() throws Exception {
        Random rnd = env.getRandom();
        long fA = memory.stream().filter(character -> character=='A').count();
        long fB = memory.stream().filter(character -> character=='B').count();
        long fC = memory.stream().filter(character -> character=='C').count();
        double f = 0;
        double DOUBLE_MEM = MEMORY_SIZE;
        char tile = env.getTile(this);
        boolean coop = false;

        if(isCarrying && tile == '0') {
            switch(object) {
                case 'A':
                    f = fA / DOUBLE_MEM;
                    break;
                case 'B':
                    f = fB / DOUBLE_MEM;
                    break;
                case 'C':
                    f = fC / DOUBLE_MEM;
                    coop = true;
                    break;
                default:
                    break;
            }

            double prob_dep = Math.pow(f/(K_MINUS+f),2);
            if(rnd.nextDouble() < prob_dep) {
                isCarrying = false;
                env.dropItem(this, object, coop);
                object = ' ';
            }

        }else if(!isCarrying && tile != '0'){
            switch(tile) {
                case 'A':
                    f = fA / DOUBLE_MEM;
                    break;
                case 'B':
                    f = fB / DOUBLE_MEM;
                    break;
                case 'C':
                    f = fC / DOUBLE_MEM;
                    coop = true;
                    isWaiting = true;
                    break;
                default:
                    break;
            }

            double prob_get = Math.pow(K_PLUS/(K_PLUS+f),2);
            if(rnd.nextDouble() < prob_get) {
                char res = env.getItem(this, coop);
                if(res!='Z') {
                    isCarrying = true;
                    object = res;
                    isWaiting = false;
                    waitForHelp = 0;
                    helpCalls=0;
                    //isAssisting = false;
                }else{
                    waitForHelp++;
                }
            }else{
                isWaiting=false;
            }
        }
    }

    public void action() throws Exception {
        Random rnd = env.getRandom();
        long fA = memory.stream().filter(character -> character=='A').count();
        long fB = memory.stream().filter(character -> character=='B').count();

        double f = 0;
        double DOUBLE_MEM = (double)MEMORY_SIZE;
        char tile = env.getTile(this);
        if(isCarrying && tile == '0') {

            f = ERRORS ? (object=='A' ? (fA+ERROR_RATE*fB)/DOUBLE_MEM : (fB+ERROR_RATE*fA)/DOUBLE_MEM) : (object == 'A') ? fA/DOUBLE_MEM : fB/DOUBLE_MEM;

            double prob_dep = Math.pow(f/(K_MINUS+f),2);
            if(rnd.nextDouble() < prob_dep) {
                isCarrying = false;
                env.dropItem(this, object, false);
                object = ' ';
            }

        }else if(!isCarrying && tile != '0'){
            f = ERRORS ? (tile=='A' ? (fA+ERROR_RATE*fB)/DOUBLE_MEM : (fB+ERROR_RATE*fA)/DOUBLE_MEM) : (tile == 'A') ? fA/DOUBLE_MEM : fB/DOUBLE_MEM;
            double prob_get = Math.pow(K_PLUS/(K_PLUS+f),2);
            if(rnd.nextDouble() < prob_get) {
                isCarrying = true;
                object = env.getItem(this, false);
            }
        }
    }

    public void run() throws Exception {
        if(this.env.isVersion2()) {
            Random rnd = env.getRandom();
            //Si on attend ET que on en a pas marre d'attendre
            double proba_att = 1./Math.pow(waitRate,waitForHelp);
            double prob_rnd = rnd.nextDouble();

            if(isWaiting && proba_att > prob_rnd) {
                //Si on a pas appelé à l'aide assez
                if(rnd.nextDouble() < 1/Math.pow(2,helpCalls)) {
                    //ALED
                    helpCalls++;
                    env.putPheromons(this);
                }
                actionV2();
            //Sinon, si je ne suis pas en train d'aider
            }else if(!isAssisting){
                //Store last tile encountered before moving
                if(memory.size()>=MEMORY_SIZE) {
                    memory.removeFirst();
                }
                memory.add(env.getTile(this));
                move();
                actionV2();
            }
        }else {
            if(memory.size()>=MEMORY_SIZE) {
                memory.removeFirst();
            }
            memory.add(env.getTile(this));
            move();
            action();
        }
    }

    public Agent getAssistantAgent() { return this.assistantAgent; }

    public void setWaiting(boolean waiting) { isWaiting = waiting; }

    public boolean isCarrying() { return isCarrying; }

    public void setAssistantAgent(Agent assistantAgent) { this.assistantAgent=assistantAgent; }

    public void setAssisting(boolean assisting) { this.isAssisting=assisting; }

    public LinkedList<Character> getMemory() { return memory; }

    public void setMemory(LinkedList<Character> memory) { this.memory = memory; }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "com.tri_sma.Agent{" +
                "id=" + id +
                '}';
    }

    public void setWaitRate(double waitRate) {
        this.waitRate = waitRate;
    }
}
