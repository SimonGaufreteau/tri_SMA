package com.tri_sma.clustering;

import com.tri_sma.Environnement;
import com.tri_sma.Runner;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Locale;

public class Evaluation {
    public enum PARAMETERS{WAIT_RATE,DIFF_SIGNAL}

    // Fixed parameters
    public static final int n = 50;
    public static final int m = 50;
    public static final int nA = 200;
    public static final int nB = 200;
    public static final int nC = 200;
    public static final int nbAgents = 20;

    // Fixed clustering parameters
    public static final int range = 3;

    // CSV
    public static final String CSV_SEP=";";

    public static double[] startEvaluation(double wait_rate, int diff_signal, int nbIterations, long seed, FileWriter fw , boolean write) throws Exception {
        long timer;
        Environnement environnement = new Environnement(n,m,nA,nB,nC,nbAgents,seed);
        environnement.setDiffSignal(diff_signal);
        environnement.setAgentWaitRate(wait_rate);
        Runner runner = new Runner(environnement,nbIterations);
        timer = System.currentTimeMillis();
        runner.start();
        runner.join();
        timer = System.currentTimeMillis() - timer;

        HashSet<HashSet<Point>> clustering = Clustering.createClustering(environnement.getGrille(), range);
        double[] score = Clustering.basicEvaluation(clustering, environnement.getGrille());
        System.out.println("\nFinished evaluation with parameters : WR = " +
                wait_rate + " / DS = " + diff_signal + " / IT = "+
                nbIterations + " in "+ timer + "ms.\nScore : "+ Clustering.formatScore(score)+"\n");

        if(write)
            writeResultsCSV(wait_rate,diff_signal,nbIterations,score,timer,fw);
        return score;
    }

    public static void writeHeaderCSV(FileWriter fw) throws IOException {
        fw.write("Wait rate;Signal diffusion;Iterations;Mean size;Mean distance;Number of cluster;Time\n");
    }

    public static void writeResultsCSV(double waitRate, int diffSignal, int nbIterations, double[] score, long time, FileWriter fw) throws IOException {
        fw.write(waitRate+CSV_SEP+diffSignal+CSV_SEP+nbIterations+CSV_SEP+score[0]+CSV_SEP+score[1]+CSV_SEP+score[2]+CSV_SEP+time+"\n");
    }

    public static FileWriter createAndOpenFile(String path) throws IOException {
        File file = new File(path);
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file);
        return fileWriter;
    }

    private static void parametersEvaluation() throws Exception {
        // Files
        String filePathWaitRate = "./results/waitRate.csv";
        String filePathSignal = "./results/diffSignal_mean.csv";
        String filePathIteration = "./results/iterations.csv";

        // Base, min and max parameters
        int stepsWR = 200;
        double defWR = 1.01;
        double minWR = 1.001;
        double maxWR = 1.3;

        int defDS = 2;
        int minDS = 1;
        int maxDS = 15;

        int defIT = 400000;
        int minIT = 0;
        int maxIT = 500000;

        double step = (maxWR-minWR)/stepsWR;


        String barChar = "-";
        int nChar = 10;
        String barString = barChar.repeat(nChar);

        Locale.setDefault(new Locale("en"));
        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.CEILING);

        System.out.println("Starting estimate run");
        FileWriter fw = createAndOpenFile(filePathWaitRate);
        writeHeaderCSV(fw);
        long estimate = 7000;
        System.out.println("\n"+barString+"Starting evaluation on waitRate"+barString+"  (ETA : "+estimate+"s / "+estimate/60+"min)\n");

        for(double waitRate = minWR;waitRate<maxWR;waitRate+=step){
            startEvaluation(Double.parseDouble(df.format(waitRate)),defDS,defIT,0,fw);
        }
        fw.close();


        fw = createAndOpenFile(filePathSignal);
        writeHeaderCSV(fw);
        System.out.println(barString+"\nStarting evaluation on diffSignal"+barString+"  (ETA : "+estimate+"s)\n");
        double[] meanResult= new double[4];
        for(int ds = minDS;ds<maxDS;ds++){
            for(int i=0;i<5;i++){
                double[] score =startEvaluation(defWR,ds,defIT,0,fw,false);
                for (int j = 0; j < score.length; j++) {
                    meanResult[j]+=score[j];
                }
            }
            for (int j = 0; j < meanResult.length; j++) {
                meanResult[j]/=5;
            }
            writeResultsCSV(defWR,ds,defIT,meanResult,0,fw);
        }

        fw.close();


        fw = createAndOpenFile(filePathIteration);
        writeHeaderCSV(fw);
        System.out.println(barString+"\nStarting evaluation on iteration"+barString+" (No clear ETA)\n");
        for(int it = minIT;it<maxIT;it+=50000){
            startEvaluation(defWR,defDS,it,0,fw);
        }
        fw.close();

    }

    private static double[] startEvaluation(double waitRate, int ds, int it, int seed, FileWriter fw) throws Exception {
        return startEvaluation(waitRate,ds,it,seed,fw,true);
    }

    public static void main(String[] args) throws Exception {
        parametersEvaluation();
    }

}
