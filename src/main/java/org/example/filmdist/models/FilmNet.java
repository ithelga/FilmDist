package org.example.filmdist.models;

import javax.persistence.*;

/**
 * Define model-entity to create DB film_net
 */

@Entity
public class FilmNet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nameFilmNet;
    private String linkFilmNet;
    private String filename;

    /**
     * empty constructor
     */
    public FilmNet() {
    }

    /**
     * constructor with attributes - or field for DB
     */

    public FilmNet(String nameFilmNet, String linkFilmNet) {
        this.nameFilmNet = nameFilmNet;
        this.linkFilmNet = linkFilmNet;
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

    public String getNameFilmNet() {
        return nameFilmNet;
    }

    public void setNameFilmNet(String cinemaNetName) {
        this.nameFilmNet = cinemaNetName;
    }

    public String getLinkFilmNet() {
        return linkFilmNet;
    }

    public void setLinkFilmNet(String link) {
        this.linkFilmNet = link;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }


}
