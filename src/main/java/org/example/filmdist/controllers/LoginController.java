package org.example.filmdist.controllers;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    private String referrer;

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

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        referrer = request.getHeader("referer");
        return "login";

    }

    @GetMapping("/login/success")
    public String loginsuccess() {
        if (referrer == null || referrer.equals("") || referrer.endsWith("/registration") || referrer.endsWith("/login"))
            return "redirect:/";
        return "redirect:" + referrer;
    }


}
