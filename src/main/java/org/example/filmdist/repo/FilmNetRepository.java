package org.example.filmdist.repo;

import org.example.filmdist.models.FilmNet;
import org.example.filmdist.models.User;
import org.springframework.data.repository.CrudRepository;


public interface FilmNetRepository extends CrudRepository<FilmNet, Long> {
    FilmNet findByNameFilmNet(String nameFilmNet);
}
