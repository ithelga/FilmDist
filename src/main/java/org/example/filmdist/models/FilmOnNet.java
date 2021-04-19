package org.example.filmdist.models;

import javax.persistence.*;
import java.util.Set;

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
}


