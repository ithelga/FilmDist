package org.example.filmdist.models;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;

@Entity
public class FilmOnNet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "film_id")
    private Film film;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "film_net_id")
    private FilmNet filmNet;

    public FilmOnNet(Film film, FilmNet filmNet) {
        this.film = film;
        this.filmNet = filmNet;
    }

    public FilmOnNet() {
    }

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


