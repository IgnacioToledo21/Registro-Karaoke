package org.registrokaraoke.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LogInController implements Initializable {

    // model

    private BooleanProperty loggedIn = new SimpleBooleanProperty(false);

    // view

    @FXML
    private Button logInButton;

    @FXML
    private TextField passwordTextfield;

    @FXML
    private TextField userTextfield;

    @FXML
    private BorderPane root;

    public LogInController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LogInView.fxml"));
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BorderPane getRoot() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Aquí puedes inicializar datos o configurar elementos específicos si es necesario
    }

    @FXML
    private void handleLogIn(ActionEvent event) {
        String username = userTextfield.getText().trim();
        String password = passwordTextfield.getText().trim();

        // Autenticación básica
        if (authenticateUser(username, password)) {
            loggedIn.set(true);
        } else {
            showLoginError();
        }
    }

    private boolean authenticateUser(String username, String password) {
        // Autenticación simple (puedes expandir con una base de datos o configuración externa)
        return username.equals("root") && password.equals("1234");
    }

    private void showLoginError() {
        showErrorAlert("Error de autenticación", "Usuario o contraseña incorrectos. Inténtalo nuevamente.");
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BooleanProperty loggedInProperty() {
        return loggedIn;
    }

}
