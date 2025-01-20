package org.registrokaraoke.controllers;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    private NavMenuController nv = new NavMenuController();

    @FXML
    private BorderPane root;

    public MainMenuController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenuView.fxml"));
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LogInView.fxml"));
            BorderPane loginView = loader.load();

            // Establecer LoginView como contenido inicial
            root.setCenter(loginView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BorderPane getRoot() {
        return root;
    }
}
