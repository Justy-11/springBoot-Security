package com.jathursh.springSecurity27th.event.listener;

import com.jathursh.springSecurity27th.entity.User;
import com.jathursh.springSecurity27th.event.RegistrationCompleteEvent;
import com.jathursh.springSecurity27th.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // create verification token for user with the link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token, user);

        //send mail to user
        String url = event.getApplicationUrl() + "verifyRegistration?token=" + token;

        //sendVerificationEmail()
        log.info("Click the link to verify your account: {}", url);

    }
}
