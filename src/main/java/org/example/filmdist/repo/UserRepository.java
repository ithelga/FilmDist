package org.example.filmdist.repo;

import org.example.filmdist.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <User, Long>{
    User findByUsername(String username);
}
