package com.example.springsecurity.repository;

import com.example.springsecurity.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT t FROM Token t inner join User u on t.user.id = u.id where t.user.id = :userId and t.loggedOut = false")
    List<Token> findAllTokenByUser(Long userId);


    Optional<Token> findByToken(String token);
}
