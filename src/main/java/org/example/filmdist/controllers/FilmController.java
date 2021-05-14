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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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


/**
 * Controller to define information about film to get from Model DB and receive from View
 * it will me use in url
 */

@Controller
public class FilmController {

    private String logoDefault = "logo_film_default.png"; //default logo film path
    private String sortKey = "stars"; //default param(col) to sort
    private Long filmCount;
    private Sort.Direction sortDirection = Sort.Direction.DESC; //default order to sort - descending
    //param to define search:
    private Boolean onlyLike = false, IsSearchFilm = false,
            IsSearchFilmPremiere = false, IsSearchStatistic = false;
    //param to search   //param to define search:
    private String SearchFilmParam, SearchFilmPremiereParam, SearchStatisticParam;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Get exemplar of DB Film beans
     */
    @Autowired
    private FilmRepository filmRepository;

    /**
     * Get exemplar of DB Net beans
     */
    @Autowired
    private FilmNetRepository filmNetRepository;

    /**
     * Get exemplar of DB Film on Net beans
     */
    @Autowired
    private FilmOnNetRepository filmOnNetRepository;


    /**
     * Get exemplar of DB Film liked User beans
     */
    @Autowired
    private UserFilmRepository userFilmRepository;

    /**
     * define path to uploads
     */
    @Value("${upload.path}")
    private String uploadPath;

    /**
     * method to sort film
     */
    private void sortFilm(Model model) {
        Iterable<Film> films = filmRepository.findAll(Sort.by(sortDirection, sortKey));
        model.addAttribute("films", films);
        model.addAttribute("key", sortKey);
        model.addAttribute("direct", sortDirection == Sort.Direction.DESC ? "desk" : "ask");
    }

    /**
     * method to find information about Film Distribution from Model and received to View for main page
     */
    @GetMapping("/")
    public String main(@AuthenticationPrincipal User user, Model model) {
        Date date = new Date();
        model.addAttribute("date", date); //to find only film in distribution
        if (IsSearchFilm) {//if search
            List<Film> films = filmRepository.searchFilm(SearchFilmParam); //return search list
            model.addAttribute("films", films);
        } else
            sortFilm(model); //all list with sort param

        List<UserFilm> userFilm = userFilmRepository.findAll();
        List<Long> filmIds = new ArrayList<>();//list of liked film by user

        if (user != null) {
            for (UserFilm uf : userFilm) {
                if (user.getId().equals(uf.getUser().getId())) filmIds.add(uf.getFilm().getId());
            }
        }
        //sent param to View
        model.addAttribute("user", user);
        model.addAttribute("filmIds", filmIds);
        model.addAttribute("userFilm", userFilm);
        model.addAttribute("onlylike", onlyLike);
        model.addAttribute("searchParam", SearchFilmParam);
        model.addAttribute("page", "film");
        IsSearchFilm = false;//clean param
        SearchFilmParam = ""; //clean param
        return "film";
    }

    /**
     * method to redirect
     */
    @GetMapping("/film")
    public String film(Model model) {
        return "redirect:/";
    }


    /**
     * method to show View for adding film
     */

    @PreAuthorize("hasAuthority('ADMIN')") //url for admin
    @GetMapping("/film/add")
    public String filmAdd(Model model) {
        Iterable<FilmNet> filmnet = filmNetRepository.findAll(Sort.by(Sort.Direction.ASC, "nameFilmNet"));
        Set<String> filmNames = new HashSet<>();
        for (FilmNet net : filmNetRepository.findAll()) {
            filmNames.add(net.getNameFilmNet()); //fild all Nets
        }

        //sent param to View
        model.addAttribute("nets", filmNames);
        model.addAttribute("filmnet", filmnet);
        model.addAttribute("page", "film");
        return "film-add";
    }

    /**
     * method for adding film to DB
     */

    @PreAuthorize("hasAuthority('ADMIN')") //url for admin
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
        //check if film from DB
        if (filmFromDB != null) {
            Iterable<FilmNet> filmnet = filmNetRepository.findAll(Sort.by(Sort.Direction.ASC, "nameFilmNet"));
            Set<String> filmNames = new HashSet<>();
            for (FilmNet net : filmNetRepository.findAll()) {
                filmNames.add(net.getNameFilmNet());
            }
            //sent param to View
            model.addAttribute("nets", filmNames);
            model.addAttribute("filmnet", filmnet);
            model.addAttribute("page", "film");
            model.addAttribute("message", "Такой фильм уже существует");
            return "film-add";
        }

