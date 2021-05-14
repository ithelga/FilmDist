package org.example.filmdist.repo;

import org.example.filmdist.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface to CRUD with table User
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * method to find user in UserDB by name
     */
    User findByUsername(String username);
}
