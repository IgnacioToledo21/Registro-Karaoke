    package org.registrokaraoke.controllers;

    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import javafx.scene.control.Button;
    import javafx.scene.control.TableColumn;
    import javafx.scene.control.TableView;
    import javafx.scene.layout.BorderPane;

    import java.io.IOException;
    import java.net.URL;
    import java.util.ResourceBundle;

    public class SongController implements Initializable {

        @FXML
        private Button addButton;

        @FXML
        private TableColumn<?, ?> artistColumn;

        @FXML
        private Button deleteButton;

        @FXML
        private TableColumn<?, ?> durationColum;

        @FXML
        private Button findButton;

        @FXML
        private TableColumn<?, ?> genreColumn;

        @FXML
        private Button modifyButton;

        @FXML
        private BorderPane root;

        @FXML
        private TableView<?> songTable;

        @FXML
        private TableColumn<?, ?> titleColumn;

        public SongController() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SongView.fxml"));
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
        void onAddAction(ActionEvent event) {

        }

        @FXML
        void onDeleteAction(ActionEvent event) {

        }

        @FXML
        void onFindAction(ActionEvent event) {

        }

        @FXML
        void onModifyAction(ActionEvent event) {

        }

        public BorderPane getRoot() {
            return root;
        }
    }
