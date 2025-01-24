package org.registrokaraoke;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        // Configurar EntityManagerFactory y EntityManager
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistencia");
        EntityManager em = emf.createEntityManager();

        try {
            // Verificar la conexión inicial
            System.out.println("Conexión establecida con la base de datos.");

            // Lógica principal de la aplicación aquí (lanzar JavaFX)
            javafx.application.Application.launch(App.class);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            if (em.isOpen()) {
                em.close();
            }
            if (emf.isOpen()) {
                emf.close();
            }
            System.out.println("Conexión cerrada con la base de datos.");
        }
    }
}
