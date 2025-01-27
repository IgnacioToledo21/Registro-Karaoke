    package org.registrokaraoke.controllers;

    import javafx.beans.property.BooleanProperty;
    import javafx.beans.property.SimpleBooleanProperty;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import javafx.scene.control.*;
    import javafx.scene.layout.BorderPane;
    import javafx.scene.layout.VBox;
    import org.registrokaraoke.models.Usuario;
    import org.registrokaraoke.services.SesionUsuario;

    import javax.persistence.EntityManager;
    import javax.persistence.EntityManagerFactory;
    import javax.persistence.NoResultException;
    import javax.persistence.Persistence;
    import java.io.IOException;
    import java.net.URL;
    import java.util.ResourceBundle;

    public class LogInController implements Initializable {

        private BooleanProperty loggedIn = new SimpleBooleanProperty(false);

        @FXML
        private Button logInButton;

        @FXML
        private Button registerButton;

        @FXML
        private TextField userTextfield;

        @FXML
        private BorderPane root;

        @FXML
        private TextField nombreTextfield;

        @FXML
        private TextField emailTextfield;

        @FXML
        private TextField edadTextfield;

        @FXML
        private PasswordField passwordField;

        private EntityManagerFactory emf;

        private Usuario usuarioLogeado;

        public LogInController() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LogInView.fxml"));
                loader.setController(this);
                loader.load();
                emf = Persistence.createEntityManagerFactory("persistencia");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public BorderPane getRoot() {
            return root;
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {

        }

        @FXML
        void onRegisterAction(ActionEvent event) {
            // Crear el Alert para la entrada de datos
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro de Usuario");
            alert.setHeaderText("Por favor, ingresa los datos del nuevo usuario");

            // Crear un formulario dentro del Alert
            VBox vbox = new VBox();
            vbox.setSpacing(10);

            // Campos de texto
            TextField nombreTextfield = new TextField();
            nombreTextfield.setPromptText("Nombre");

            TextField emailTextfield = new TextField();
            emailTextfield.setPromptText("Correo electrónico");

            TextField edadTextfield = new TextField();
            edadTextfield.setPromptText("Edad");

            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Contraseña");

            // Añadir los campos al VBox
            vbox.getChildren().addAll(nombreTextfield, emailTextfield, edadTextfield, passwordField);

            // Mostrar el formulario en el Alert
            alert.getDialogPane().setContent(vbox);

            // Crear un botón para aceptar el formulario
            ButtonType registerButtonType = new ButtonType("Registrar", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(registerButtonType, ButtonType.CANCEL);

            // Mostrar el Alert y esperar una respuesta
            alert.showAndWait().ifPresent(response -> {
                if (response == registerButtonType) {
                    // Obtener los datos de los campos de texto
                    String nombre = nombreTextfield.getText().trim();
                    String edadTexto = edadTextfield.getText().trim();
                    String email = emailTextfield.getText().trim();
                    String password = passwordField.getText().trim();

                    // Validación de campos vacíos
                    if (nombre.isEmpty() || edadTexto.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        showErrorAlert("Error de registro", "Por favor, completa todos los campos.");
                        return;
                    }

                    // Validación de edad como número
                    int edad;
                    try {
                        edad = Integer.parseInt(edadTexto);
                        if (edad <= 0) {
                            showErrorAlert("Error de registro", "La edad debe ser un número positivo.");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        showErrorAlert("Error de registro", "La edad debe ser un número válido.");
                        return;
                    }

                    // Crear el EntityManager para interactuar con la base de datos
                    EntityManager em = emf.createEntityManager();
                    try {
                        em.getTransaction().begin();

                        // Verificar si el correo electrónico ya está registrado
                        long count = em.createQuery(
                                        "SELECT COUNT(u) FROM Usuario u WHERE u.correoElectronico = :correo", Long.class)
                                .setParameter("correo", email)
                                .getSingleResult();

                        if (count > 0) {
                            showErrorAlert("Error de registro", "El correo ya está registrado. Intenta con otro.");
                            return;
                        }

                        // Crear y guardar el nuevo usuario
                        Boolean isAdmin = false; // O puedes decidir otro valor para isAdmin
                        Usuario nuevoUsuario = new Usuario(nombre, email, edad, password, isAdmin);
                        em.persist(nuevoUsuario);

                        em.getTransaction().commit();

                        // Mostrar mensaje de éxito
                        showInfoAlert("Registro exitoso", "El usuario ha sido registrado correctamente.");
                    } catch (Exception e) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        showErrorAlert("Error de registro", "Ocurrió un error durante el registro. Inténtalo nuevamente.");
                    } finally {
                        em.close();
                    }
                }
            });
        }

        @FXML
        void handleLogIn(ActionEvent event) {
            String username = userTextfield.getText().trim();
            String password = passwordField.getText().trim();

            Usuario usuario = authenticateUser(username, password);
            if (usuario != null) {
                SesionUsuario.setUsuarioLogeado(usuario);
                loggedIn.set(true);
                System.out.println("Usuario logeado: " + usuario.getNombre());
                // Aquí podrías cargar otra vista o hacer algo al iniciar sesión
            } else {
                showLoginError();
            }
        }

        private Usuario authenticateUser(String email, String contraseña) {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery(
                                "SELECT u FROM Usuario u WHERE u.correoElectronico = :correo AND u.contraseña = :contraseña", Usuario.class)
                        .setParameter("correo", email)
                        .setParameter("contraseña", contraseña)
                        .getSingleResult();
            } catch (NoResultException e) {
                return null;
            } finally {
                em.close();
            }
        }

        private void showLoginError() {
            showErrorAlert("Error de autenticación", "Correo o contraseña incorrectos. Inténtalo nuevamente.");
        }

        private void showErrorAlert(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

        private void showInfoAlert(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

        public BooleanProperty loggedInProperty() {
            return loggedIn;
        }
    }
