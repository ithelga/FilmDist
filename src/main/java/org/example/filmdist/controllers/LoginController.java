package org.example.filmdist.controllers;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * Controller to define information about user, tu success and error login
 */

@Controller
public class LoginController {

    private String referrer;

    /**
     * method to sent error if login or password is not correct
     */
    @GetMapping("/login-error")
    public String login(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String errorMessage = null;
        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                errorMessage = ex.getMessage();
            }
        }
        model.addAttribute("errorMessage", "Неверные данные. Попробуйте еще раз");
        return "login";
    }


    /**
     * method to define page before login and get view login
     */
    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        referrer = request.getHeader("referer");
        return "login";
    }


    /**
     * method to redirect to rhe page before authorization
     */
    @GetMapping("/login/success")
    public String loginsuccess() {
        if (referrer == null || referrer.equals("") || referrer.contains("/registration") || referrer.contains("/login"))
            // check if page before login was registration or error(incorrect login or password), redirect in main page
            return "redirect:/";
        return "redirect:" + referrer; //redirect to previous
    }
}
