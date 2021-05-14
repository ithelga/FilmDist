package org.example.filmdist.config;

import org.example.filmdist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


/**
 * class to set up work of Spring Security
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/registration", "/filmnet/**", "/statistic",
                        "/filmpremiere/**", "/about-author", "/static/**",
                        "/images/**", "/sort", "/netsort", "/film/**",
                        "/searchFilm", "/searchFilmPremiere", "/searchStatistic", "/searchFilmNet",
                        "/film/rating", "/filmnet/rating",
                        "/error", "/searchFilmInNet").permitAll() //url which user can visit without authentication
                .antMatchers("/css/****").permitAll()
                .antMatchers("/img/****").permitAll()
                .antMatchers("/js/****").permitAll()

                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login") //use Spring Security
                .failureUrl("/login-error")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/login/success", true) //url to go after login
                .permitAll()
                .and()
                .csrf().disable()
                .logout()
                .permitAll();
    }

    /**
     * class to set up login and work with user
     */

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(new BCryptPasswordEncoder()); //encrypt password
    }

}