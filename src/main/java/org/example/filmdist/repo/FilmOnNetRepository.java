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


public interface FilmOnNetRepository extends JpaRepository<FilmOnNet, Long>, CrudRepository<FilmOnNet, Long> {
    @Query("SELECT new org.example.filmdist.models.NetJoin(fon.film.id, fn.id, f.nameFilm, f.stars, f.releaseDate, " +
            "fn.nameFilmNet, fn.linkFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "LEFT JOIN Film f ON f.id=fon.film.id")
    List<NetJoin> findNets();

    @Query("SELECT new org.example.filmdist.models.NetJoin(fon.film.id, fn.id, f.nameFilm, f.stars, f.releaseDate, " +
            "fn.nameFilmNet, fn.linkFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "LEFT JOIN Film f ON f.id=fon.film.id order by 3 ASC")
    List<NetJoin> sortNetsByNameAsc();

    @Query("SELECT new org.example.filmdist.models.NetJoin(fon.film.id, fn.id, f.nameFilm, f.stars, f.releaseDate, " +
            "fn.nameFilmNet, fn.linkFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "LEFT JOIN Film f ON f.id=fon.film.id order by 3 DESC")
    List<NetJoin> sortNetsByNameDesc();

    @Query("SELECT new org.example.filmdist.models.NetJoin(fon.film.id, fn.id, f.nameFilm, f.stars, f.releaseDate, " +
            "fn.nameFilmNet, fn.linkFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "LEFT JOIN Film f ON f.id=fon.film.id order by 4 ASC")
    List<NetJoin> sortNetsByStarsAsc();

    @Query("SELECT new org.example.filmdist.models.NetJoin(fon.film.id, fn.id, f.nameFilm, f.stars, f.releaseDate, " +
            "fn.nameFilmNet, fn.linkFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "LEFT JOIN Film f ON f.id=fon.film.id order by 4 DESC")
    List<NetJoin> sortNetsByStarsDesc();



    @Query("SELECT new org.example.filmdist.models.NetJoin(fon.film.id, fn.id, f.nameFilm, f.stars, f.releaseDate, " +
            "fn.nameFilmNet, fn.linkFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "LEFT JOIN Film f ON f.id=fon.film.id order by 5 ASC")
    List<NetJoin> sortNetsByReleaseDateAsc();

    @Query("SELECT new org.example.filmdist.models.NetJoin(fon.film.id, fn.id, f.nameFilm, f.stars, f.releaseDate, " +
            "fn.nameFilmNet, fn.linkFilmNet) FROM FilmOnNet fon " +
            "LEFT JOIN FilmNet fn ON fn.id=fon.filmNet.id " +
            "LEFT JOIN Film f ON f.id=fon.film.id order by 5 DESC")
    List<NetJoin> sortNetsByReleaseDateDesc();


//    @Query("SELECT new org.example.filmdist.models.FilmRemove(fon.id, f.id) FROM FilmOnNet fon " +
//            "LEFT JOIN Film f ON f.id=fon.film.id")
//    List<NetJoin> findNetsbyFilm();





    List <FilmOnNet> findByFilm(Film film);
    List <FilmOnNet> findByFilmNet(FilmNet filmnet);

}
