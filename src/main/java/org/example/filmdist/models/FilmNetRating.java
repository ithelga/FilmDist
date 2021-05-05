package org.example.filmdist.models;

public class FilmNetRating {

    private Long count;
    private String nameFilmNet;

    public FilmNetRating() {
    }

    public FilmNetRating(Long count, String nameFilmNet) {
        this.count = count;
        this.nameFilmNet = nameFilmNet;
    }

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
