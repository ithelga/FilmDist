package org.example.filmdist.models;

/**
 * Define additional model FilmNetRating with constructor to SQL-query with new field
 */

public class FilmNetRating {

    private Long count;
    private String nameFilmNet;

    /**
     * empty constructor
     */
    public FilmNetRating() {
    }

    /**
     * constructor with attributes
     */

    public FilmNetRating(Long count, String nameFilmNet) {
        this.count = count;
        this.nameFilmNet = nameFilmNet;
    }

    /**
     * getter and setter for attributes
     */

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getNameFilmNet() {
        return nameFilmNet;
    }

    public void setNameFilmNet(String nameFilmNet) {
        this.nameFilmNet = nameFilmNet;
    }
}
