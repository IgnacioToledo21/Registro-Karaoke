package org.registrokaraoke.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.registrokaraoke.JPAUtils;
import org.registrokaraoke.models.Estadistica;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class FindController implements Initializable {

    @FXML
    private Button findButton;

    @FXML
    private TableView<Estadistica> findTable;

    @FXML
    private TextField findTextfield;

    @FXML
    private TableColumn<Estadistica, String> reproductionColumn;

    @FXML
    private BorderPane root;

    @FXML
    private TableColumn<Estadistica, String> songColumn;

    @FXML
    private TableColumn<Estadistica, String> userColumn;

    private ObservableList<Estadistica> allEstadisticas;

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
        // Configurar columnas de la TableView
        songColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().getCancion().getTitulo())
        );
        userColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().getUsuario().getNombre())
        );
        reproductionColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> String.valueOf(cellData.getValue().getReproducciones()))
        );

        // Cargar todos los datos al inicio
        allEstadisticas = FXCollections.observableArrayList(loadEstadisticas());
        findTable.setItems(allEstadisticas);

        // Escuchar cambios en el TextField para filtrar por fecha
        findTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTableByDate(newValue);
        });
    }

    private void filterTableByDate(String dateString) {
        if (dateString.isEmpty()) {
            // Si el campo de texto está vacío, mostrar todos los registros
            findTable.setItems(allEstadisticas);
        } else {
            // Filtrar por parte de la fecha utilizando una consulta LIKE en JPQL
            ObservableList<Estadistica> filteredData = FXCollections.observableArrayList(loadEstadisticasByDate(dateString));
            findTable.setItems(filteredData); // Actualizar la TableView con los resultados filtrados
        }
    }

    private List<Estadistica> loadEstadisticasByDate(String dateString) {
        EntityManager em = JPAUtils.getEntityManager();
        try {
            // Usar una consulta JPQL con LIKE para filtrar por parte de la fecha
            TypedQuery<Estadistica> query = em.createQuery(
                    "SELECT e FROM Estadistica e WHERE CAST(e.fecha AS string) LIKE :datePattern", Estadistica.class
            );
            query.setParameter("datePattern", "%" + dateString + "%"); // Añadir el "%" para buscar cualquier parte de la fecha
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }

    private LocalDate parseDate(String dateString) {
        try {
            // Intentar parsear la fecha
            return LocalDate.parse(dateString); // Esto asume que el formato es YYYY-MM-DD
        } catch (Exception e) {
            return null; // Si no es válida, devolver null
        }
    }

    @FXML
    void onFindAction(ActionEvent event) {
        // El botón findButton no parece ser necesario para el filtrado en tiempo real
    }

    private List<Estadistica> loadEstadisticas() {
        EntityManager em = JPAUtils.getEntityManager();
        try {
            TypedQuery<Estadistica> query = em.createQuery("SELECT e FROM Estadistica e", Estadistica.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }

    public BorderPane getRoot() {
        return root;
    }
}