package org.example.filmdist.controllers;


import org.example.filmdist.models.Film;
import org.example.filmdist.models.FilmNet;
import org.example.filmdist.repo.FilmNetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class FilmNetController {



    @Autowired
    private FilmNetRepository filmNetRepository;

    @GetMapping("/filmnet")
    public String filmnet(Model model) {
        Iterable<FilmNet> filmnet = filmNetRepository.findAll();
        model.addAttribute("filmnet", filmnet);
        return "film-net";
    }


    @GetMapping("/filmnet/add")
    public String filmnetAdd(Model model) {
        Iterable<FilmNet> filmnet = filmNetRepository.findAll();
        model.addAttribute("filmnet", filmnet);
        return "film-net-add";
    }

    @PostMapping("/filmnet/add")
    public String filmAdd(FilmNet filmNet, @RequestParam String nameFilmNet, @RequestParam String linkFilmNet, Model model) {

        FilmNet filmNetFromDB = filmNetRepository.findByNameFilmNet(filmNet.getNameFilmNet());
        if (filmNetFromDB != null) {
            model.addAttribute("message", "Такая киносеть уже существует");
            return "film-net-add";
        }

        filmNet = new FilmNet(nameFilmNet, linkFilmNet);
        filmNetRepository.save(filmNet);

        return "redirect:/filmnet";
    }


    @GetMapping("/filmnet/{id}/remove")
    public String filmDelete(@PathVariable(value = "id") Long id) {
        FilmNet filmNet = filmNetRepository.findById(id).orElseThrow();
        filmNetRepository.delete(filmNet);
        return "redirect:/filmnet";
    }


    @GetMapping("/filmnet/{id}/edit")
    public String filmEdit(@PathVariable(value = "id") Long id, Model model) {
        if (!filmNetRepository.existsById(id)) {
            return "redirect:/filmnet";
        }
        FilmNet filmNet = filmNetRepository.findById(id).orElseThrow();
        model.addAttribute("filmnet", filmNet);
        return "film-net-edit";
    }

    @PostMapping("/filmnet/{id}/edit")
    public String filmUpdate(@PathVariable(value = "id") Long id, @RequestParam String nameFilmNet, @RequestParam String linkFilmNet, Model model) {

        FilmNet filmNet = filmNetRepository.findById(id).orElseThrow();
        filmNet.setNameFilmNet(nameFilmNet);
        filmNet.setLinkFilmNet(linkFilmNet);
        filmNetRepository.save(filmNet);
        return "redirect:/filmnet";
    }


}

