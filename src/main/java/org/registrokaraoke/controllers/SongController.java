package org.registrokaraoke.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.registrokaraoke.models.Cancion;
import org.registrokaraoke.services.SongService;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SongController implements Initializable {

    @FXML
    private BorderPane root;

    @FXML
    private Button addButton;

    @FXML
    private Button modifyButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button findButton;

    @FXML
    private Button refreshButton;

    @FXML
    private TableView<Cancion> songTable;

    @FXML
    private TableColumn<Cancion, String> titleColumn;

    @FXML
    private TableColumn<Cancion, String> artistColumn;

    @FXML
    private TableColumn<Cancion, String> durationColumn;

    @FXML
    private TableColumn<Cancion, String> genreColumn;

    private final SongService songService;

    public SongController() {
        songService = new SongService();
        System.out.println("Instanciando SongController");
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

        // Para columnas de tipo String, usamos getters normales
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitulo()));
        artistColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getArtista()));
        genreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGenero()));

        // Para columna de tipo Integer, usamos asObject() y el getter normal
        durationColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDuracion()).asString());

        loadSongs();
    }


    private void loadSongs() {
        songTable.getItems().clear();
        songTable.getItems().addAll(songService.findAllSongs());
    }

    @FXML
    void onAddAction(ActionEvent event) {
        // Crear un dialogo para el título de la canción
        TextInputDialog titleDialog = new TextInputDialog();
        titleDialog.setTitle("Añadir Canción");
        titleDialog.setHeaderText("Introduce el título de la canción:");
        Optional<String> titleResult = titleDialog.showAndWait();

        if (titleResult.isPresent()) {
            String title = titleResult.get();

            // Crear un dialogo para el artista
            TextInputDialog artistDialog = new TextInputDialog();
            artistDialog.setTitle("Añadir Canción");
            artistDialog.setHeaderText("Introduce el artista de la canción:");
            Optional<String> artistResult = artistDialog.showAndWait();

            if (artistResult.isPresent()) {
                String artist = artistResult.get();

                // Crear un dialogo para el género
                TextInputDialog genreDialog = new TextInputDialog();
                genreDialog.setTitle("Añadir Canción");
                genreDialog.setHeaderText("Introduce el género de la canción:");
                Optional<String> genreResult = genreDialog.showAndWait();

                if (genreResult.isPresent()) {
                    String genre = genreResult.get();

                    // Crear un dialogo para la duración de la canción (usamos un InputDialog)
                    TextInputDialog durationDialog = new TextInputDialog();
                    durationDialog.setTitle("Añadir Canción");
                    durationDialog.setHeaderText("Introduce la duración de la canción en segundos:");
                    Optional<String> durationResult = durationDialog.showAndWait();

                    if (durationResult.isPresent()) {
                        try {
                            int duration = Integer.parseInt(durationResult.get());

                            // Verificar que no haya campos vacíos y que la duración sea un número válido
                            if (title.isEmpty() || artist.isEmpty() || genre.isEmpty()) {
                                showAlert("Error", "Todos los campos deben ser llenados correctamente", AlertType.ERROR);
                            } else {
                                Cancion newSong = new Cancion(title, artist, genre, duration);
                                songService.addSong(newSong);
                                loadSongs();
                                showAlert("Canción añadida", "La canción ha sido añadida correctamente", AlertType.INFORMATION);
                            }
                        } catch (NumberFormatException e) {
                            showAlert("Error", "La duración debe ser un número válido", AlertType.ERROR);
                        }
                    }
                }
            }
        }
    }


    @FXML
    void onDeleteAction(ActionEvent event) {
        Cancion selectedSong = songTable.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            songService.deleteSong(selectedSong.getId());
            loadSongs();
            showAlert("Canción eliminada", "La canción se ha eliminado correctamente", AlertType.INFORMATION);
        } else {
            showAlert("Error", "Seleccione una canción para eliminar", AlertType.ERROR);
        }
    }

    @FXML
    void onFindAction(ActionEvent event) {
        // Crear un TextInputDialog para que el usuario ingrese el título de la canción
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buscar Canción");
        dialog.setHeaderText("Ingrese el título de la canción:");
        dialog.setContentText("Título:");

        // Mostrar el cuadro de diálogo y obtener la respuesta
        dialog.showAndWait().ifPresent(searchTitle -> {
            if (searchTitle.trim().isEmpty()) {
                showAlert("Error", "Por favor ingrese un título válido para buscar.", AlertType.WARNING);
                return;
            }

            // Limpiar la tabla y agregar las canciones que coinciden con la búsqueda
            songTable.getItems().clear();
            songTable.getItems().addAll(songService.findSongsByTitle(searchTitle));

            if (songTable.getItems().isEmpty()) {
                showAlert("No encontrado", "No se encontraron canciones con ese título.", AlertType.INFORMATION);
            }
        });
    }


    @FXML
    void onModifyAction(ActionEvent event) {
        Cancion selectedSong = songTable.getSelectionModel().getSelectedItem();

        if (selectedSong != null) {
            // Crear los campos de texto para los 4 atributos
            TextField titleField = new TextField(selectedSong.getTitulo());
            TextField artistField = new TextField(selectedSong.getArtista());
            TextField genreField = new TextField(selectedSong.getGenero());
            TextField durationField = new TextField(String.valueOf(selectedSong.getDuracion()));

            // Crear un HBox para organizar los campos en vertical
            HBox hbox = new HBox(10); // Espaciado entre los elementos
            hbox.setSpacing(10);
            hbox.setStyle("-fx-padding: 10;"); // Opcional, para agregar algo de padding alrededor de los elementos

            // Crear los labels y añadirlos junto con los campos de texto al HBox
            VBox vbox = new VBox(10); // VBox dentro del HBox para alinear los elementos en vertical
            vbox.getChildren().addAll(
                    new Label("Título:"), titleField,
                    new Label("Artista:"), artistField,
                    new Label("Género:"), genreField,
                    new Label("Duración (segundos):"), durationField
            );

            // Crear el diálogo y establecer su contenido
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Modificar Canción");
            dialog.setHeaderText("Modifica los datos de la canción seleccionada");
            dialog.getDialogPane().setContent(vbox);

            // Botones de confirmación
            ButtonType saveButton = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);

            // Esperar la respuesta del usuario
            Optional<ButtonType> result = dialog.showAndWait();

            // Si el usuario presiona "Guardar", actualizamos la canción
            if (result.isPresent() && result.get() == saveButton) {
                try {
                    String newTitle = titleField.getText();
                    String newArtist = artistField.getText();
                    String newGenre = genreField.getText();
                    int newDuration = Integer.parseInt(durationField.getText());

                    // Verificar que los campos no estén vacíos y que la duración sea válida
                    if (newTitle.isEmpty() || newArtist.isEmpty() || newGenre.isEmpty()) {
                        showAlert("Error", "Todos los campos deben ser llenados correctamente", AlertType.ERROR);
                    } else {
                        // Actualizar la canción con los nuevos datos
                        selectedSong.setTitulo(newTitle);
                        selectedSong.setArtista(newArtist);
                        selectedSong.setGenero(newGenre);
                        selectedSong.setDuracion(newDuration);

                        // Guardar la canción modificada
                        songService.updateSong(selectedSong);
                        loadSongs();

                        showAlert("Canción modificada", "La canción se ha actualizado correctamente", AlertType.INFORMATION);
                    }
                } catch (NumberFormatException e) {
                    showAlert("Error", "La duración debe ser un número válido", AlertType.ERROR);
                }
            }
        } else {
            showAlert("Error", "Seleccione una canción para modificar", AlertType.ERROR);
        }
    }

    @FXML
    void onRefreshAction(ActionEvent event) {
        loadSongs(); // Recarga las canciones y actualiza la tabla
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getRoot() {
        return root;
    }
}
