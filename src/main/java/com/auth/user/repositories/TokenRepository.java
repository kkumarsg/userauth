package com.auth.user.repositories;


import com.auth.user.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByValueAndIsDeletedEquals(String token, boolean isDeleted);

    Optional<Token> findByValueAndIsDeletedEqualsAndExpirydateGreaterThan(
            String token, boolean isDeleted, Date expiryDate);
}
