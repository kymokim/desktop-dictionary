package com.kymokim.desktopdictionary.auth.repository;

import com.kymokim.desktopdictionary.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Auth findByEmail(String email);
    Auth findByEmailAndPassword(String email, String password);

    boolean existsByEmail(String email);
}
