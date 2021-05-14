package org.example.filmdist.repo;

import org.example.filmdist.models.Film;
import org.example.filmdist.models.UserFilm;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

/**
 * Interface to CRUD with table UserFilm
 */
public interface UserFilmRepository extends JpaRepository<UserFilm, Long> {
    /**
     * method to find favorites film in DB UserFilm by object Film's class
     */
    List<UserFilm> findByFilm(Film film);
}
