package org.registrokaraoke.models;

import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String correoElectronico;

    @Column(nullable = false)
    private Integer edad;

    @Column(nullable = false, length = 255)
    private String contraseña;

    // Se eliminó el campo isAdmin de la base de datos
    // Este campo será gestionado solo en la autenticación.
    private Boolean isAdmin;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Estadistica> estadisticas;

    // Constructor vacío requerido por Hibernate
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(String nombre, String correoElectronico, Integer edad, String contraseña) {
        this.nombre = nombre;
        this.correoElectronico = correoElectronico;
        this.edad = edad;
        this.contraseña = contraseña;
        // No asignamos isAdmin aquí, lo manejaremos en la lógica de autenticación
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public List<Estadistica> getEstadisticas() {
        return estadisticas;
    }

    public void setEstadisticas(List<Estadistica> estadisticas) {
        this.estadisticas = estadisticas;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", correoElectronico='" + correoElectronico + '\'' +
                ", edad=" + edad +
                ", isAdmin=" + isAdmin +  // Mostrar si el usuario es admin
                '}';
    }
}
