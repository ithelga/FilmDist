package org.example.filmdist.repo;

import org.example.filmdist.models.Film;
import org.example.filmdist.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FilmRepository extends JpaRepository<Film, Long> , CrudRepository<Film, Long> {
    Iterable<Film> findAllById(Long id);

}
