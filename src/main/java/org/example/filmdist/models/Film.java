package org.example.filmdist.models;


import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @OneToMany(fetch = FetchType.EAGER)
//    @JoinColumn(name = "film_id")
//    private Set<FilmOnNet> FilmOnNet;
//
//    public Set<org.example.filmdist.models.FilmOnNet> getFilmOnNet() {
//        return FilmOnNet;
//    }
//
//    public void setFilmOnNet(Set<org.example.filmdist.models.FilmOnNet> filmOnNet) {
//        FilmOnNet = filmOnNet;
//    }

    private String nameFilm, genre, distributor, filename;
    private int ageLimit, countShows, countViewer, countFees, stars;

    @Temporal(TemporalType.DATE)
    Date releaseDate;

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

    public int getCountFees() {
        return countFees;
    }

    public void setCountFees(int countFees) {
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

    public Film() {
    }

    public Film(String nameFilm, String genre, String distributor, int ageLimit, int countShows, int countViewer, int countFees, int stars, Date releaseDate) {
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
}
