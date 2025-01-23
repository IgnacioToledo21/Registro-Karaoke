package org.registrokaraoke.controllers;

import javafx.application.Platform;
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
    private StatsController stc = new StatsController();
    private UserController uc = new UserController();

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
        loadUserTab();

        //Estadísticas
        loadStatsTab();

        //Búsquedas
        loadFindTab();

    }

    //Cargar vista de usuarios
    private void loadUserTab() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserView.fxml"));
            BorderPane userView = loader.load();

            userTab.setContent(userView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Cargar vista de estadísticas
    private void loadStatsTab() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StatsView.fxml"));
            BorderPane statsView = loader.load();

            statsTab.setContent(statsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Cargar vista de búsquedas
    private void loadFindTab() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FindView.fxml"));
            BorderPane findView = loader.load();

            findTab.setContent(findView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BorderPane getRoot() {
        return root;
    }

}
