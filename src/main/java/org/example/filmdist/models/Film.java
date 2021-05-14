package org.example.filmdist.models;

import javax.persistence.*;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Define model-entity to create DB film
 */

@Entity
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nameFilm, genre, distributor, filename, linkTrailer;
    private int ageLimit, countShows, countViewer, stars;
    private Long countFees;

    @Temporal(TemporalType.DATE)
    Date releaseDate;


    /**
     * empty constructor
     */
    public Film() {
    }

    /**
     * constructor with attributes - or field for DB
     */
    public Film(String nameFilm, String genre, String distributor, int ageLimit, int countShows, int countViewer, Long countFees, int stars, Date releaseDate) {
        this.nameFilm = nameFilm;
        this.genre = genre;
        this.distributor = distributor;
        this.ageLimit = ageLimit;
        this.countShows = countShows;
        this.countViewer = countViewer;
        this.countFees = countFees;
        this.stars = stars;
        this.releaseDate = releaseDate;
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

    public String getNameFilm() {
        return nameFilm;
    }

    public void setNameFilm(String nameFilm) {
        this.nameFilm = nameFilm;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public int getAgeLimit() {
        return ageLimit;
    }

    public void setAgeLimit(int ageLimit) {
        this.ageLimit = ageLimit;
    }

    public int getCountShows() {
        return countShows;
    }

    public void setCountShows(int countShows) {
        this.countShows = countShows;
    }

    public int getCountViewer() {
        return countViewer;
    }

    public void setCountViewer(int countViewer) {
        this.countViewer = countViewer;
    }

    public Long getCountFees() {
        return countFees;
    }

    public void setCountFees(Long countFees) {
        this.countFees = countFees;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLinkTrailer() {
        return linkTrailer;
    }

    public void setLinkTrailer(String linkTrailer) {
        this.linkTrailer = linkTrailer;
    }

}
