package org.example.filmdist.controllers;
import org.example.filmdist.models.Role;
import org.example.filmdist.models.User;
import org.example.filmdist.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * Controller to define information about user, tu success and error registration
 */
@Controller
public class RegistrationController implements ErrorController{
    /**
     * Get exemplar of DB User beans
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Method to show registration fields
     */
    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    /**
     * Method to add user to DB
     */

    @PostMapping("/registration")
    //check if user exists
    public String addUser(User user, Model model) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB != null) {
            model.addAttribute("message", "Такой пользователь уже существует");
            return "registration";
        }
        user.setActive(true); //param need to Guery
        user.setRoles(Collections.singleton(Role.USER)); //set default Role
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword())); //set encrypt password
        userRepository.save(user); //save user in DB
        return "redirect:/login";
    }

    /**
     * Method to get errors
     */

    @Override
    public String getErrorPath() {
        return null;
    }

    /**
     * Method to hide debug errors from users
     */

    @GetMapping("/error")
    public String error(HttpServletRequest request) {
        return "redirect:/";
    }
}
