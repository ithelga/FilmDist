package org.example.filmdist.controllers;

import org.example.filmdist.models.*;
import org.example.filmdist.repo.FilmNetRepository;
import org.example.filmdist.repo.FilmOnNetRepository;
import org.example.filmdist.repo.FilmRepository;

import org.example.filmdist.repo.UserFilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
public class FilmController {

    private String logoDefault = "logo_film_default.png";
    private String sortKey = "stars";
    private Long filmCount;
    private Sort.Direction sortDirection = Sort.Direction.DESC;
    private Boolean onlyLike = false, IsSearchFilm = false, IsSearchFilmPremiere = false, IsSearchStatistic = false;
    private String SearchFilmParam, SearchFilmPremiereParam, SearchStatisticParam;

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private FilmNetRepository filmNetRepository;

    @Autowired
    private FilmOnNetRepository filmOnNetRepository;

    @Autowired
    private UserFilmRepository userFilmRepository;

    @Value("${upload.path}")
    private String uploadPath;


    private void sortFilm(Model model) {
        Iterable<Film> films = filmRepository.findAll(Sort.by(sortDirection, sortKey));
        model.addAttribute("films", films);
        model.addAttribute("key", sortKey);
        model.addAttribute("direct", sortDirection == Sort.Direction.DESC ? "desk" : "ask");
    }


