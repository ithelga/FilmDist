package org.example.filmdist.models;

import javax.persistence.*;

/**
 * Define model-entity to create DB user_film to connected film which like user
 */
@Entity
public class UserFilm {

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
     * connected entity to user
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * empty constructor
     */
    public UserFilm() {
    }

    /**
     * constructor with attributes - or field for DB
     */
    public UserFilm(Film film, User user) {
        this.film = film;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
