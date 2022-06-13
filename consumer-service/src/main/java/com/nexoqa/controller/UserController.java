package com.nexoqa.controller;

import com.nexoqa.model.Client;
import com.nexoqa.model.User;
import com.nexoqa.service.EmailService;
import com.nexoqa.service.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class UserController {

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private EmailService emailService;

    @RequestMapping(value = "/subscribe", method = POST, produces = "application/json")
    private ResponseEntity<Client> subscribeUser(@RequestBody User user) {
        return subscriberService.subscribeUser(user);
    }

    @RequestMapping(value = "/email/send", method = POST, produces = "application/json")
    private ResponseEntity<List<Client>> notifyUser() {
        return emailService.notifyActiveUsers();
    }

    @RequestMapping(value="/unsubscribe", method = RequestMethod.DELETE, produces = "application/json")
    private ResponseEntity<Void> unsubscribeUser(@RequestBody User user) {
        return subscriberService.unsubscribeUser(user);
    }

    @RequestMapping(value = "/subscribe", method = RequestMethod.PUT, produces = "application/json")
    private ResponseEntity<Client> modifyUser(@RequestBody User user) {
        return subscriberService.modifyUser(user);
    }
}