    @GetMapping("/")
    public String main(@AuthenticationPrincipal User user, Model model) {
        Date date = new Date();
        model.addAttribute("date", date);
        if (IsSearchFilm) {
            List<Film> films = filmRepository.searchFilm(SearchFilmParam);
            model.addAttribute("films", films);
        } else
            sortFilm(model);

        List<UserFilm> userFilm = userFilmRepository.findAll();
        List<Long> filmIds = new ArrayList<>();

        if (user != null) {
            for (UserFilm uf : userFilm) {
                if (user.getId().equals(uf.getUser().getId())) filmIds.add(uf.getFilm().getId());
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("filmIds", filmIds);
        model.addAttribute("userFilm", userFilm);
        model.addAttribute("onlylike", onlyLike);
        model.addAttribute("searchParam", SearchFilmParam);
        model.addAttribute("page", "film");
        IsSearchFilm = false;
        SearchFilmParam = "";
        return "film";
    }


    @GetMapping("/film")
    public String film(Model model) {
        return "redirect:/";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/film/add")
    public String filmAdd(Model model) {
        Iterable<FilmNet> filmnet = filmNetRepository.findAll(Sort.by(Sort.Direction.ASC, "nameFilmNet"));
        Set<String> filmNames = new HashSet<>();
        for (FilmNet net : filmNetRepository.findAll()) {
            filmNames.add(net.getNameFilmNet());
        }
        model.addAttribute("nets", filmNames);
        model.addAttribute("filmnet", filmnet);
        model.addAttribute("page", "film");

        return "film-add";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/film/add")
    public String filmAdd(@RequestParam String nameFilm,
                          @RequestParam String stars,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date releaseDate,
                          @RequestParam String genre,
                          @RequestParam String distributor,
                          @RequestParam(defaultValue = "0") Integer countViewer,
                          @RequestParam(defaultValue = "0") Integer countShows,
                          @RequestParam(defaultValue = "0") Long countFees,
                          @RequestParam String ageLimit,
                          @RequestParam String linkTrailer,
                          @RequestParam("file") MultipartFile file,
                          @RequestParam Map<String, String> form,
                          Model model) throws IOException {


        Film filmFromDB = filmRepository.findByNameFilm(nameFilm);
        if (filmFromDB != null) {
            Iterable<FilmNet> filmnet = filmNetRepository.findAll(Sort.by(Sort.Direction.ASC, "nameFilmNet"));
            Set<String> filmNames = new HashSet<>();
            for (FilmNet net : filmNetRepository.findAll()) {
                filmNames.add(net.getNameFilmNet());
            }
            model.addAttribute("nets", filmNames);
            model.addAttribute("filmnet", filmnet);
            model.addAttribute("page", "film");
            model.addAttribute("message", "Такой фильм уже существует");
            return "film-add";
        }

        Film film = new Film(nameFilm, genre, distributor, Integer.parseInt(ageLimit), countShows, countViewer, countFees, Integer.parseInt(stars), releaseDate);
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
        film.setLinkTrailer(linkTrailer);
        filmRepository.save(film);

        Set<String> filmNames = new HashSet<>();
        for (FilmNet net : filmNetRepository.findAll()) {
            filmNames.add(net.getNameFilmNet());
        }


        for (String key : form.keySet()) {
            if (filmNames.contains(key)) {
                filmOnNetRepository.save(new FilmOnNet(film, filmNetRepository.findByNameFilmNet(key)));
            }
        }
        model.addAttribute("page", "film");
        return "redirect:/";
    }

    @GetMapping("/film/{id}")
    public String film(@PathVariable(value = "id") Long id, @AuthenticationPrincipal User user, Model model) {
        if (!filmRepository.existsById(id)) {
            return "redirect:/";
        }

        List<UserFilm> userFilm = userFilmRepository.findAll();
        List<Long> filmIds = new ArrayList<>();

        if (user != null) {
            for (UserFilm uf : userFilm) {
                if (user.getId().equals(uf.getUser().getId())) filmIds.add(uf.getFilm().getId());
            }
        }

        Film film = filmRepository.findById(id).orElseThrow();
        model.addAttribute("film", film);
        model.addAttribute("filmIds", filmIds);
        List<NetJoin> selectNet = filmOnNetRepository.findNets();
        model.addAttribute("selectNet", selectNet);
        model.addAttribute("page", "film");
        return "film-id";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/film/{id}/edit")
    public String filmEdit(@PathVariable(value = "id") Long id, Model model) {
        if (!filmRepository.existsById(id)) {
            return "redirect:/";
        }
        Film film = filmRepository.findById(id).orElseThrow();
        model.addAttribute("film", film);
        List<FilmOnNet> filmOnNet = filmOnNetRepository.findByFilm(film);


        Iterable<FilmNet> filmnet = filmNetRepository.findAll(Sort.by(Sort.Direction.ASC, "nameFilmNet"));
        Set<String> filmNames = new HashSet<>();
        for (FilmNet net : filmNetRepository.findAll()) {
            for (FilmOnNet fn : filmOnNet) {
                if (fn.getFilmNet().getNameFilmNet().equals(net.getNameFilmNet())) {
                    filmNames.add(net.getNameFilmNet());
                }
            }
        }
        model.addAttribute("nets", filmNames);
        model.addAttribute("filmnet", filmnet);
        model.addAttribute("page", "film");
        return "film-edit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/film/{id}/edit")
    public String filmUpdate(@PathVariable(value = "id") Long id,
                             @RequestParam String nameFilm,
                             @RequestParam String stars,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date releaseDate,
                             @RequestParam String genre,
                             @RequestParam String distributor,
                             @RequestParam(defaultValue = "0") Integer countViewer,
                             @RequestParam(defaultValue = "0") Integer countShows,
                             @RequestParam(defaultValue = "0") Long countFees,
                             @RequestParam Integer ageLimit,
                             @RequestParam String linkTrailer,
                             @RequestParam("file") MultipartFile file,
                             @RequestParam Map<String, String> form,
                             Model model) throws IOException {

        Film filmFromDB = filmRepository.findByNameFilm(nameFilm);
        if (filmFromDB != null && !filmFromDB.getId().equals(id)) {
            Film film = filmRepository.findById(id).orElseThrow();
            model.addAttribute("film", film);
            List<FilmOnNet> filmOnNet = filmOnNetRepository.findByFilm(film);


            Iterable<FilmNet> filmnet = filmNetRepository.findAll(Sort.by(Sort.Direction.ASC, "nameFilmNet"));
            Set<String> filmNames = new HashSet<>();
            for (FilmNet net : filmNetRepository.findAll()) {
                for (FilmOnNet fn : filmOnNet) {
                    if (fn.getFilmNet().getNameFilmNet().equals(net.getNameFilmNet())) {
                        filmNames.add(net.getNameFilmNet());
                    }
                }
            }
            model.addAttribute("nets", filmNames);
            model.addAttribute("filmnet", filmnet);
            model.addAttribute("page", "film");
            model.addAttribute("message", "Такой фильм уже существует");
            return "film-edit";

        }

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

        List<FilmOnNet> filmOnNet = filmOnNetRepository.findByFilm(film);
        for (FilmOnNet fn : filmOnNet) {
            filmOnNetRepository.delete(fn);
        }

        Set<String> filmNames = new HashSet<>();
        for (FilmNet net : filmNetRepository.findAll()) {
            filmNames.add(net.getNameFilmNet());
        }

        for (String key : form.keySet()) {
            if (filmNames.contains(key)) {
                filmOnNetRepository.save(new FilmOnNet(film, filmNetRepository.findByNameFilmNet(key)));
            }
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
        film.setLinkTrailer(linkTrailer);
        filmRepository.save(film);
        model.addAttribute("page", "film");
        return "redirect:/";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/film/{id}/remove")
    public String filmDelete(@PathVariable(value = "id") Long id, HttpServletRequest request, Model model) {
        Film film = filmRepository.findById(id).orElseThrow();
        List<FilmOnNet> filmOnNet = filmOnNetRepository.findByFilm(film);
        for (FilmOnNet fn : filmOnNet) {
            filmOnNetRepository.delete(fn);
        }


        List<UserFilm> userFilm = userFilmRepository.findByFilm(film);
        for (UserFilm uf : userFilm) {
            userFilmRepository.delete(uf);
        }

        if (!logoDefault.equals(film.getFilename())) {
            File fileNameOldDir = new File(uploadPath + "/" + film.getFilename());
            fileNameOldDir.delete();
        }


        filmRepository.deleteById(id);
        String referrer = request.getHeader("referer");
        model.addAttribute("page", "film");
        return "redirect:" + referrer;
    }

    @GetMapping("/filmpremiere")
    public String filmpremiere(Model model, @AuthenticationPrincipal User user) {
        Date date = new Date();
        model.addAttribute("date", date);
        if (IsSearchFilmPremiere) {
            List<Film> films = filmRepository.searchFilmPremiere(SearchFilmPremiereParam);
            model.addAttribute("films", films);
        } else sortFilm(model);

        List<UserFilm> userFilm = userFilmRepository.findAll();
        List<Long> filmIds = new ArrayList<>();

        if (user != null) {
            for (UserFilm uf : userFilm) {
                if (user.getId().equals(uf.getUser().getId())) filmIds.add(uf.getFilm().getId());
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("filmIds", filmIds);
        model.addAttribute("userFilm", userFilm);
        model.addAttribute("onlylike", onlyLike);

        model.addAttribute("searchParam", SearchFilmPremiereParam);
        IsSearchFilmPremiere = false;
        SearchFilmPremiereParam = "";
        model.addAttribute("page", "filmpremiere");

        return "film-premiere";
    }

    @GetMapping("/filmpremiere/{id}")
    public String filmnet(@PathVariable(value = "id") Long id, Model model, @AuthenticationPrincipal User user) {
        if (!filmRepository.existsById(id)) {
            return "redirect:/filmpremiere";
        }
        Film film = filmRepository.findById(id).orElseThrow();
        model.addAttribute("film", film);
        List<NetJoin> selectNet = filmOnNetRepository.findNets();
        List<UserFilm> userFilm = userFilmRepository.findAll();
        List<Long> filmIds = new ArrayList<>();

        if (user != null) {
            for (UserFilm uf : userFilm) {
                if (user.getId().equals(uf.getUser().getId())) filmIds.add(uf.getFilm().getId());
            }
        }
        model.addAttribute("selectNet", selectNet);
        model.addAttribute("filmIds", filmIds);
        model.addAttribute("page", "filmpremiere");
        return "film-premiere-id";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/filmpremiere/{id}/edit")
    public String filmPremiereEdit(@PathVariable(value = "id") Long id, Model model) {
        if (!filmRepository.existsById(id)) {
            return "redirect:/filmpremiere";
        }


        Film film = filmRepository.findById(id).orElseThrow();
        model.addAttribute("film", film);
        List<FilmOnNet> filmOnNet = filmOnNetRepository.findByFilm(film);
        Iterable<FilmNet> filmnet = filmNetRepository.findAll(Sort.by(Sort.Direction.ASC, "nameFilmNet"));
        Set<String> filmNames = new HashSet<>();
        for (FilmNet net : filmNetRepository.findAll()) {
            for (FilmOnNet fn : filmOnNet) {
                if (fn.getFilmNet().getNameFilmNet().equals(net.getNameFilmNet())) {
                    filmNames.add(net.getNameFilmNet());
                }
            }
        }
        model.addAttribute("nets", filmNames);
        model.addAttribute("filmnet", filmnet);
        model.addAttribute("page", "filmpremiere");
        return "film-edit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/filmpremiere/{id}/edit")
    public String filmPremiereUpdate(@PathVariable(value = "id") Long id,
                                     @RequestParam String nameFilm,
                                     @RequestParam String stars,
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date releaseDate,
                                     @RequestParam String genre,
                                     @RequestParam String distributor,
                                     @RequestParam(defaultValue = "0") Integer countViewer,
                                     @RequestParam(defaultValue = "0") Integer countShows,
                                     @RequestParam(defaultValue = "0") Long countFees,
                                     @RequestParam Integer ageLimit,
                                     @RequestParam String linkTrailer,
                                     @RequestParam("file") MultipartFile file,
                                     @RequestParam Map<String, String> form,
                                     Model model) throws IOException {

        Film filmFromDB = filmRepository.findByNameFilm(nameFilm);
        if (filmFromDB != null && !filmFromDB.getId().equals(id)) {
            Film film = filmRepository.findById(id).orElseThrow();
            model.addAttribute("film", film);
            List<FilmOnNet> filmOnNet = filmOnNetRepository.findByFilm(film);


            Iterable<FilmNet> filmnet = filmNetRepository.findAll(Sort.by(Sort.Direction.ASC, "nameFilmNet"));
            Set<String> filmNames = new HashSet<>();
            for (FilmNet net : filmNetRepository.findAll()) {
                for (FilmOnNet fn : filmOnNet) {
                    if (fn.getFilmNet().getNameFilmNet().equals(net.getNameFilmNet())) {
                        filmNames.add(net.getNameFilmNet());
                    }
                }
            }
            model.addAttribute("nets", filmNames);
            model.addAttribute("filmnet", filmnet);
            model.addAttribute("page", "film");
            model.addAttribute("message", "Такой фильм уже существует");
            return "film-edit";

        }

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

        List<FilmOnNet> filmOnNet = filmOnNetRepository.findByFilm(film);
        for (FilmOnNet fn : filmOnNet) {
            filmOnNetRepository.delete(fn);
        }

        Set<String> filmNames = new HashSet<>();
        for (FilmNet net : filmNetRepository.findAll()) {
            filmNames.add(net.getNameFilmNet());
        }

        for (String key : form.keySet()) {
            if (filmNames.contains(key)) {
                filmOnNetRepository.save(new FilmOnNet(film, filmNetRepository.findByNameFilmNet(key)));
            }
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
        film.setLinkTrailer(linkTrailer);
        filmRepository.save(film);
        model.addAttribute("page", "filmpremiere");
        return "redirect:/filmpremiere";
    }


    @GetMapping("/sort")
    public String sort(@RequestParam(name = "k", defaultValue = "stars") String key, Model model, HttpServletRequest request) {
        if (sortKey.equals(key)) {
            sortDirection = (sortDirection == Sort.Direction.ASC ? Sort.Direction.DESC : Sort.Direction.ASC);
        } else sortKey = key;
        String referrer = request.getHeader("referer");
        onlyLike = false;
        return "redirect:" + referrer;
    }


    @GetMapping("/like/{id}")
    public String like(@PathVariable(value = "id") Long id, @AuthenticationPrincipal User user, Model model, HttpServletRequest request) {
        Film film = filmRepository.findById(id).orElseThrow();
        UserFilm userFilm = new UserFilm(film, user);
        userFilmRepository.save(userFilm);
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }


    @GetMapping("/relike/{id}")
    public String relike(@PathVariable(value = "id") Long id, @AuthenticationPrincipal User user, Model model, HttpServletRequest request) {
        Film film = filmRepository.findById(id).orElseThrow();

        List<UserFilm> userFilm = userFilmRepository.findByFilm(film);
        for (UserFilm uf : userFilm) {
            userFilmRepository.delete(uf);
        }

        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }


    @GetMapping("/findLike")
    public String findLike(Model model, HttpServletRequest request) {
        onlyLike = true;
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }


    @GetMapping("/searchFilm")
    public String searchFilm(@RequestParam String param, Model model, HttpServletRequest request) {
        IsSearchFilm = true;
        SearchFilmParam = param;
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }

    @GetMapping("/searchFilmPremiere")
    public String searchFilmPremiere(@RequestParam String param, Model model, HttpServletRequest request) {
        IsSearchFilmPremiere = true;
        SearchFilmPremiereParam = param;
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }

    @GetMapping("/searchStatistic")
    public String searchStatistic(@RequestParam String param, Model model, HttpServletRequest request) {
        IsSearchStatistic = true;
        SearchStatisticParam = param;
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }


    @GetMapping("/findAll")
    public String findAll(Model model, HttpServletRequest request) {
        onlyLike = false;
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }


    @GetMapping("/statistic")
    public String statistic(Model model) {
        Date date = new Date();
        model.addAttribute("date", date);
        if (IsSearchStatistic) {
            List<Film> films = filmRepository.searchFilmStatistic(SearchStatisticParam);
            model.addAttribute("films", films);
        } else sortFilm(model);

        model.addAttribute("searchParam", SearchStatisticParam);
        IsSearchStatistic = false;
        SearchStatisticParam = "";
        model.addAttribute("page", "statistic");
        return "statistic";
    }


    @GetMapping("/about-author")
    public String aboutauthor(Model model) {
        model.addAttribute("page", "about-author");
        return "about-author";
    }


    @GetMapping("/film/rating")
    public String ratingFilm(Model model) {
        List<Film> filmFees = filmRepository.findFilmTop10OrderFees();
        Map<String, Long> mapFees = new LinkedHashMap<>();
        for (Film f : filmFees) {
            mapFees.put(f.getNameFilm(), f.getCountFees() / 1000000);

        }
        model.addAttribute("mapFees", mapFees);

        List<Film> filmStars = filmRepository.findFilmTop10OrderStars();
        Map<String, Integer> mapStars = new LinkedHashMap<>();
        for (Film f : filmStars) {
            mapStars.put(f.getNameFilm(), f.getStars());

        }
        model.addAttribute("mapStars", mapStars);


        List<Film> filmShows = filmRepository.findFilmTop10OrderShows();
        Map<String, Integer> mapShows = new LinkedHashMap<>();
        for (Film f : filmShows) {
            mapShows.put(f.getNameFilm(), f.getCountShows());
        }

        model.addAttribute("mapShows", mapShows);


        List<Film> filmViewer = filmRepository.findFilmTop10OrderViewer();
        Map<String, Integer> mapViewer = new LinkedHashMap<>();
        for (Film f : filmViewer) {
            mapViewer.put(f.getNameFilm(), f.getCountViewer() / 1000);

        }
        model.addAttribute("mapViewer", mapViewer);
        model.addAttribute("page", "rating");
        return "film-rating";
    }

    @GetMapping("/filmnet/rating")
    public String ratingFilmNet(Model model) {
        filmCount = 0L;

        List<FilmNetRating> filmNetRatings = filmNetRepository.findNetRating();
        for (FilmNetRating f : filmNetRatings) {
            filmCount += f.getCount();
        }

        Map<String, Long> mapNet = new LinkedHashMap<>();
        for (FilmNetRating f : filmNetRatings) {
            mapNet.put(f.getNameFilmNet(), f.getCount() * 100 / filmCount);
        }

        model.addAttribute("mapNet", mapNet);
        model.addAttribute("pass", 30);
        model.addAttribute("fail", 70);
        model.addAttribute("page", "rating");
        return "film-net-rating";

    }


}
