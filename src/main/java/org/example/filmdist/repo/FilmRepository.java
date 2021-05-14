package org.example.filmdist.repo;

import org.example.filmdist.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * Interface to CRUD with table Film
 */
public interface FilmRepository extends JpaRepository<Film, Long> {

    /**
     * find data to search film by input param
     */

    @Query(value = "select * from film WHERE concat(age_limit, distributor, genre, name_film, release_date) LIKE %:param%", nativeQuery = true)
    List<Film> searchFilm(@Param("param") String param);

    @Query(value = "select * from film WHERE concat(age_limit, distributor, genre, name_film, release_date) LIKE %:param%", nativeQuery = true)
    List<Film> searchFilmPremiere(@Param("param") String param);

    @Query(value = "select * from film WHERE concat(age_limit, count_fees, count_shows, count_viewer, distributor, genre, name_film, release_date) LIKE %:param%", nativeQuery = true)
    List<Film> searchFilmStatistic(@Param("param") String param);

    /**
     * find data to generate rating of films by param
     */

    @Query(value = "select * from film order by count_fees DESC LIMIT 5", nativeQuery = true)
    List<Film> findFilmTop10OrderFees();

    @Query(value = "select * from film order by stars DESC LIMIT 5", nativeQuery = true)
    List<Film> findFilmTop10OrderStars();

    @Query(value = "select * from film order by count_shows DESC LIMIT 5", nativeQuery = true)
    List<Film> findFilmTop10OrderShows();

    @Query(value = "select * from film order by count_viewer DESC LIMIT 5", nativeQuery = true)
    List<Film> findFilmTop10OrderViewer();

    /**
     * find Film in DB Film by name
     */
    Film findByNameFilm(String nameFilmNet);
}
