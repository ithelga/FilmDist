package org.example.filmdist.models;

import javax.persistence.*;
import javax.persistence.Entity;


/**
 * Define model-entity to create DB film_on_net to connected films in net
 */
@Entity
public class FilmOnNet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * connected entity to film
     */

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "film_id")
    private Film film;

    /**
     * connected entity to film_net
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "film_net_id")
    private FilmNet filmNet;

    /**
     * empty constructor
     */

    public FilmOnNet() {
    }

    /**
     * constructor with attributes - or field for DB
     */
    public FilmOnNet(Film film, FilmNet filmNet) {
        this.film = film;
        this.filmNet = filmNet;
    }


    /**
     * getter and setter for attributes
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public FilmNet getFilmNet() {
        return filmNet;
    }

    public void setFilmNet(FilmNet filmNet) {
        this.filmNet = filmNet;
    }
}


