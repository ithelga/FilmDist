package org.example.filmdist.controllers;
import org.example.filmdist.models.FilmNet;
import org.example.filmdist.models.FilmOnNet;
import org.example.filmdist.models.NetJoin;
import org.example.filmdist.repo.FilmNetRepository;
import org.example.filmdist.repo.FilmOnNetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


/**
 * Controller to define information about net to get from Model DB and receive from View
 * it will me use in url
 */

@Controller
public class FilmNetController {

    private String logoNetDefault = "logo_net_default.png"; //default path to logo for net
    private Boolean IsSearchFilmNet = false, IsSearchFilmInNet = false; //param of searching
    private String SearchFilmNetParam, SearchFilmInNetParam; //criteria of searching

    @Value("${upload.path}")
    private String uploadPath; //path for uploads

    private String sortKey = "stars";//default sort param(col)
    private Sort.Direction sortDirection = Sort.Direction.DESC; //default sort order

    /**
     * Get exemplar of DB Net beans
     */
    @Autowired
    private FilmNetRepository filmNetRepository;

    /**
     * Get exemplar of DB Film On Net beans
     */
    @Autowired
    private FilmOnNetRepository filmOnNetRepository;

    /**
     * method for getting View adout net
     */

    @GetMapping("/filmnet")
    public String filmnet(Model model) {

        if (IsSearchFilmNet) { //check if search done
            List<FilmNet> filmnet = filmNetRepository.searchFilmNet(SearchFilmNetParam); //search
            model.addAttribute("filmnet", filmnet);
        } else {
            Iterable<FilmNet> filmnet = filmNetRepository.findAll(); //all list
            model.addAttribute("filmnet", filmnet);
        }

        List<NetJoin> selectNet = filmOnNetRepository.findNets();
        model.addAttribute("selectNet", selectNet);
        model.addAttribute("searchParam", SearchFilmNetParam);
        IsSearchFilmNet = false; //clean search param
        SearchFilmNetParam = ""; //clean search param
        model.addAttribute("page", "filmnet");
        return "film-net";
    }

    /**
     * method for getting View to add net
     */

    @PreAuthorize("hasAuthority('ADMIN')")//url for admin
    @GetMapping("/filmnet/add")
    public String filmnetAdd(Model model) {
        model.addAttribute("page", "filmnet");
        return "film-net-add";
    }

    /**
     * method for adding net to DB
     */

    @PreAuthorize("hasAuthority('ADMIN')")//url for admin
    @PostMapping("/filmnet/add")
    public String filmAdd(FilmNet filmNet, @RequestParam String nameFilmNet,
                          @RequestParam String linkFilmNet,
                          @RequestParam("file") MultipartFile file,
                          Model model) throws IOException {
        model.addAttribute("page", "filmnet");

        FilmNet filmNetFromDB = filmNetRepository.findByNameFilmNet(filmNet.getNameFilmNet());
        //check if net exists
        if (filmNetFromDB != null) {
            model.addAttribute("message", "Такая киносеть уже существует");
            return "film-net-add";
        }

        filmNet = new FilmNet(nameFilmNet, linkFilmNet);

        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String finalFilename = uuidFile + "." + file.getOriginalFilename(); //add unique logo name
            file.transferTo(new File(uploadPath + "/" + finalFilename));
            filmNet.setFilename(finalFilename);
        } else {
            filmNet.setFilename(logoNetDefault); //add default logo
        }

        filmNetRepository.save(filmNet);

