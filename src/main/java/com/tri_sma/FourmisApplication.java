package com.tri_sma;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;

public class FourmisApplication extends Application implements Observer {

    private GridPane gp;
    private Scene sc;
    private Stage st;
    private final Environnement ev;
    private HashMap<Character, Color> colorHashMap;
    private ArrayList<Color> pheromonColors;
    private ArrayList<Double> yellowBorders;
    private Rectangle[][]  rectGrid;
    private int step = 0;


    public FourmisApplication(Environnement ev) {
        super();
        this.ev = ev;
        colorHashMap = new HashMap<>();
        createYellowBrightness();
        colorHashMap.put('A', Color.BLUE);
        colorHashMap.put('B', Color.RED);
        colorHashMap.put('C', Color.GREEN);
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
                        rect.setFill(grid[j][i] != Environnement.DEFAULT_CHAR ? colorHashMap.get(grid[j][i]) : yellowColor(ev.getGrilleAide()[j][i]));
                        Agent agent = ev.getGrilleAgent()[j][i];
                        if(agent!=null) {
                            rect.setStroke(agent.getAssistantAgent()!=null ? Color.PURPLE : Color.BLACK);
                        }else rect.setStroke(Color.WHITE);

                        rect.setStrokeWidth(2);
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
                rectGrid[j][i] = rect;
            }
        }
        sc = new Scene(gp, ev.getN()*12, ev.getM()*12, true);
        stage.setScene(sc);
        stage.setTitle("Tri SMA");

        stage.show();
    }

    public void createYellowBrightness() {
        pheromonColors = new ArrayList<>();
        yellowBorders = new ArrayList<>();
        for(int i=0;i<=14;i++) {
            int blue =50 +14*i;
            pheromonColors.add(Color.rgb(255,255,blue));
        }
        Collections.reverse(pheromonColors);
        for(double i=0;i<=14;i++) {
            yellowBorders.add(i/14);
        }
    }

    public Color yellowColor(Double tileValue) {
        if(tileValue==0) return Color.WHITE;
        int i = 0;
        while(i<=14) {
            if(tileValue<=yellowBorders.get(i)) return pheromonColors.get(i);
            i++;
        }
        return Color.WHITE;
    }


}
