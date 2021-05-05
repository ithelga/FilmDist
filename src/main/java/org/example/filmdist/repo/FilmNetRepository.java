package org.example.filmdist.repo;

import org.example.filmdist.models.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface FilmNetRepository extends JpaRepository<FilmNet, Long> {
    FilmNet findByNameFilmNet(String nameFilmNet);

    @Query(value = "select * from film_net WHERE name_film_net LIKE %:param%", nativeQuery = true)
    List<FilmNet> searchFilmNet(@Param("param") String param);


    @Query("SELECT new org.example.filmdist.models.FilmNetRating(COUNT(fon.film.id), fn.nameFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "GROUP BY fon.filmNet.id, fn.nameFilmNet")
    List<FilmNetRating> findNetRating();

}
