package org.registrokaraoke.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.registrokaraoke.JPAUtils;
import org.registrokaraoke.models.Usuario;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML
    private Button addButton;

    @FXML
    private TableColumn<Usuario, Integer> ageColumn;

    @FXML
    private Button deleteButton;

    @FXML
    private TableColumn<Usuario, String> emailColumn;

    @FXML
    private Button findButton;

    @FXML
    private Button modifyButton;

    @FXML
    private TableColumn<Usuario, String> nameColumn;

    @FXML
    private BorderPane root;

    @FXML
    private TableView<Usuario> userTable;

    // Lista observable para usuarios
    private final ObservableList<Usuario> usuarios = FXCollections.observableArrayList();

    public UserController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserView.fxml"));
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar las columnas
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("edad"));

        // Cargar los datos en la tabla
        loadUsersFromDatabase();
    }

    @FXML
    void onAddAction(ActionEvent event) {
        // Pedir el nombre del usuario
        TextInputDialog nombreDialog = new TextInputDialog();
        nombreDialog.setTitle("Añadir Usuario");
        nombreDialog.setHeaderText("Introduce el nombre del nuevo usuario:");
        String nombre = nombreDialog.showAndWait().orElse("");

        if (nombre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("El nombre no puede estar vacío.");
            alert.showAndWait();
            return;
        }

        // Pedir el correo electrónico del usuario
        TextInputDialog correoDialog = new TextInputDialog();
        correoDialog.setTitle("Añadir Usuario");
        correoDialog.setHeaderText("Introduce el correo electrónico del nuevo usuario:");
        String correoElectronico = correoDialog.showAndWait().orElse("");

        if (correoElectronico.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("El correo electrónico no puede estar vacío.");
            alert.showAndWait();
            return;
        }

        // Pedir la edad del usuario
        TextInputDialog edadDialog = new TextInputDialog();
        edadDialog.setTitle("Añadir Usuario");
        edadDialog.setHeaderText("Introduce la edad del nuevo usuario:");
        String edadStr = edadDialog.showAndWait().orElse("");

        if (edadStr.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("La edad no puede estar vacía.");
            alert.showAndWait();
            return;
        }

        Integer edad;
        try {
            edad = Integer.parseInt(edadStr);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("La edad debe ser un número válido.");
            alert.showAndWait();
            return;
        }

        // Pedir la contraseña del usuario usando un PasswordField
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Introduce la contraseña del nuevo usuario");
        Dialog<String> contraseñaDialog = new Dialog<>();
        contraseñaDialog.setTitle("Añadir Usuario");
        contraseñaDialog.setHeaderText("Introduce la contraseña del nuevo usuario:");
        contraseñaDialog.getDialogPane().setContent(passwordField);
        contraseñaDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        contraseñaDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        contraseñaDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return passwordField.getText();
            }
            return null;
        });

        String contraseña = contraseñaDialog.showAndWait().orElse("");

        if (contraseña.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("La contraseña no puede estar vacía.");
            alert.showAndWait();
            return;
        }

        // Crear el nuevo usuario
        Usuario nuevoUsuario = new Usuario(nombre, correoElectronico, edad, contraseña);

        // Obtener el EntityManager para interactuar con la base de datos
        EntityManager entityManager = JPAUtils.getEntityManager();

        try {
            // Iniciar la transacción
            entityManager.getTransaction().begin();

            // Persistir el nuevo usuario en la base de datos
            entityManager.persist(nuevoUsuario);

            // Confirmar la transacción
            entityManager.getTransaction().commit();

            // Mostrar mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Usuario añadido");
            alert.setHeaderText(null);
            alert.setContentText("El nuevo usuario se ha añadido correctamente.");
            alert.showAndWait();
            loadUsersFromDatabase();

        } catch (Exception e) {
            // Si ocurre un error, hacer rollback y mostrar mensaje de error
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Se produjo un error al añadir el usuario.");
            alert.showAndWait();

            e.printStackTrace();
        } finally {
            // Cerrar el EntityManager
            entityManager.close();
        }
    }



    @FXML
    private void onDeleteAction(ActionEvent event) {
        // Verificar si hay un usuario seleccionado
        Usuario usuarioSeleccionado = userTable.getSelectionModel().getSelectedItem();

        if (usuarioSeleccionado == null) {
            // Si no hay usuario seleccionado, mostrar alerta
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No seleccionado");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona un usuario para eliminar.");
            alert.showAndWait();
            return;
        }

        // Mostrar un cuadro de diálogo para confirmar la eliminación
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Eliminar Usuario");
        confirmationAlert.setHeaderText("¿Estás seguro de que deseas eliminar a este usuario?");
        confirmationAlert.setContentText("Se eliminarán los datos de este usuario de la base de datos.");

        // Esperar la respuesta del usuario
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Si el usuario confirma, proceder con la eliminación

                // Obtener el usuario de la base de datos usando su id
                EntityManager entityManager = JPAUtils.getEntityManager();
                Usuario usuarioDesdeDB = entityManager.find(Usuario.class, usuarioSeleccionado.getId());

                if (usuarioDesdeDB == null) {
                    // Si no se encuentra el usuario en la base de datos
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Usuario no encontrado");
                    alert.setHeaderText(null);
                    alert.setContentText("No se pudo encontrar el usuario en la base de datos.");
                    alert.showAndWait();
                    return;
                }

                // Comenzar la transacción y eliminar el usuario
                try {
                    entityManager.getTransaction().begin();
                    entityManager.remove(usuarioDesdeDB);  // Eliminar el usuario
                    entityManager.getTransaction().commit();

                    // Eliminar el usuario de la TableView local
                    userTable.getItems().remove(usuarioSeleccionado);

                    // Mostrar un mensaje de éxito
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Usuario Eliminado");
                    alert.setHeaderText(null);
                    alert.setContentText("El usuario ha sido eliminado correctamente.");
                    alert.showAndWait();
                    loadUsersFromDatabase();

                } catch (Exception e) {
                    // En caso de error, deshacer la transacción
                    entityManager.getTransaction().rollback();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error al eliminar");
                    alert.setHeaderText(null);
                    alert.setContentText("Hubo un problema al eliminar el usuario.");
                    alert.showAndWait();
                } finally {
                    entityManager.close();
                }
            }
        });
    }


    @FXML
    void onFindAction(ActionEvent event) {
        // Mostrar un cuadro de diálogo para ingresar el nombre del usuario a buscar
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buscar Usuario");
        dialog.setHeaderText("Introduce el nombre del usuario:");
        dialog.setContentText("Nombre:");

        // Obtener el nombre ingresado por el usuario
        String nombreBuscado = dialog.showAndWait().orElse("");

        if (nombreBuscado.isEmpty()) {
            // Si no se ingresó ningún nombre, mostrar un mensaje de advertencia
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Búsqueda Vacía");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, introduce un nombre para buscar.");
            alert.showAndWait();
            return;
        }

        // Obtener el EntityManager de la clase JPAUtils
        EntityManager entityManager = JPAUtils.getEntityManager();

        try {
            // Iniciar la transacción
            entityManager.getTransaction().begin();

            // Realizar la búsqueda utilizando el EntityManager de manera directa
            // Aquí estamos buscando el usuario por nombre (usando NamedQuery o búsqueda directa)
            List<Usuario> resultadoBusqueda = entityManager.createQuery("SELECT u FROM Usuario u WHERE u.nombre = :nombre", Usuario.class)
                    .setParameter("nombre", nombreBuscado)
                    .getResultList();

            // Terminar la transacción
            entityManager.getTransaction().commit();

            if (resultadoBusqueda.isEmpty()) {
                // Si no se encontró ningún usuario, mostrar un mensaje
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Usuario no encontrado");
                alert.setHeaderText(null);
                alert.setContentText("No se encontró ningún usuario con el nombre: " + nombreBuscado);
                alert.showAndWait();
            } else {
                // Mostrar los resultados de la búsqueda en el TableView
                ObservableList<Usuario> observableResultados = FXCollections.observableArrayList(resultadoBusqueda);
                userTable.setItems(observableResultados);
            }
        } catch (Exception e) {
            // Si ocurre un error, hacer rollback y mostrar un mensaje de error
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error en la búsqueda");
            alert.setHeaderText(null);
            alert.setContentText("Se produjo un error al buscar el usuario.");
            alert.showAndWait();
            e.printStackTrace();
        } finally {
            // Cerrar el EntityManager
            entityManager.close();
        }
    }

    @FXML
    void actionUpdateButton(ActionEvent event) {
        loadUsersFromDatabase();
    }



    @FXML
    private void onModifyAction(ActionEvent event) {
        // Verificar si hay un usuario seleccionado
        Usuario usuarioSeleccionado = userTable.getSelectionModel().getSelectedItem();

        if (usuarioSeleccionado == null) {
            // Si no hay usuario seleccionado, mostrar alerta
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No seleccionado");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona un usuario para modificar.");
            alert.showAndWait();
            return;
        }

        // Obtener el usuario de la base de datos usando su id
        EntityManager entityManager = JPAUtils.getEntityManager();
        Usuario usuarioDesdeDB = entityManager.find(Usuario.class, usuarioSeleccionado.getId());

        if (usuarioDesdeDB == null) {
            // Si no se encuentra el usuario en la base de datos
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Usuario no encontrado");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo encontrar el usuario en la base de datos.");
            alert.showAndWait();
            return;
        }

        // Crear un Dialog para modificar el usuario
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle("Modificar Usuario");
        dialog.setHeaderText("Modifica los datos del usuario seleccionado:");

        // Crear los campos de entrada para los datos con valores de la base de datos
        TextField nombreField = new TextField(usuarioDesdeDB.getNombre());
        TextField correoField = new TextField(usuarioDesdeDB.getCorreoElectronico());
        TextField edadField = new TextField(usuarioDesdeDB.getEdad().toString());
        PasswordField passwordField = new PasswordField();
        passwordField.setText(usuarioDesdeDB.getContraseña());

        // Configurar el contenido del Dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nombreField, 1, 0);
        grid.add(new Label("Correo Electrónico:"), 0, 1);
        grid.add(correoField, 1, 1);
        grid.add(new Label("Edad:"), 0, 2);
        grid.add(edadField, 1, 2);
        grid.add(new Label("Contraseña:"), 0, 3);
        grid.add(passwordField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Botones de acción
        ButtonType buttonTypeOk = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                // Crear un nuevo usuario con los datos modificados
                try {
                    String nombre = nombreField.getText();
                    String correo = correoField.getText();
                    Integer edad = Integer.parseInt(edadField.getText());
                    String contrasena = passwordField.getText();

                    // Validar que los campos no estén vacíos
                    if (nombre.isEmpty() || correo.isEmpty() || edadField.getText().isEmpty() || contrasena.isEmpty()) {
                        showErrorDialog("Todos los campos deben estar completos.");
                        return null;
                    }

                    // Actualizar el usuario con los nuevos datos
                    usuarioDesdeDB.setNombre(nombre);
                    usuarioDesdeDB.setCorreoElectronico(correo);
                    usuarioDesdeDB.setEdad(edad);
                    usuarioDesdeDB.setContraseña(contrasena);

                    // Persistir los cambios en la base de datos
                    entityManager.getTransaction().begin();
                    entityManager.merge(usuarioDesdeDB);  // Merge para actualizar el usuario
                    entityManager.getTransaction().commit();

                    loadUsersFromDatabase();
                    return usuarioDesdeDB;



                } catch (NumberFormatException e) {
                    showErrorDialog("La edad debe ser un número válido.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    // Método para mostrar un diálogo de error
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void loadUsersFromDatabase() {
        EntityManager em = JPAUtils.getEntityManager();

        try {
            // Consultar los usuarios desde la base de datos
            TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u", Usuario.class);
            List<Usuario> resultList = query.getResultList();

            // Agregar los usuarios a la lista observable
            usuarios.clear();
            usuarios.addAll(resultList);

            // Vincular la lista observable con la tabla
            userTable.setItems(usuarios);
        } finally {
            em.close();
        }
    }

    public BorderPane getRoot() {
        return root;
    }
}
