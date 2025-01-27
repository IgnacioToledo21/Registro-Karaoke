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
import java.util.stream.Collectors;

public class StatsController implements Initializable {

    @FXML
    private BorderPane root;

    @FXML
    private TableView<Estadistica> statsTable;

    @FXML
    private TableColumn<Estadistica, String> songColumn, userColumn, dateColumn;

    @FXML
    private TableColumn<Estadistica, Integer> reproductionColumn;

    @FXML
    private TextField buscarTextField;

    private ObservableList<Estadistica> allStats = FXCollections.observableArrayList(); // Almacena todos los datos

    public StatsController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StatsView.fxml"));
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error cargando StatsView.fxml", e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadStats(); // Carga datos al inicio
        setupSearchListener();
    }

    private void setupTableColumns() {
        songColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCancion().getTitulo()));
        userColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getUsuario().getNombre()));
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFecha().toString()));
        reproductionColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getReproducciones())
                        .asObject());
    }

    private void setupSearchListener() {
        buscarTextField.textProperty().addListener((observable, oldValue, newValue) -> filterTableByUser(newValue));
    }

    public void loadStats() {
        List<Estadistica> statsFromDB = fetchStatsFromDB();
        allStats.setAll(statsFromDB);
        statsTable.setItems(allStats);
    }

    private void filterTableByUser(String userName) {
        if (userName.isBlank()) {
            statsTable.setItems(allStats); // Restaurar datos originales
        } else {
            String lowerCaseFilter = userName.toLowerCase();
            List<Estadistica> filteredList = allStats.stream()
                    .filter(e -> e.getUsuario().getNombre().toLowerCase().contains(lowerCaseFilter))
                    .collect(Collectors.toList());
            statsTable.setItems(FXCollections.observableArrayList(filteredList));
        }
    }

    private List<Estadistica> fetchStatsFromDB() {
        EntityManager em = JPAUtils.getEntityManager();
        try {
            TypedQuery<Estadistica> query = em.createQuery("SELECT e FROM Estadistica e ORDER BY reproducciones DESC", Estadistica.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }

    public void addOrUpdateEstadistica(Estadistica estadistica) {
        EntityManager em = JPAUtils.getEntityManager();
        try {
            em.getTransaction().begin();
            if (estadistica.getId() == null) {
                em.persist(estadistica);
            } else {
                em.merge(estadistica);
            }
            em.getTransaction().commit();
            loadStats(); // Recargar datos después de una actualización
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public void refreshOnView() {
        loadStats();
    }

    public BorderPane getRoot() {
        return root;
    }
}