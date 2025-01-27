package org.registrokaraoke.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.registrokaraoke.JPAUtils;
import org.registrokaraoke.services.SesionUsuario;
import org.registrokaraoke.models.Cancion;
import org.registrokaraoke.models.Estadistica;
import org.registrokaraoke.models.Usuario;
import org.registrokaraoke.services.SongService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
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

    @FXML
    private TextField buscarCancionTextField;

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
        durationColumn.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(cellData.getValue().getDuracion()).asString());

        // Listener para la búsqueda en tiempo real
        buscarCancionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            onSearch(newValue); // Realiza la búsqueda con el nuevo texto
        });

        loadSongs(); // Carga las canciones inicialmente



    }

    private void onSearch(String searchText) {
        // Limpiar la tabla antes de mostrar los resultados
        songTable.getItems().clear();

        // Si el texto de búsqueda no está vacío, buscar las canciones que coinciden
        if (searchText != null && !searchText.trim().isEmpty()) {
            songTable.getItems().addAll(songService.findSongsByTitle(searchText));
        } else {
            // Si el campo está vacío, mostrar todas las canciones
            loadSongs();
        }
    }

    private void loadSongs() {
        songTable.getItems().clear();
        songTable.getItems().addAll(songService.findAllSongs());
    }

    @FXML
    void onAddAction(ActionEvent event) {
        // Crear el cuadro de diálogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Añadir Canción");
        dialog.setHeaderText("Introduce los detalles de la canción");

        // Crear los campos de texto
        TextField titleField = new TextField();
        titleField.setPromptText("Título de la canción");

        TextField artistField = new TextField();
        artistField.setPromptText("Artista");

        TextField genreField = new TextField();
        genreField.setPromptText("Género");

        TextField durationField = new TextField();
        durationField.setPromptText("Duración en segundos");

        // Diseñar el layout del cuadro de diálogo
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Título:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Artista:"), 0, 1);
        grid.add(artistField, 1, 1);
        grid.add(new Label("Género:"), 0, 2);
        grid.add(genreField, 1, 2);
        grid.add(new Label("Duración:"), 0, 3);
        grid.add(durationField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Añadir botones de acción
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Mostrar el cuadro de diálogo y esperar el resultado
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String title = titleField.getText().trim();
                String artist = artistField.getText().trim();
                String genre = genreField.getText().trim();
                int duration = Integer.parseInt(durationField.getText().trim());

                if (title.isEmpty() || artist.isEmpty() || genre.isEmpty() || duration <= 0) {
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

    @FXML
    void onDeleteAction(ActionEvent event) {
        Cancion selectedSong = songTable.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmar eliminación");
            confirmationAlert.setHeaderText("¿Está seguro que desea eliminar la canción?");
            confirmationAlert.setContentText("Esta acción no se puede deshacer.");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                songService.deleteSong(selectedSong.getId());
                loadSongs();
                showAlert("Canción eliminada", "La canción se ha eliminado correctamente", AlertType.INFORMATION);
            }
        } else {
            showAlert("Error", "Seleccione una canción para eliminar", AlertType.ERROR);
        }
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
                    new Label("Duración (segundos):"), durationField);

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

                        showAlert("Canción modificada", "La canción se ha actualizado correctamente",
                                AlertType.INFORMATION);
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

    @FXML
    private void onPlayAction(ActionEvent event) {
        Usuario usuarioLogeado = SesionUsuario.getUsuarioLogeado();

        if (usuarioLogeado == null) {
            showAlert("Error", "no hay usuario logeaod", AlertType.ERROR);
            return;
        }

        Cancion cancionSeleccionada = obtenerCancionSeleccionada();

        if (cancionSeleccionada == null) {
            showAlert("Error", "no hay cancion seleccionada", AlertType.ERROR);
            return;
        }

        EntityManager em = JPAUtils.getEntityManager();
        try {
            em.getTransaction().begin();

            // Buscar si ya existe un registro de esta canción para este usuario en la fecha
            // actual
            TypedQuery<Estadistica> query = em.createQuery(
                    "SELECT e FROM Estadistica e WHERE e.usuario = :usuario AND e.cancion = :cancion AND e.fecha = :fecha",
                    Estadistica.class);
            query.setParameter("usuario", usuarioLogeado);
            query.setParameter("cancion", cancionSeleccionada);
            query.setParameter("fecha", LocalDate.now());

            List<Estadistica> resultados = query.getResultList();

            Estadistica estadistica;
            if (!resultados.isEmpty()) {
                // Si ya existe, aumentar el número de reproducciones
                estadistica = resultados.get(0);
                estadistica.setReproducciones(estadistica.getReproducciones() + 1);
            } else {
                // Si no existe, crear un nuevo registro
                estadistica = new Estadistica(cancionSeleccionada, usuarioLogeado, LocalDate.now(), 1);
                em.persist(estadistica);
            }

            em.getTransaction().commit();
            System.out.println("Reproducción registrada: " + estadistica);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            showAlert("Error", "no se pudo registrar la cancion", AlertType.ERROR);
        } finally {
            em.close();
        }
    }

    private Cancion obtenerCancionSeleccionada() {
        return songTable.getSelectionModel().getSelectedItem();
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