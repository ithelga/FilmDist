package org.example.filmdist;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
public class FilmDist {

    /**
     * This is the start point of application
     */
    public static void main(String[] args) {
        SpringApplication.run(FilmDist.class, args);
        System.out.println("FilmDist run on http://localhost:8085/");
    }
}
