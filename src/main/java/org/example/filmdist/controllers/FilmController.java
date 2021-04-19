package org.example.filmdist.controllers;


import org.example.filmdist.models.Film;
import org.example.filmdist.models.FilmNet;
import org.example.filmdist.repo.FilmNetRepository;
import org.example.filmdist.repo.FilmRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
public class FilmController {

    private String logoDefault = "215bb7db-79e2-4c1d-8fe9-dd444cdac468.logo-default.png";
    private String sortKey = "stars";
    private Sort.Direction sortDirection = Sort.Direction.DESC;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private FilmNetRepository filmNetRepository;

    @Value("${upload.path}")
    private String uploadPath;


    @GetMapping("/")
    public String home(Model model) {
        Iterable<Film> films = filmRepository.findAll(Sort.by(sortDirection, sortKey));
        model.addAttribute("films", films);
        model.addAttribute("key", sortKey);
        if (sortDirection == Sort.Direction.DESC) {
            model.addAttribute("direct", "desk");
        } else {
            model.addAttribute("direct", "ask");
        }

        return "film";
    }

    @GetMapping("/sort")
    public String sort(@RequestParam(name = "k", defaultValue = "stars") String key, Model model) {
        if (sortKey.equals(key)) {
            sortDirection = sortDirection == Sort.Direction.ASC ? Sort.Direction.DESC : Sort.Direction.ASC;
        } else sortKey = key;

        return "redirect:/";
    }

    @GetMapping("/statistic")
    public String statistic(Model model) {
        Iterable<Film> films = filmRepository.findAll();
        model.addAttribute("films", films);
        return "statistic";
    }

    @GetMapping("/filmpremiere")
    public String filmpremiere(Model model) {
        Iterable<Film> films = filmRepository.findAll();
        Date date = new Date();
        model.addAttribute("films", films);
        model.addAttribute("date", date);
        System.out.println(date);
        return "film-premiere";
    }

    @GetMapping("/film")
    public String film(Model model) {
        return "redirect:/";
    }

    @GetMapping("/film/add")
    public String filmAdd(Model model) {
        Iterable<FilmNet> filmnet = filmNetRepository.findAll();
        model.addAttribute("filmnet", filmnet);
        return "film-add";
    }


    @PostMapping("/film/add")
    public String filmAdd(@RequestParam String nameFilm,
                          @RequestParam String stars,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date releaseDate,
                          @RequestParam String genre,
                          @RequestParam String distributor,
                          @RequestParam Integer countViewer,
                          @RequestParam Integer countShows,
                          @RequestParam Integer countFees,
                          @RequestParam String ageLimit,
                          @RequestParam("file") MultipartFile file,
                          Model model) throws IOException {

        Film film = new Film(nameFilm, genre, distributor, Integer.parseInt(ageLimit), countViewer, countShows, countFees, Integer.parseInt(stars), releaseDate);
        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String finalFilename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + finalFilename));
            film.setFilename(finalFilename);
        } else {
            film.setFilename(logoDefault);
        }

        filmRepository.save(film);
        return "redirect:/";
    }

    @GetMapping("/film/{id}")
    public String film(@PathVariable(value = "id") Long id, Model model) {
        if (!filmRepository.existsById(id)) {
            return "redirect:/";
        }
        Film film = filmRepository.findById(id).orElseThrow();
        model.addAttribute("film", film);
        return "film-id";
    }

    @GetMapping("/filmpremiere/{id}")
    public String filmnet(@PathVariable(value = "id") Long id, Model model) {
        if (!filmRepository.existsById(id)) {
            return "redirect:/filmpremiere";
        }
        Film film = filmRepository.findById(id).orElseThrow();
        model.addAttribute("film", film);
        return "film-id";
    }

    @GetMapping("/film/{id}/edit")
    public String filmEdit(@PathVariable(value = "id") Long id, Model model) {
        if (!filmRepository.existsById(id)) {
            return "redirect:/";
        }
        Film film = filmRepository.findById(id).orElseThrow();
        model.addAttribute("film", film);
        return "film-edit";
    }

    @PostMapping("/film/{id}/edit")
    public String filmUpdate(@PathVariable(value = "id") Long id,
                             @RequestParam String nameFilm,
                             @RequestParam String stars,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date releaseDate,
                             @RequestParam String genre,
                             @RequestParam String distributor,
                             @RequestParam Integer countViewer,
                             @RequestParam Integer countShows,
                             @RequestParam Integer countFees,
                             @RequestParam Integer ageLimit,
                             @RequestParam("file") MultipartFile file,
                             Model model) throws IOException {

        Film film = filmRepository.findById(id).orElseThrow();
        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {

            String NameOld = film.getFilename();
            if (!logoDefault.equals(NameOld)) {
                File fileNameOldDir = new File(uploadPath + "/" + NameOld);
                fileNameOldDir.delete();
            }

            String uuidFile = UUID.randomUUID().toString();
            String finalFilename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + finalFilename));
            film.setFilename(finalFilename);
        }

        film.setNameFilm(nameFilm);
        film.setStars(Integer.parseInt(stars));
        film.setReleaseDate(releaseDate);
        film.setGenre(genre);
        film.setDistributor(distributor);
        film.setCountViewer(countViewer);
        film.setCountShows(countShows);
        film.setCountFees(countFees);
        film.setAgeLimit(ageLimit);
        filmRepository.save(film);


        return "redirect:/";
    }

    @GetMapping("/film/{id}/remove")
    public String filmDelete(@PathVariable(value = "id") Long id) {
        Film film = filmRepository.findById(id).orElseThrow();
        filmRepository.delete(film);
        return "redirect:/";
    }


    @GetMapping("/about-author")
    public String aboutauthor(Model model) {
        return "about-author";
    }

//    private void uploadFile(@RequestParam("file") MultipartFile file, Film film) throws IOException {
//        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
//            File uploadDir = new File(uploadPath);
//            if (!uploadDir.exists()) {
//                uploadDir.mkdir();
//            }
//
//            String NameOld = film.getFilename();
//            if (!logoDefault.equals(NameOld)) {
//                File fileNameOldDir = new File(uploadPath + "/" + NameOld);
//                fileNameOldDir.delete();
//            }
//
//            String uuidFile = UUID.randomUUID().toString();
//            String finalFilename = uuidFile + "." + file.getOriginalFilename();
//            file.transferTo(new File(uploadPath + "/" + finalFilename));
//            film.setFilename(finalFilename);
//        } else {
//            film.setFilename(logoDefault);
//        }
//    }
}
