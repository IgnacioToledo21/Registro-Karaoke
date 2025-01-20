package org.registrokaraoke;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.registrokaraoke.controllers.RootController;

public class App extends Application {

    private RootController rc;

    @Override
    public void start(Stage primaryStage) throws Exception {

        rc = new RootController();

        primaryStage.setTitle("Registro de Karaoke");
        primaryStage.setScene(new Scene(rc.getRoot()));
        primaryStage.show();

    }

}