        return "redirect:/filmnet";
    }


    /**
     * method for getting View to one net
     */

    @GetMapping("/filmnet/{id}")
    public String filmnetId(@PathVariable(value = "id") Long id, Model model) {
        if (!filmNetRepository.existsById(id)) {
            return "redirect:/";
        }
        Date date = new Date();
        model.addAttribute("date", date);
        FilmNet filmNet = filmNetRepository.findById(id).orElseThrow();
        model.addAttribute("filmNet", filmNet);
        List<NetJoin> selectNet;

        if (IsSearchFilmInNet) { //check search
            System.out.println(IsSearchFilmInNet);
            System.out.println(SearchFilmInNetParam);
            selectNet = filmOnNetRepository.searchFilmInNet(SearchFilmInNetParam);
            System.out.println(selectNet);

        } else if (sortKey.equals("name")) { //check sort
            selectNet = sortDirection == Sort.Direction.ASC ? filmOnNetRepository.sortNetsByNameAsc() : filmOnNetRepository.sortNetsByNameDesc();

        } else if (sortKey.equals("releaseDate")) {//check sort
            selectNet = sortDirection == Sort.Direction.ASC ? filmOnNetRepository.sortNetsByReleaseDateAsc() : filmOnNetRepository.sortNetsByReleaseDateDesc();


        } else if (sortKey.equals("stars")) {//check sort
            selectNet = sortDirection == Sort.Direction.ASC ? filmOnNetRepository.sortNetsByStarsAsc() : filmOnNetRepository.sortNetsByStarsDesc();

        } else selectNet = filmOnNetRepository.sortNetsByNameAsc();


        model.addAttribute("selectNet", selectNet);
        model.addAttribute("key", sortKey);
        model.addAttribute("direct", sortDirection == Sort.Direction.DESC ? "desk" : "ask");
        model.addAttribute("page", "filmnet");

        model.addAttribute("searchParam", SearchFilmInNetParam);
        IsSearchFilmInNet = false;
        SearchFilmInNetParam = "";
        return "film-net-id";
    }

    /**
     * method for remove net
     */

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/filmnet/{id}/remove")
    public String filmDelete(@PathVariable(value = "id") Long id, Model model) {
        FilmNet filmNet = filmNetRepository.findById(id).orElseThrow();
        if (!logoNetDefault.equals(filmNet.getFilename())) {
            File fileNameOldDir = new File(uploadPath + "/" + filmNet.getFilename());
            fileNameOldDir.delete();
        }

        List<FilmOnNet> filmOnNets = filmOnNetRepository.findByFilmNet(filmNet);
        for (FilmOnNet fn : filmOnNets) {
            filmOnNetRepository.delete(fn);
        }

        filmNetRepository.delete(filmNet);
        model.addAttribute("page", "filmnet");
        return "redirect:/filmnet";
    }


    /**
     * method for getting View to edit net
     */

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/filmnet/{id}/edit")
    public String filmEdit(@PathVariable(value = "id") Long id, Model model) {
        if (!filmNetRepository.existsById(id)) {
            return "redirect:/filmnet";
        }

        FilmNet filmNet = filmNetRepository.findById(id).orElseThrow();
        model.addAttribute("filmnet", filmNet);
        model.addAttribute("page", "filmnet");
        return "film-net-edit";
    }

    /**
     * method for editing filmnet and save changes
     */

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/filmnet/{id}/edit")
    public String filmUpdate(@PathVariable(value = "id") Long id,
                             @RequestParam String nameFilmNet,
                             @RequestParam String linkFilmNet,
                             @RequestParam("file") MultipartFile file,
                             Model model) throws IOException {

        FilmNet filmNetFromDB = filmNetRepository.findByNameFilmNet(nameFilmNet);
        if (filmNetFromDB != null && !id.equals(filmNetFromDB.getId())) {
            model.addAttribute("message", "Такая киносеть уже существует");
            FilmNet filmNet = filmNetRepository.findById(id).orElseThrow();
            model.addAttribute("filmnet", filmNet);
            model.addAttribute("page", "filmnet");
            return "film-net-edit";
        }


        FilmNet filmNet = filmNetRepository.findById(id).orElseThrow();
        filmNet.setNameFilmNet(nameFilmNet);
        filmNet.setLinkFilmNet(linkFilmNet);

        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {

            String NameOld = filmNet.getFilename();
            if (!logoNetDefault.equals(NameOld)) {
                File fileNameOldDir = new File(uploadPath + "/" + NameOld);
                fileNameOldDir.delete();
            }

            String uuidFile = UUID.randomUUID().toString();
            String finalFilename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + finalFilename));
            filmNet.setFilename(finalFilename);
        }

        filmNetRepository.save(filmNet);
        model.addAttribute("page", "filmnet");
        return "redirect:/filmnet";
    }

    /**
     * method for sort net by input param
     */

    @GetMapping("/netsort")
    public String sort(@RequestParam(name = "k", defaultValue = "3") String key, Model model, HttpServletRequest request) {
        if (sortKey.equals(key)) {
            sortDirection = (sortDirection == Sort.Direction.ASC ? Sort.Direction.DESC : Sort.Direction.ASC);
        } else sortKey = key;
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }

    /**
     * method for search net
     */
    @GetMapping("/searchFilmNet")
    public String searchFilmNet(@RequestParam String param, Model model, HttpServletRequest request) {
        IsSearchFilmNet = true;
        SearchFilmNetParam = param;
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }

    /**
     * method for search film in concrete net
     */
    @GetMapping("/searchFilmInNet")
    public String searchFilm(@RequestParam String param, Model model, HttpServletRequest request) {
        IsSearchFilmInNet = true;
        SearchFilmInNetParam = param;
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }
}

