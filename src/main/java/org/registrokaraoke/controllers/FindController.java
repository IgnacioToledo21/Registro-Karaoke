package org.registrokaraoke.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FindController implements Initializable {

    @FXML
    private Button findButton;

    @FXML
    private TableView<?> findTable;

    @FXML
    private TextField findTextfield;

    @FXML
    private TableColumn<?, ?> reproductionColumn;

    @FXML
    private BorderPane root;

    @FXML
    private TableColumn<?, ?> songColumn;

    @FXML
    private TableColumn<?, ?> userColumn;

    public FindController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FindView.fxml"));
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void onFindAction(ActionEvent event) {

    }

    public BorderPane getRoot() {
        return root;
    }
}
