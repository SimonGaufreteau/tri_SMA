package com.tri_sma;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class FourmisApplication extends Application implements Observer {

    private GridPane gp;
    private Scene sc;
    private Stage st;
    private final Environnement ev;
    private HashMap<Character, Color> colorHashMap;
    private Rectangle[][]  rectGrid;
    private int step = 0;


    public FourmisApplication(Environnement ev) {
        super();
        this.ev = ev;
        colorHashMap = new HashMap<>();
        colorHashMap.put('A', Color.BLUE);
        colorHashMap.put('B', Color.RED);
        colorHashMap.put(Environnement.DEFAULT_CHAR, Color.WHITE);
        rectGrid = new Rectangle[ev.getN()][ev.getM()];
    }

    @Override
    public void update(Observable o, Object arg) {
        if(step++%600==0){
            Platform.runLater(() -> {
                char[][] grid = ev.getGrille();
                int n = ev.getN();
                int m = ev.getM();

                for(int i=0; i<n; i++) {
                    for(int j=0; j<m; j++) {
                        Rectangle rect = rectGrid[j][i];
                        rect.setFill(colorHashMap.get(grid[j][i]));
                    }
                }

            });
            step = 1;
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        gp = new GridPane();
        this.st = stage;

        int n = ev.getN();
        int m = ev.getM();
        char[][] grid = ev.getGrille();

        for(int i=0; i<n; i++) {
            for(int j=0; j<m; j++) {
                Rectangle rect = new Rectangle();
                rect.setFill(colorHashMap.get(grid[j][i]));
                rect.setWidth(10);
                rect.setHeight(10);
                gp.add(rect, j, i);
                rectGrid[i][j] = rect;
            }
        }
        sc = new Scene(gp, ev.getN()*10, ev.getM()*10, true);
        stage.setScene(sc);
        stage.setTitle("Tri SMA");

        stage.show();
    }



}
