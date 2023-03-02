package com.jathursh.springSecurity27th.service;

import com.jathursh.springSecurity27th.entity.User;
import com.jathursh.springSecurity27th.entity.VerificationToken;
import com.jathursh.springSecurity27th.model.UserModel;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    User findUserByEmail(String email);

    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    boolean checkIfValidPassword(User user, String oldPassword);
}
