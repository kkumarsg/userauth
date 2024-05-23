package com.auth.user.services;

import com.auth.user.dtos.EmailFormat;
import com.auth.user.models.Token;
import com.auth.user.models.User;
import com.auth.user.repositories.TokenRepository;
import com.auth.user.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // create this bean and use it.
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;



    public User signUp(String name, String email, String password){

        // skipping email verification part here.
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            // throw user is already present
        }
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));

        // take the email id that for whom you want to send and put it in the kafka queue.
        try {
            kafkaTemplate.send("sendEmail", objectMapper.writeValueAsString(getMessage(user)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return userRepository.save(user);
    }

    private EmailFormat getMessage(User user) {
        EmailFormat message = new EmailFormat();
        message.setTo(user.getEmail());
        message.setContent("Successfully signed up");
        message.setSubject("Sign up success ");
        message.setFrom("keerthikumarsg@gmail.com");

        return message;
    }

    public Token login(String email, String password) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            // throw user is not valid
            return null;
        }

        User user = optionalUser.get();
        if (!bCryptPasswordEncoder.matches(password, user.getHashedPassword())) {
            // throw password is wrong
            return null;
        }

        Token token = new Token();
        token.setUser(user);
        token.setExpirydate(get30DaysLaterDate());
        token.setValue(UUID.randomUUID().toString());

        return tokenRepository.save(token);
    }

    private Date get30DaysLaterDate() {

        Date date = new Date();

        // Convert date to calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Add 30 days
        calendar.add(Calendar.DAY_OF_MONTH, 30);

        // extract date from calendar
        return calendar.getTime();
    }

    public void logout(String token) {

        Optional<Token> tokenOptional
                = tokenRepository.findByValueAndIsDeletedEquals(token, false);

        if (tokenOptional.isEmpty()) {
            // throw an exception saying token is not present or already deleted.
            return ;
        }

        Token updatedToken = tokenOptional.get();
        updatedToken.setDeleted(true);
        tokenRepository.save(updatedToken);

    }

    public boolean validateToken(String token) {

        /*
        1. Check if the token is present in db
        2. Check if the token is not deleted
        3. Check if the token is not expired
         */

        Optional<Token> tokenOptional =
                tokenRepository.findByValueAndIsDeletedEqualsAndExpirydateGreaterThan(
                token, false, new Date());

        return tokenOptional.isPresent();
    }
}
