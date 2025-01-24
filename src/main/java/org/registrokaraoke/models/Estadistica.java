package org.registrokaraoke.models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "estadistica")
public class Estadistica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cancion_id", nullable = false)
    private Cancion cancion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Integer reproducciones;

    // Constructor vacío requerido por Hibernate
    public Estadistica() {
    }

    // Constructor con parámetros opcional
    public Estadistica(Cancion cancion, Usuario usuario, LocalDate fecha, Integer reproducciones) {
        this.cancion = cancion;
        this.usuario = usuario;
        this.fecha = fecha;
        this.reproducciones = reproducciones;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cancion getCancion() {
        return cancion;
    }

    public void setCancion(Cancion cancion) {
        this.cancion = cancion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getReproducciones() {
        return reproducciones;
    }

    public void setReproducciones(Integer reproducciones) {
        this.reproducciones = reproducciones;
    }

    @Override
    public String toString() {
        return "Estadistica{" +
                "id=" + id +
                ", cancion=" + cancion.getTitulo() +
                ", usuario=" + usuario.getNombre() +
                ", fecha=" + fecha +
                ", reproducciones=" + reproducciones +
                '}';
    }
}