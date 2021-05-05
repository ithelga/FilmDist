package org.example.filmdist.models;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;
import java.util.Set;


@Entity
public class FilmNet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nameFilmNet;
    private String linkFilmNet;
    private String filename;


    public FilmNet() {}

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

    public FilmNet(String nameFilmNet, String linkFilmNet) {
        this.nameFilmNet = nameFilmNet;
        this.linkFilmNet = linkFilmNet;
    }
}