        Film film = new Film(nameFilm, genre, distributor, Integer.parseInt(ageLimit), countShows, countViewer, countFees, Integer.parseInt(stars), releaseDate);
        //check is logo img attach
        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            File uploadDir = new File(uploadPath);
            //create ditectory if not exists
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString(); //generate unique name for img
            String finalFilename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + finalFilename));
            film.setFilename(finalFilename);
        } else {
            film.setFilename(logoDefault); //attach default logo
        }
        film.setLinkTrailer(linkTrailer);
        filmRepository.save(film);//add to film DB

        Set<String> filmNames = new HashSet<>();
        for (FilmNet net : filmNetRepository.findAll()) {
            filmNames.add(net.getNameFilmNet()); //find list of names Nets
        }

        for (String key : form.keySet()) {
            if (filmNames.contains(key)) { //check if choose nets exist
                //connect film and nets from DB
                filmOnNetRepository.save(new FilmOnNet(film, filmNetRepository.findByNameFilmNet(key)));
            }
        }
        model.addAttribute("page", "film");
        return "redirect:/";
    }

    /**
     * method for show one film
     */

    @GetMapping("/film/{id}")
    public String film(@PathVariable(value = "id") Long id, @AuthenticationPrincipal User user, Model model) {
        //check if url incorrect
        if (!filmRepository.existsById(id)) {
            return "redirect:/";
        }

        List<UserFilm> userFilm = userFilmRepository.findAll();
        List<Long> filmIds = new ArrayList<>();

        if (user != null) {
            for (UserFilm uf : userFilm) {
                if (user.getId().equals(uf.getUser().getId())) filmIds.add(uf.getFilm().getId()); //find liked films
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


    /**
     * method for getting View to edit one film
     */

    @PreAuthorize("hasAuthority('ADMIN')")//url for admin
    @GetMapping("/film/{id}/edit")
    public String filmEdit(@PathVariable(value = "id") Long id, Model model) {
        //check url
        if (!filmRepository.existsById(id)) {
            return "redirect:/";
        }
        Film film = filmRepository.findById(id).orElseThrow();
        model.addAttribute("film", film);
        List<FilmOnNet> filmOnNet = filmOnNetRepository.findByFilm(film);


        Iterable<FilmNet> filmnet = filmNetRepository.findAll(Sort.by(Sort.Direction.ASC, "nameFilmNet"));
        Set<String> filmNames = new HashSet<>();
        //find info about film in nets
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

    /**
     * method for edit info about one film from DB
     */

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
        //check if film in DB
        if (filmFromDB != null && !filmFromDB.getId().equals(id)) {
            Film film = filmRepository.findById(id).orElseThrow();
            model.addAttribute("film", film);
            List<FilmOnNet> filmOnNet = filmOnNetRepository.findByFilm(film);


            Iterable<FilmNet> filmnet = filmNetRepository.findAll(Sort.by(Sort.Direction.ASC, "nameFilmNet"));
            Set<String> filmNames = new HashSet<>();
            for (FilmNet net : filmNetRepository.findAll()) {
                for (FilmOnNet fn : filmOnNet) {
                    if (fn.getFilmNet().getNameFilmNet().equals(net.getNameFilmNet())) {
                        filmNames.add(net.getNameFilmNet()); //change nets
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
        //change logo if logo is new and delete old
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
            //delete old connected film and nets
        }

        Set<String> filmNames = new HashSet<>();
        for (FilmNet net : filmNetRepository.findAll()) {
            filmNames.add(net.getNameFilmNet());
            //find name of nets

        }

        for (String key : form.keySet()) {
            if (filmNames.contains(key)) {
                filmOnNetRepository.save(new FilmOnNet(film, filmNetRepository.findByNameFilmNet(key)));
                //add new connected film and nets
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
        filmRepository.save(film); //save new info about film
        model.addAttribute("page", "film");
        return "redirect:/";
    }


    /**
     * method to remove film from DB
     */

    @PreAuthorize("hasAuthority('ADMIN')")//url for admin
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


    /**
     * method to find information about Film Premiere from Model and received to View for main page
     */

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

    /**
     * method for show one film
     */
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

    /**
     * method for getting View to edit one film
     */
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


    /**
     * method for edit info about one film from DB
     */

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


    /**
     * method for define sort params
     */

    @GetMapping("/sort")
    public String sort(@RequestParam(name = "k", defaultValue = "stars") String key, Model model, HttpServletRequest request) {
        if (sortKey.equals(key)) {
            sortDirection = (sortDirection == Sort.Direction.ASC ? Sort.Direction.DESC : Sort.Direction.ASC);
        } else sortKey = key;
        String referrer = request.getHeader("referer");
        onlyLike = false;
        return "redirect:" + referrer;
    }


    /**
     * method for add films to liked list
     */

    @PostMapping("/like/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void like(@PathVariable(value = "id") Long id, @AuthenticationPrincipal User user) {
        Film film = filmRepository.findById(id).orElseThrow();
        UserFilm userFilm = new UserFilm(film, user);
        userFilmRepository.save(userFilm);
    }


    /**
     * method for delete films from liked list
     */


    @PostMapping("/relike/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void relike(@PathVariable(value = "id") Long id, @AuthenticationPrincipal User user) {
        Film film = filmRepository.findById(id).orElseThrow();
        List<UserFilm> userFilm = userFilmRepository.findByFilm(film);
        for (UserFilm uf : userFilm) {
            userFilmRepository.delete(uf);
        }

    }


    /**
     * method for find all liked films with search
     */
    @GetMapping("/findLike")
    public String findLike(Model model, HttpServletRequest request) {
        onlyLike = true;
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }


    /**
     * method for search film
     */

    @GetMapping("/searchFilm")
    public String searchFilm(@RequestParam String param, Model model, HttpServletRequest request) {
        IsSearchFilm = true;
        SearchFilmParam = param;
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }

    /**
     * method for search film premiere
     */

    @GetMapping("/searchFilmPremiere")
    public String searchFilmPremiere(@RequestParam String param, Model model, HttpServletRequest request) {
        IsSearchFilmPremiere = true;
        SearchFilmPremiereParam = param;
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }

    /**
     * method for search film in statistic
     */

    @GetMapping("/searchStatistic")
    public String searchStatistic(@RequestParam String param, Model model, HttpServletRequest request) {
        IsSearchStatistic = true;
        SearchStatisticParam = param;
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }

    /**
     * method for findAll films liked and reliked
     */

    @GetMapping("/findAll")
    public String findAll(Model model, HttpServletRequest request) {
        onlyLike = false;
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }

    /**
     * method for get View statistic
     */
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

    /**
     * method for get View about-author
     */
    @GetMapping("/about-author")
    public String aboutauthor(Model model) {
        model.addAttribute("page", "about-author");
        return "about-author";
    }

    /**
     * method for get View film's rating
     */
    @GetMapping("/film/rating")
    public String ratingFilm(Model model) {
        //rating by fees
        List<Film> filmFees = filmRepository.findFilmTop10OrderFees();
        Map<String, Long> mapFees = new LinkedHashMap<>();//data for graph
        for (Film f : filmFees) {
            mapFees.put(f.getNameFilm(), f.getCountFees() / 1000000);//price in million
        }
        model.addAttribute("mapFees", mapFees);

        //rating by stars
        List<Film> filmStars = filmRepository.findFilmTop10OrderStars();
        Map<String, Integer> mapStars = new LinkedHashMap<>(); //data for graph
        for (Film f : filmStars) {
            mapStars.put(f.getNameFilm(), f.getStars());

        }
        model.addAttribute("mapStars", mapStars);

        //rating by shows
        List<Film> filmShows = filmRepository.findFilmTop10OrderShows();
        Map<String, Integer> mapShows = new LinkedHashMap<>();//data for graph
        for (Film f : filmShows) {
            mapShows.put(f.getNameFilm(), f.getCountShows());
        }

        model.addAttribute("mapShows", mapShows);

        //rating by viewer
        List<Film> filmViewer = filmRepository.findFilmTop10OrderViewer();
        Map<String, Integer> mapViewer = new LinkedHashMap<>(); //data for graph
        for (Film f : filmViewer) {
            mapViewer.put(f.getNameFilm(), f.getCountViewer() / 1000); // find in thousand

        }
        model.addAttribute("mapViewer", mapViewer);
        model.addAttribute("page", "rating");
        return "film-rating";
    }

    /**
     * method for get View net's rating
     */

    @GetMapping("/filmnet/rating")
    public String ratingFilmNet(Model model) {
        filmCount = 0L;

        List<FilmNetRating> filmNetRatings = filmNetRepository.findNetRating();
        for (FilmNetRating f : filmNetRatings) {
            filmCount += f.getCount();
        }

        Map<String, Long> mapNet = new LinkedHashMap<>();
        for (FilmNetRating f : filmNetRatings) {
            mapNet.put(f.getNameFilmNet(), f.getCount() * 100 / filmCount); //percent nets count of film
        }

        model.addAttribute("mapNet", mapNet);
        model.addAttribute("pass", 30);
        model.addAttribute("fail", 70);
        model.addAttribute("page", "rating");
        return "film-net-rating";

    }
}
