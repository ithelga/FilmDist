package org.example.filmdist.repo;

import org.example.filmdist.models.Film;
import org.example.filmdist.models.FilmNet;
import org.example.filmdist.models.FilmOnNet;
import org.example.filmdist.models.NetJoin;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Interface to CRUD with table FilmOnNet
 */
public interface FilmOnNetRepository extends JpaRepository<FilmOnNet, Long> {

    /**
     * method to find film on net by object film
     */
    List<FilmOnNet> findByFilm(Film film);

    /**
     * method to find film on net by object net
     */
    List<FilmOnNet> findByFilmNet(FilmNet filmnet);


    /**
     * method to find nets with films, which they show, and information about them
     */
    @Query("SELECT new org.example.filmdist.models.NetJoin(fon.film.id, fn.id, f.nameFilm, f.stars, f.releaseDate, " +
            "fn.nameFilmNet, fn.linkFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "LEFT JOIN Film f ON f.id=fon.film.id")

    //add constructor NetJoin to new attribute (different to existing tables) SQL - select
    List<NetJoin> findNets();


    /**
     * method to sorted nets with films, which they show, and information about them by name in ascending order
     */
    @Query("SELECT new org.example.filmdist.models.NetJoin(fon.film.id, fn.id, f.nameFilm, f.stars, f.releaseDate, " +
            "fn.nameFilmNet, fn.linkFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "LEFT JOIN Film f ON f.id=fon.film.id order by 3 ASC")
    List<NetJoin> sortNetsByNameAsc();

    /**
     * method to sorted nets with films, which they show, and information about them by name in descending order
     */

    @Query("SELECT new org.example.filmdist.models.NetJoin(fon.film.id, fn.id, f.nameFilm, f.stars, f.releaseDate, " +
            "fn.nameFilmNet, fn.linkFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "LEFT JOIN Film f ON f.id=fon.film.id order by 3 DESC")
    List<NetJoin> sortNetsByNameDesc();

    /**
     * method to sorted nets with films, which they show, and information about them by stars in ascending order
     */

    @Query("SELECT new org.example.filmdist.models.NetJoin(fon.film.id, fn.id, f.nameFilm, f.stars, f.releaseDate, " +
            "fn.nameFilmNet, fn.linkFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "LEFT JOIN Film f ON f.id=fon.film.id order by 4 ASC")
    List<NetJoin> sortNetsByStarsAsc();

    /**
     * method to sorted nets with films, which they show, and information about them by stars in descending order
     */

    @Query("SELECT new org.example.filmdist.models.NetJoin(fon.film.id, fn.id, f.nameFilm, f.stars, f.releaseDate, " +
            "fn.nameFilmNet, fn.linkFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "LEFT JOIN Film f ON f.id=fon.film.id order by 4 DESC")
    List<NetJoin> sortNetsByStarsDesc();

    /**
     * method to sorted nets with films, which they show, and information about them by release date in ascending order
     */

    @Query("SELECT new org.example.filmdist.models.NetJoin(fon.film.id, fn.id, f.nameFilm, f.stars, f.releaseDate, " +
            "fn.nameFilmNet, fn.linkFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "LEFT JOIN Film f ON f.id=fon.film.id order by 5 ASC")
    List<NetJoin> sortNetsByReleaseDateAsc();

    /**
     * method to sorted nets with films, which they show, and information about them by release date in descending order
     */

    @Query("SELECT new org.example.filmdist.models.NetJoin(fon.film.id, fn.id, f.nameFilm, f.stars, f.releaseDate, " +
            "fn.nameFilmNet, fn.linkFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "LEFT JOIN Film f ON f.id=fon.film.id order by 5 DESC")
    List<NetJoin> sortNetsByReleaseDateDesc();


    /**
     * method to search films on net
     */

    @Query("SELECT new org.example.filmdist.models.NetJoin(fon.film.id, fn.id, f.nameFilm, f.stars, f.releaseDate, " +
            "fn.nameFilmNet, fn.linkFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "LEFT JOIN Film f ON f.id=fon.film.id " +
            "WHERE concat(f.nameFilm, f.releaseDate) LIKE %:param%")
    List<NetJoin> searchFilmInNet(@Param("param") String param);
}
