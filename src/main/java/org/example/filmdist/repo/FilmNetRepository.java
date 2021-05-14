package org.example.filmdist.repo;

import org.example.filmdist.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Interface to CRUD with table FilmNet
 */
public interface FilmNetRepository extends JpaRepository<FilmNet, Long> {
    /**
     * method to find net of film in FilmNet by name
     */
    FilmNet findByNameFilmNet(String nameFilmNet);


    /**
     * method to search net of film in FilmNet by name like input param
     */
    @Query(value = "select * from film_net WHERE name_film_net LIKE %:param%", nativeQuery = true)
    List<FilmNet> searchFilmNet(@Param("param") String param);

    /**
     * method to find Data about Film net to create rating
     */
    @Query("SELECT new org.example.filmdist.models.FilmNetRating(COUNT(fon.film.id), fn.nameFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "GROUP BY fon.filmNet.id, fn.nameFilmNet")
   //add constructor FilmNetRating to new attribute (different to existing tables) SQL - select
    List<FilmNetRating> findNetRating();

}
