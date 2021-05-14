package org.example.filmdist.controllers;


import org.example.filmdist.models.Role;
import org.example.filmdist.models.User;
import org.example.filmdist.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Controller to define information about user, to get for admin in View
 */

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')") //only admin has access to following url
public class UserController {


    /**
     * Get exemplar of DB User beans
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Method to show all users
     */
    @GetMapping
    public String userList(Model model) {
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "user-list";
    }

    /**
     * Method to show name role of user
     */

    @GetMapping("{user}")
    public String userEdit(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "user-edit";
    }

    /**
     * Method to change name or role of user
     */

    @PostMapping
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("id") User user,
            Model model) {
        boolean hasRole = false;

        //Chech if user exists
        User userFromDB = userRepository.findByUsername(username);
        if (userFromDB != null && !userFromDB.getId().equals(user.getId())) {
            model.addAttribute("message", "Такой пользователь уже существует");
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
            return "user-edit";
        }

        user.setUsername(username); //set name for user
        user.getRoles().clear();
        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());
        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key)); //set role or roles
                hasRole = true;
            }
        }

        //Check if role not assigned, set User role
        if (!hasRole) user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);
        return "redirect:/user";
    }

}
