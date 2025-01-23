package org.registrokaraoke.controllers;

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

    @FXML
    private Button logInButton;

    @FXML
    private TextField passwordTextfield;

    @FXML
    private TextField userTextfield;

    @FXML
    private BorderPane root;

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
            openNavMenu();
        } else {
            showLoginError();
        }
    }

    private boolean authenticateUser(String username, String password) {
        // Autenticación simple (puedes expandir con una base de datos o configuración externa)
        return username.equals("root") && password.equals("1234");
    }

    private void openNavMenu() {
        try {
            // Cargar el NavMenuController
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NavMenuView.fxml"));
            BorderPane navMenu = loader.load();

            // Obtener el Stage principal desde la ventana actual
            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setTitle("Menú de Navegación");

            // Establecer la nueva vista raíz
            primaryStage.getScene().setRoot(navMenu);

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error al cargar el menú", "No se pudo cargar la vista del menú de navegación.");
        }
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
}
