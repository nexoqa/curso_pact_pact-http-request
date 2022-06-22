package com.nexoqa.controller;

import com.nexoqa.model.Client;
import com.nexoqa.model.Clients;
import com.nexoqa.model.User;
import com.nexoqa.service.ClientService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
public class ClientController {

    private static Logger logger = LogManager.getLogger(ClientController.class);

    @Autowired
    private ClientService clientService;

    @RequestMapping(value = "/client", method = GET, produces = "application/json")
    private @ResponseBody ResponseEntity<Client> getClient(@RequestParam(value = "id") Integer id) {
        logger.info("getting client -> " + id);
        Client requestedClient = clientService.getClient(id);
        return Optional
                .ofNullable(requestedClient)
                .map(client -> ResponseEntity.ok().body(requestedClient))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/client", method = DELETE, produces = "application/json")
    private @ResponseBody ResponseEntity<Void> deleteClient(@RequestBody User user) {
        logger.info("deleting client -> " + user.toString());
        if (clientService.isRegistered(user.getId())) {
            clientService.deleteClient(user);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(value = "/client", method = PUT, produces = "application/json")
    private @ResponseBody ResponseEntity<Client> udpateClient(@RequestBody User user) {
        logger.info("modifing client -> " + user.toString());
        if (clientService.isRegistered(user.getId())) {
            Client requestedClient = clientService.updateClient(user);
            return Optional
                    .ofNullable(requestedClient)
                    .map(client -> ResponseEntity.ok().body(requestedClient))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(value = "/clients", method = GET, produces = "application/json")
    private @ResponseBody ResponseEntity<Clients> getClients() {
        Clients requestedClients = clientService.getClients();
        logger.info("getting clients -> " + requestedClients.toString());
        return Optional
                .ofNullable(requestedClients)
                .map(client -> ResponseEntity.ok().body(requestedClients))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/client", method = POST, produces = "application/json")
    private @ResponseBody ResponseEntity<Client> createClient(@RequestBody User user) {
        logger.info("creating client -> " + user.toString());

        if (clientService.isRegistered(user.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            Client client = clientService.createClient(user);
            return ResponseEntity
                    .ok()
                    .header("Content-type", "application/json")
                    .body(client);
        }

    }
}
