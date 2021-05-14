package org.example.filmdist.models;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;


/**
 * Define additional model NetJoin with constructor to SQL-query with new field
 */

public class NetJoin {

    private Long filmId, filmNetId;
    private String nameFilmNet,nameFilm,linkFilmNet;
    private int stars;

    @Temporal(TemporalType.DATE)
    Date releaseDate;

    /**
     * empty constructor
     */
    public NetJoin() {
    }

    public NetJoin(Long filmId, Long filmNetId, String nameFilm, int stars, Date releaseDate, String nameFilmNet, String linkFilmNet) {
        this.filmId = filmId;
        this.filmNetId = filmNetId;
        this.nameFilm = nameFilm;
        this.stars = stars;
        this.releaseDate = releaseDate;
        this.nameFilmNet = nameFilmNet;
        this.linkFilmNet = linkFilmNet;
    }

    /**
     * getter and setter for attributes
     */

    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }

    public String getNameFilmNet() {
        return nameFilmNet;
    }

    public void setNameFilmNet(String nameFilmNet) {
        this.nameFilmNet = nameFilmNet;
    }

    public String getLinkFilmNet() {
        return linkFilmNet;
    }

    public void setLinkFilmNet(String linkFilmNet) {
        this.linkFilmNet = linkFilmNet;
    }

    public Long getFilmNetId() {
        return filmNetId;
    }

    public void setFilmNetId(Long filmNetId) {
        this.filmNetId = filmNetId;
    }

    public String getNameFilm() {
        return nameFilm;
    }

    public void setNameFilm(String nameFilm) {
        this.nameFilm = nameFilm;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }
}
