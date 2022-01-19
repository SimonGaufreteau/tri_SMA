package com.tri_sma;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Environnement extends Observable {
    private char[][] grille;
    private Agent[][] grilleAgent;
    private double[][] grilleAide;
    private int N,M,nA,nB,nC,nbAgents;
    private final boolean VERSION_2 = true;
    private final double diffSignal = 2;


    private static int I = 1;

    private HashMap<Agent, Point> mapAgents;
    private List<Agent> helpers;

    public static final char DEFAULT_CHAR = ' ';

    public char[][] getGrille() {
        return grille;
    }

    public Agent[][] getGrilleAgent() {
        return grilleAgent;
    }

    public int getN() {
        return N;
    }

    public int getM() {
        return M;
    }

    public int getnA() {
        return nA;
    }

    public int getnB() {
        return nB;
    }

    public int getnC() {
        return nC;
    }

    public int getNbAgents() {
        return nbAgents;
    }

    public boolean isVersion2() { return this.VERSION_2; }

    public Environnement(int n, int m, int nA, int nB, int nC, int nbAgents) {
        N = n;
        M = m;
        this.nA = nA;
        this.nB = nB;
        this.nC = nC;
        this.nbAgents = nbAgents;
        this.grille = new char[n][m];
        this.grilleAgent = new Agent[n][m];
        this.grilleAide = new double[n][m];
        mapAgents = new HashMap<>();
        helpers = new ArrayList<>();

        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                grille[i][j] = DEFAULT_CHAR;
                grilleAide[i][j] = 0;
            }
        }

        remplirValeur(nA,'A');
        remplirValeur(nB,'B');
        if(VERSION_2) {
            remplirValeur(nC,'C');
        }
        placerAgents();

    }

    private void placerAgents(){
        int i=0;
        Random random = new Random();
        while(i<nbAgents){
            int x = random.nextInt(M);
            int y = random.nextInt(N);
            if(grilleAgent[y][x]==null){
                Agent agent = new Agent(this);
                //agent.move();
                grilleAgent[y][x] = agent;
                mapAgents.put(agent,new Point(x,y));
                i++;
            }
        }
    }

    private void remplirValeur(int nb,char ch) {
        int i=0;
        Random random = new Random();
        while(i<nb){
            int x = random.nextInt(M);
            int y = random.nextInt(N);
            if(grille[x][y]==DEFAULT_CHAR){
                grille[x][y]=ch;
                i++;
            }
        }
    }
    
    public void moveAgent(Agent agent, Directions dir) {
        Point pos = (Point) mapAgents.get(agent).clone();
        Point helpPos = getPheromonNeighborhood(pos);
        Point lastPos = mapAgents.get(agent);
        if(agent.isCarrying() || helpPos.equals(pos)) {
            switch (dir) {
                case NORTH -> pos.y -= I;
                case NORTHEAST -> {
                    pos.y -= I;
                    pos.x += I;
                }
                case EAST -> pos.x += I;
                case SOUTHEAST -> {
                    pos.x += I;
                    pos.y += I;
                }
                case SOUTH -> pos.y += I;
                case SOUTHWEST -> {
                    pos.x -= I;
                    pos.y += I;
                }
                case WEST -> pos.x -= I;
                case NORTHWEST -> {
                    pos.x -= I;
                    pos.y -= I;
                }
            }
        }else{
            pos = helpPos;
        }
        if(pos.x >= 0 && pos.y >=0 && pos.y < M && pos.x < N){
            int lastX = lastPos.x;
            int lastY = lastPos.y;
            int newX = pos.x;
            int newY = pos.y;
            // Si un agent est sur la case, on ne bouge pas
            if(grilleAgent[newY][newX]!=null){
                return;
            }
            grilleAgent[lastY][lastX] = null;
            grilleAgent[newY][newX] = agent;
            mapAgents.put(agent,pos);
        }
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        // Environnement
        StringBuilder stringBuilder = new StringBuilder("Environnement :\n");
        stringBuilder.append('|').append("-".repeat(Math.max(0, N*3))).append("|\n");
        for(int i=0;i<N;i++){
            stringBuilder.append("|");
            for(int j=0;j<M;j++){
                stringBuilder.append(' ').append(grille[i][j]).append(' ');
            }
            stringBuilder.append("|\n");
        }
        stringBuilder.append('|').append("-".repeat(Math.max(0, N*3))).append('|');

        // Agents
        stringBuilder.append("\n\nAgents :\n");
        stringBuilder.append('|').append("-".repeat(Math.max(0, N*3))).append("|\n");
        for(int i=0;i<N;i++){
            stringBuilder.append("|");
            for(int j=0;j<M;j++){
                if(grilleAgent[i][j]==null) {
                    stringBuilder.append("   ");
                }else{
                    int id = grilleAgent[i][j].getId();
                    int l = (""+id).length();
                    if(l<3) stringBuilder.append('0');
                    if(l<2) stringBuilder.append('0');
                    stringBuilder.append(grilleAgent[i][j].getId());
                }
            }
            stringBuilder.append("|\n");
        }
        stringBuilder.append('|').append("-".repeat(Math.max(0, N*3))).append('|');
        return  stringBuilder.toString();
    }

    public char getTile(Agent agent) throws Exception {
        Point point = mapAgents.get(agent);
        if(point != null){
            char res = grille[point.y][point.x];
            return res==' '?'0':res;
        }
        throw new Exception("Could not find the Agent in the env map.");
    }

    public Set<Agent> getAgents() {
        return mapAgents.keySet();
    }

    public void dropItem(Agent agent, char object, boolean coop) {
        Point pos = mapAgents.get(agent);
        grille[pos.y][pos.x] = object;
        if(coop) {
            Agent assistant = agent.getAssistantAgent();
            Point posAssistant=getAvailableNeighborhood(pos);
            this.grilleAgent[posAssistant.y][posAssistant.x]=assistant;
            mapAgents.put(assistant,posAssistant);
            helpers.remove(assistant);
            assistant.setMemory((LinkedList<Character>) agent.getMemory().clone());
            assistant.setAssisting(false);
            agent.setAssistantAgent(null);
        }
    }

    public char getItem(Agent agent, boolean coop) {
        Point pos = mapAgents.get(agent);
        if(coop) {
            Point neighborCoord = getNeighborhood(pos);
            if (neighborCoord != null) {
                Agent neighbor = grilleAgent[neighborCoord.y][neighborCoord.x];
                neighbor.setAssisting(true);
                neighbor.setWaiting(false);
                agent.setAssistantAgent(neighbor);
                this.grilleAgent[neighborCoord.y][neighborCoord.x]=null;
                helpers.add(neighbor);
            } else {
                //Let's assume that the Z character will never be in the grid so we can use it to say we can't pickup the item
                return 'Z';
            }
        }
        char res = grille[pos.y][pos.x];
        grille[pos.y][pos.x] = DEFAULT_CHAR;
        return res;
    }

    /**
     *
     * @param pos The position of the actual agent whose neighborhood is verified
     * @return An adjacent agent is such agent exists
     */
    public Point getNeighborhood(Point pos) {
        int x = pos.x;
        int y = pos.y;
        for(int i = x-1; i <= x+1; i++) {
            for(int j = y-1; j <= y+1; j++) {
                if((i != x || j != y) && (i>=0 && i<N && j>=0 && j<M)) { //ignore the center tile
                    if(grilleAgent[j][i] != null) {
                        return new Point(i,j);
                    }
                }
            }
        }
        return null;
    }

    public Point getPheromonNeighborhood(Point pos) {
        Random rdn = new Random();
        int x = pos.x;
        int y = pos.y;
        double val = grilleAide[y][x];
        for(int i = x-1; i <= x+1; i++) {
            for(int j = y-1; j <= y+1; j++) {
                if(i>=0 && i<N && j>=0 && j<M) { //Out of bounds
                    if(grilleAide[j][i]>val/* && rdn.nextDouble()<grilleAide[j][i]*/) {
                        return new Point(i,j);
                    }
                }
            }
        }
        return pos;
    }

    /**
     *
     * @param pos
     * @return
     */
    public Point getAvailableNeighborhood(Point pos) {
        int x = pos.x;
        int y = pos.y;
        for(int i = x-1; i <= x+1; i++) {
            for(int j = y-1; j <= y+1; j++) {
                if((i != x || j != y) && (i>=0 && i<N && j>=0 && j<M)) { //ignore the center tile
                    if(grilleAgent[j][i] == null) {
                        return new Point(i,j);
                    }
                }
            }
        }
        return null;
    }

    public void putPheromons(Agent agent) {
        Point pos = mapAgents.get(agent);
        int x = pos.x;
        int y = pos.y;
        double tmpDiff=diffSignal;
        grilleAide[y][x] = 1;

        while(tmpDiff>=0) {
            double fillVal = 1 - tmpDiff/(diffSignal+1);
            for(int i = (int) (x-tmpDiff); i <= x+tmpDiff; i++) {
                for(int j = (int) (y-tmpDiff); j <= y+tmpDiff; j++) {
                    if(i>=0 && i<N && j>=0 && j<M) { //Out of bounds
                        grilleAide[j][i] = fillVal;
                    }
                }
            }
            tmpDiff--;
        }
    }

    public void evaporate() {
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                double val = grilleAide[j][i];
                double ratio = 1d/28d;
                grilleAide[j][i] = val<ratio ? 0d : val/1.1;
            }
        }
    }


    public double[][] getGrilleAide() { return grilleAide; }

    public int getDiffSignal() { return (int) diffSignal; }

    public int getAgentsOnGrid() {
        int ret=0;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                if(grilleAgent[i][j]!=null) ret++;
            }
        }
        return ret;
    }

    public Agent hasDuplicateAgents() {
        Set<Agent> setAgent = new HashSet<>();
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                Agent agent = grilleAgent[i][j];
                if(agent!=null) {
                    if(setAgent.contains(agent)){
                        return agent;
                    }
                    setAgent.add(agent);
                }
            }
        }
        return null;
    }
}
