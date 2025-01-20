package org.registrokaraoke.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class LogInController {

    @FXML
    private Button logInButton;

    @FXML
    private TextField passwordTextfield;

    @FXML
    private TextField userTextfield;

    @FXML
    private BorderPane root;

//    public LogInController() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LogInView.fxml"));
//            loader.setController(this);
//            loader.load();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    // Metodo llamado al presionar el botón 'Ingresar'
    @FXML
    private void handleLogIn(ActionEvent event) {
        String username = userTextfield.getText();
        String password = passwordTextfield.getText();

        // Aquí puedes agregar la lógica de autenticación
        boolean isAuthenticated = authenticateUser(username, password);

        if (isAuthenticated) {
            // Si la autenticación es exitosa, abrir la ventana de navegación
            openNavMenu();
        } else {
            // Si no es exitoso, mostrar un mensaje de error o algo similar
            showLoginError();
        }
    }

    // Lógica para autenticar al usuario (esto lo puedes reemplazar por tu lógica real)
    private boolean authenticateUser(String username, String password) {
        return username.equals("root") && password.equals("1234"); // Autentificacion admin
    }

    // Mostrar un mensaje de error (puedes personalizarlo)
    private void showLoginError() {
        // Aquí puedes poner un Alert o cualquier otra cosa para notificar al usuario
        System.out.println("Error de autenticación");
    }

    // Abrir la ventana de navegación después del login
    // Abrir la ventana de navegación después del login exitoso
    private void openNavMenu() {
        try {
            // Cargar la vista de navegación
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NavMenuView.fxml"));
            BorderPane navMenu = loader.load();

            // Obtener el Stage principal desde la ventana de login
            Stage primaryStage = (Stage) root.getScene().getWindow();

            // Cambiar el contenido del Stage principal
            primaryStage.setTitle("Menú de Navegación");
            primaryStage.getScene().setRoot(navMenu);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
