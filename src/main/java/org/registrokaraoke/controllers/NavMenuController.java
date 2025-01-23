package org.registrokaraoke.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NavMenuController implements Initializable {

    private SongController sc = new SongController();
    private StatsController statsController = new StatsController();
    private UserController userController = new UserController();
    private FindController findController = new FindController();

    @FXML
    private Tab findTab;

    @FXML
    private TabPane navTabPane;

    @FXML
    private BorderPane root;

    @FXML
    private Tab songTab;

    @FXML
    private Tab statsTab;

    @FXML
    private Tab userTab;

    public NavMenuController() {
        System.out.println("Instanciando NavMenuController");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NavMenuView.fxml"));
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println("Inicializando NavMenuController...");

        //Canciones
        songTab.setContent(sc.getRoot());

        //Usuarios
        userTab.setContent(userController.getRoot());

        //Estadísticas
        statsTab.setContent(statsController.getRoot());

        //Búsquedas
        findTab.setContent(findController.getRoot());

    }

    public BorderPane getRoot() {
        return root;
    }

}
