package com.tri_sma.clustering;

import com.tri_sma.Environnement;
import com.tri_sma.Runner;

import java.awt.*;
import java.util.*;


public class Clustering {

    /**
     * A very basic clustering separating algorithm running fast on grid where clusters have been previously been
     * formed. Must be used AFTER the cluster have been formed.
     * This algorithm ressembles to graph exploration algorithms.
     * @param grid the grid to execute the separation on
     * @param range the maximum range allowed to find a cluster. if no other object of the same sort are found in the
     *              area, the exploration will stop. 3 or 4 recommended as this will impact efficiency heavily.
     * @return a list of clusters. Each cluster is a list of positions.
     * @throws Exception if the grid size is incorrect.
     */
    public static HashSet<HashSet<Point>> createClustering(char[][] grid, int range) throws Exception {
        if(grid.length==0 || grid[0].length==0){
            throw new Exception("Incorrect grid size.");
        }
        boolean[][] visited = new boolean[grid.length][grid[0].length];

        HashMap<Point,HashSet<Point>> positionToCluster = new HashMap<>();

        // Exploration
        LinkedList<Point> positionQueue = new LinkedList<>();
        for (int i=0;i<grid.length;i++){
            char[] row = grid[i];
            for(int j=0;j<row.length;j++){
                if(grid[i][j]!= Environnement.DEFAULT_CHAR && !visited[i][j]){
                    positionQueue.add(new Point(i,j));
                    explore(grid,visited,positionQueue,positionToCluster,range);
                }
            }
        }
        Collection<HashSet<Point>> values = positionToCluster.values();
        return new HashSet<>(values);
    }

    private static void explore(char[][] grid,boolean[][] visited,
                                LinkedList<Point> positionQueue,
                                HashMap<Point,HashSet<Point>> positionToCluster,
                                int range){
        if(positionQueue.size()==0)
            return;
        Point curP = positionQueue.poll();
        char currentChar = grid[curP.x][curP.y];

        for(int i=curP.x-range;i<curP.x+range;i++){
            for(int j=curP.y-range;j<curP.y+range;j++){
                if(!(i==curP.x && j==curP.y) && i>=0 && j>=0 && i<grid.length && j<grid[curP.x].length){
                    char nextChar = grid[i][j];
                    Point newPoint = new Point(i,j);


                    // Skip default char
                    if(nextChar==Environnement.DEFAULT_CHAR)
                        continue;

                    // Register position for exploration
                    if(!visited[i][j])
                        positionQueue.add(newPoint);

                    visited[i][j] = true;

                    // Cluster next char
                    if(currentChar==nextChar){
                        // If the other char already is in a cluster, add the current char to the same cluster
                        if(positionToCluster.containsKey(newPoint)){
                            HashSet<Point> cluster = positionToCluster.get(newPoint);
                            cluster.add(curP);
                            positionToCluster.put(curP,cluster);
                        }
                        else {
                            // If the current char is in a cluster and not the next, add it to this cluster
                            // If both are not in any, create a new cluster
                            if(positionToCluster.containsKey(curP)){
                                HashSet<Point> cluster = positionToCluster.get(curP);
                                cluster.add(newPoint);
                                positionToCluster.put(newPoint,cluster);
                            }
                            else{
                                HashSet<Point> cluster = new HashSet<>();
                                cluster.add(newPoint);
                                cluster.add(curP);
                                positionToCluster.put(newPoint,cluster);
                                positionToCluster.put(curP,cluster);
                            }
                        }
                    }
                }
            }
        }

        // Continue exploration
        explore(grid,visited,positionQueue,positionToCluster,range);
    }

    public static void displayClustering(HashSet<HashSet<Point>> clustering, int N,int M,char[][] grid,char default_char){
        ArrayList<HashSet<Point>> clusterList = new ArrayList<>(clustering);
        StringBuilder stringBuilder = new StringBuilder("Clusters :\n");
        int nb = (""+clustering.size()).length();
        stringBuilder.append('|').append("-".repeat(Math.max(0, N*4))).append("|\n");
        for(int i=0;i<N;i++){
            stringBuilder.append("|");
            for(int j=0;j<M;j++){
                stringBuilder.append(" ");
                // Check for cluster
                if(grid[i][j]!=default_char){
                    for(int c = 0; c<clusterList.size();c++){
                        HashSet<Point> cluster = clusterList.get(c);
                        if(cluster.contains(new Point(i,j))){
                            int l = (""+c).length();
                            if(l<2) stringBuilder.append('0');
                            stringBuilder.append(c);
                        }
                    }
                }
                else{
                    stringBuilder.append((""+default_char).repeat(2));
                }
                stringBuilder.append(' ');
            }
            stringBuilder.append("|\n");
        }
        stringBuilder.append('|').append("-".repeat(Math.max(0, N*4))).append('|');
        System.out.println(stringBuilder);
    }



    public static void main(String[] args) {
        Environnement ev = new Environnement(50,50,200,200,50,20);
        Runner runner = new Runner(ev,1000000);
        try{
            runner.start();
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            runner.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        HashSet<HashSet<Point>> clustering = null;
        System.out.println("Starting clustering :");
        try {
            clustering = createClustering(ev.getGrille(), 3);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
