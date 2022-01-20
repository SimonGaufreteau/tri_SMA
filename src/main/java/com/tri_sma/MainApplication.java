package com.tri_sma;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {

    public static void main(String[] args) { launch(); }

    @Override
    public void start(Stage stage) throws Exception {
        Environnement ev = new Environnement(50,50,200,200,200,20,0);
        Runner runner = new Runner(ev, 500000);
        Stage st = new Stage();
        FourmisApplication fourmiApp = new FourmisApplication(ev);

        ev.addObserver(fourmiApp);
        fourmiApp.start(st);
        try{
            runner.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

