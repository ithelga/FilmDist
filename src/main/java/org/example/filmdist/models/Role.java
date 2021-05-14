package org.example.filmdist.models;

import org.springframework.security.core.GrantedAuthority;


/**
 * Define model-entity-enum to create DB role
 */
public enum Role implements GrantedAuthority {
    USER, ADMIN; //role

    @Override
    public String getAuthority() {
        return name();
    }
}
