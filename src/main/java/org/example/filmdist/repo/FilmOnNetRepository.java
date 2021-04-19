package org.example.filmdist.repo;

import org.example.filmdist.models.FilmOnNet;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FilmOnNetRepository extends JpaRepository<FilmOnNet, Long> {
}
