package org.example.filmdist.controllers;

import org.example.filmdist.models.Film;
import org.example.filmdist.models.Role;
import org.example.filmdist.models.User;
import org.example.filmdist.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    @Autowired
    private UserRepository userRepository;


    @GetMapping
    public String userList(Model model) {
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "user-list";
    }

    @GetMapping("{user}")
    public String userEdit(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "user-edit";
    }

    @PostMapping
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("id") User user,
            Model model) {
        boolean hasRole = false;

        User userFromDB = userRepository.findByUsername(username);
        if (userFromDB != null && !userFromDB.getId().equals(user.getId())) {
            model.addAttribute("message", "Такой пользователь уже существует");
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
            return "user-edit";
        }

        user.setUsername(username);
        user.getRoles().clear();
        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());
        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
                hasRole = true;
            }
        }
        if (!hasRole) user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);
        return "redirect:/user";
    }

}
