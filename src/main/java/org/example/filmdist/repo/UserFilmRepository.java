package org.example.filmdist.repo;

import org.example.filmdist.models.Film;
import org.example.filmdist.models.FilmNet;
import org.example.filmdist.models.FilmOnNet;
import org.example.filmdist.models.UserFilm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserFilmRepository extends CrudRepository<UserFilm, Long>, JpaRepository<UserFilm, Long> {
    List<UserFilm> findByFilm(Film film);

}
