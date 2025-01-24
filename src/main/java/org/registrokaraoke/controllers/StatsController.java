package org.registrokaraoke.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.util.List;
import java.util.ResourceBundle;

public class StatsController implements Initializable {

    @FXML
    private BorderPane root;

    @FXML
    private TableColumn<Estadistica, String> dateColumn;

    @FXML
    private TableColumn<Estadistica, Integer> reproductionColumn;

    @FXML
    private TableColumn<Estadistica, String> songColumn;

    @FXML
    private TableView<Estadistica> statsTable;

    @FXML
    private TableColumn<Estadistica, String> userColumn;

    @FXML
    private TextField buscarTextField;

    public StatsController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StatsView.fxml"));
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Llenar las columnas con los datos de las estadísticas
        songColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(
                        () -> cellData.getValue().getCancion().getTitulo()
                )
        );
        userColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(
                        () -> cellData.getValue().getUsuario().getNombre()
                )
        );
        dateColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(
                        () -> cellData.getValue().getFecha().toString()
                )
        );
        reproductionColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getReproducciones()).asObject()
        );

        // Llenar la TableView con los datos de la base de datos
        ObservableList<Estadistica> estadisticas = FXCollections.observableArrayList(loadEstadisticas());
        statsTable.setItems(estadisticas);

        // Listener para el campo de búsqueda en tiempo real
        buscarTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTableByUser(newValue); // Filtrar por nombre de usuario
        });
    }

    private void filterTableByUser(String userName) {
        if (userName.isEmpty()) {
            // Si el campo de búsqueda está vacío, mostrar todos los registros
            statsTable.setItems(FXCollections.observableArrayList(loadEstadisticas()));
        } else {
            // Filtrar los registros que contienen el nombre de usuario
            ObservableList<Estadistica> filteredData = FXCollections.observableArrayList(loadEstadisticasByUser(userName));
            statsTable.setItems(filteredData);
        }
    }

    private List<Estadistica> loadEstadisticasByUser(String userName) {
        EntityManager em = JPAUtils.getEntityManager();
        try {
            // Usar JPQL con LIKE para filtrar por nombre de usuario
            TypedQuery<Estadistica> query = em.createQuery(
                    "SELECT e FROM Estadistica e WHERE LOWER(e.usuario.nombre) LIKE LOWER(:userName)", Estadistica.class
            );
            query.setParameter("userName", "%" + userName + "%"); // El "%" permite la búsqueda parcial
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
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