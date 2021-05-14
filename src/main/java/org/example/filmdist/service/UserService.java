package org.example.filmdist.service;

import org.example.filmdist.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * This is service for getting extended information about user
 */

@Service
public class UserService implements UserDetailsService {

    /**
     * Get exemplar of DB User beans
     */
    @Autowired
    private UserRepository userRepository;


    /**
     * Method to find user in DB by login and check errors
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
}
