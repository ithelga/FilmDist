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

@Controller
public class FilmNetController {

    private String logoNetDefault = "logo_net_default.png";
    private Boolean IsSearchFilmNet = false;
    private String SearchFilmNetParam;

    @Value("${upload.path}")
    private String uploadPath;
    private String sortKey = "stars";
    private Sort.Direction sortDirection = Sort.Direction.DESC;


    @Autowired
    private FilmNetRepository filmNetRepository;


    @Autowired
    private FilmOnNetRepository filmOnNetRepository;

    @GetMapping("/filmnet")
    public String filmnet(Model model) {

        if (IsSearchFilmNet) {
            List<FilmNet> filmnet = filmNetRepository.searchFilmNet(SearchFilmNetParam);
            model.addAttribute("filmnet", filmnet);
        } else {
            Iterable<FilmNet> filmnet = filmNetRepository.findAll();
            model.addAttribute("filmnet", filmnet);
        }


        List<NetJoin> selectNet = filmOnNetRepository.findNets();
        model.addAttribute("selectNet", selectNet);
        model.addAttribute("searchParam", SearchFilmNetParam);
        IsSearchFilmNet = false;
        SearchFilmNetParam = "";
        model.addAttribute("page", "filmnet");

        return "film-net";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/filmnet/add")
    public String filmnetAdd(Model model) {
        model.addAttribute("page", "filmnet");
        return "film-net-add";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/filmnet/add")
    public String filmAdd(FilmNet filmNet, @RequestParam String nameFilmNet,
                          @RequestParam String linkFilmNet,
                          @RequestParam("file") MultipartFile file,
                          Model model) throws IOException {
        model.addAttribute("page", "filmnet");

        FilmNet filmNetFromDB = filmNetRepository.findByNameFilmNet(filmNet.getNameFilmNet());
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
            String finalFilename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + finalFilename));
            filmNet.setFilename(finalFilename);
        } else {
            filmNet.setFilename(logoNetDefault);
        }

        filmNetRepository.save(filmNet);

        return "redirect:/filmnet";
    }

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
//        List<NetJoin> selectNet = filmOnNetRepository.findNetsSort("D", sortKey);

        if (sortKey.equals("name")) {
            selectNet = sortDirection == Sort.Direction.ASC ? filmOnNetRepository.sortNetsByNameAsc() : filmOnNetRepository.sortNetsByNameDesc();

        } else if (sortKey.equals("releaseDate")) {
            selectNet = sortDirection == Sort.Direction.ASC ? filmOnNetRepository.sortNetsByReleaseDateAsc() : filmOnNetRepository.sortNetsByReleaseDateDesc();


        } else if (sortKey.equals("stars")) {
            selectNet = sortDirection == Sort.Direction.ASC ? filmOnNetRepository.sortNetsByStarsAsc() : filmOnNetRepository.sortNetsByStarsDesc();

        } else selectNet = filmOnNetRepository.sortNetsByNameAsc();

        model.addAttribute("selectNet", selectNet);
        model.addAttribute("key", sortKey);
        model.addAttribute("direct", sortDirection == Sort.Direction.DESC ? "desk" : "ask");
        model.addAttribute("page", "filmnet");
        return "film-net-id";
    }

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

    @GetMapping("/netsort")
    public String sort(@RequestParam(name = "k", defaultValue = "3") String key, Model model, HttpServletRequest request) {
        if (sortKey.equals(key)) {
            sortDirection = (sortDirection == Sort.Direction.ASC ? Sort.Direction.DESC : Sort.Direction.ASC);
        } else sortKey = key;
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }

    @GetMapping("/searchFilmNet")
    public String searchFilmNet(@RequestParam String param, Model model, HttpServletRequest request) {
        IsSearchFilmNet = true;
        SearchFilmNetParam = param;
        String referrer = request.getHeader("referer");
        return "redirect:" + referrer;
    }


}

