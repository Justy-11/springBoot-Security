package com.jathursh.springSecurity27th.controller;

import com.jathursh.springSecurity27th.entity.User;
import com.jathursh.springSecurity27th.entity.VerificationToken;
import com.jathursh.springSecurity27th.event.RegistrationCompleteEvent;
import com.jathursh.springSecurity27th.model.PasswordModel;
import com.jathursh.springSecurity27th.model.UserModel;
import com.jathursh.springSecurity27th.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

// API (dto)-> controller (dto)-> service (entity)-> repo

@Slf4j
@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;

    // event for creating sending verification email to user when registering
    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request){
        User user = userService.registerUser(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return "Success";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token){
        String result = userService.validateVerificationToken(token);
        if(result.equalsIgnoreCase("valid")){
            return "User verified successfully";
        }
        return "Bad user";
    }

    // by using old token
    @GetMapping("/resendVerificationToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request){

        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        User user  = verificationToken.getUser();
        resendVerificationTokenMail(user, applicationUrl(request), verificationToken);
        return "Verification link sent successfully";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request){

        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url= "";

        if(user != null){
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user,token);
            url = passwordResetTokenMail(user, applicationUrl(request), token);
        }
        return url;
    }

    // march 1st
    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel){
        User user = userService.findUserByEmail(passwordModel.getEmail());
        if(!userService.checkIfValidPassword(user, passwordModel.getOldPassword())){
            return "Invalid old password";
        }

        userService.changePassword(user,passwordModel.getNewPassword());
        return "Password changed successfully";

    }

    private String passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url = applicationUrl + "/savePassword?token=" + token;

        //sendVerificationEmail()
        log.info("Click the link to reset your Password: {}", url);

        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token,
                               @RequestBody PasswordModel passwordModel){

        String result = userService.validatePasswordResetToken(token);

        if(!result.equalsIgnoreCase("valid")){
            return "Invalid token";
        }

        Optional<User> user = userService.getUserByPasswordResetToken(token);

        if(user.isPresent()){
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return "Password reset successful";
        }else{
            return "Invalid token";
        }

    }

    private void resendVerificationTokenMail(User user, String applicationUrl, VerificationToken verificationToken) {

        //send mail to user
        String url = applicationUrl + "verifyRegistration?token=" + verificationToken.getToken();

        // actually need to send verification email but here just logged
        log.info("Click the link to verify your account: {}", url);
    }


    private String applicationUrl(HttpServletRequest request) {

        return "http://" +  request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

}
