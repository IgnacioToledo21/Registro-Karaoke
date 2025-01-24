package org.registrokaraoke.services;

import org.registrokaraoke.models.Cancion;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.Transactional;
import java.util.List;

public class SongService {

    private EntityManagerFactory emf;
    private EntityManager em;

    public SongService() {
        emf = Persistence.createEntityManagerFactory("persistencia");
        em = emf.createEntityManager();
    }

    @Transactional
    public void addSong(Cancion cancion) {
        em.getTransaction().begin();
        em.persist(cancion);
        em.getTransaction().commit();
    }

    @Transactional
    public void deleteSong(Long id) {
        Cancion cancion = em.find(Cancion.class, id);
        if (cancion != null) {
            em.getTransaction().begin();
            em.remove(cancion);
            em.getTransaction().commit();
        }
    }

    @Transactional
    public void updateSong(Cancion cancion) {
        em.getTransaction().begin();
        em.merge(cancion);
        em.getTransaction().commit();
    }

    public List<Cancion> findAllSongs() {
        return em.createQuery("FROM Cancion", Cancion.class).getResultList();
    }

    public Cancion findSongById(Long id) {
        return em.find(Cancion.class, id);
    }

    public List<Cancion> findSongsByTitle(String title) {
        return em.createQuery("FROM Cancion WHERE titulo LIKE :title", Cancion.class)
                .setParameter("title", "%" + title + "%")
                .getResultList();
    }

    public void close() {
        em.close();
        emf.close();
    }
}
