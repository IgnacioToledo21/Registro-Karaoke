package org.registrokaraoke.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.registrokaraoke.controllers.SongController;

public class NavMenuController implements Initializable {

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

        //Canciones
        loadSongTab();

        //Usuarios
        loadUserTab();

        //Estadísticas
        loadStatsTab();

        //Búsquedas
        loadFindTab();

    }

    //Cargar vista de canciones
    private void loadSongTab() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SongView.fxml"));
            BorderPane patternContent = loader.load();

            songTab.setContent(patternContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Cargar vista de usuarios
    private void loadUserTab() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserView.fxml"));
            BorderPane patternContent = loader.load();

            userTab.setContent(patternContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Cargar vista de estadísticas
    private void loadStatsTab() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StatsView.fxml"));
            BorderPane patternContent = loader.load();

            statsTab.setContent(patternContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Cargar vista de búsquedas
    private void loadFindTab() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FindView.fxml"));
            BorderPane patternContent = loader.load();

            findTab.setContent(patternContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BorderPane getRoot() {
        return root;
    }

}
