package com.tri_sma;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Environnement {
    private char[][] grille;
    private Agent[][] grilleAgent;
    private int N,M,nA,nB,nbAgents;

    private static int I = 1;

    private HashMap<Agent, Point> mapAgents;

    private static char DEFAULT_CHAR = ' ';

    public Environnement(int n, int m, int nA, int nB, int nbAgents) {
        N = n;
        M = m;
        this.nA = nA;
        this.nB = nB;
        this.nbAgents = nbAgents;
        this.grille = new char[n][m];
        this.grilleAgent = new Agent[n][m];
        mapAgents = new HashMap<>();

        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                grille[i][j] = DEFAULT_CHAR;
            }
        }

        remplirValeur(nA,'A');
        remplirValeur(nB,'B');
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
        Point lastPos = mapAgents.get(agent);

        switch (dir){
            case NORTH -> pos.y-=I;
            case NORTHEAST -> {
                pos.y-=I;
                pos.x+=I;
            }
            case EAST -> pos.x+=I;
            case SOUTHEAST -> {
                pos.x+=I;
                pos.y+=I;
            }
            case SOUTH -> pos.y+=I;
            case SOUTHWEST -> {
                pos.x-=I;
                pos.y+=I;
            }
            case WEST -> pos.x-=I;
            case NORTHWEST -> {
                pos.x-=I;
                pos.y-=I;
            }
        }
        if(pos.x >= 0 && pos.y >=0 && pos.y < M && pos.x < N){
//            System.out.println("Moving agent n°"+ agent.getId()+" to : " + pos.toString());
            grilleAgent[lastPos.y][lastPos.x] = null;
            grilleAgent[pos.y][pos.x] = agent;
            mapAgents.put(agent,pos);
        }
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
        /*stringBuilder.append("\n\nAgents :\n");
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
        stringBuilder.append('|').append("-".repeat(Math.max(0, N*3))).append('|');*/
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

    public void dropItem(Agent agent, char object) {
        Point pos = mapAgents.get(agent);
        //System.out.println("dropping item : "+object);
        grille[pos.y][pos.x] = object;
    }

    public char getItem(Agent agent) {
        Point pos = mapAgents.get(agent);
        char res = grille[pos.y][pos.x];
        grille[pos.y][pos.x] = DEFAULT_CHAR;
        //System.out.println("getting item : "+res);
        return res;
    }

    //Legacy function, RIP in peperonni
    /*public int getNeighbourood(Agent agent,char carry) {
        int range = I;
        Point pos = mapAgents.get(agent);
        for (int i=-range;i<range;i++){
            int currentY = pos.y+i;
            if(currentY>=0 && currentY)
            for(int j=-range;j<range;j++){
                int currentX = pos.x+j;

                if(currentY>=0 && currentX<)
            }
        }
    }*/
}
