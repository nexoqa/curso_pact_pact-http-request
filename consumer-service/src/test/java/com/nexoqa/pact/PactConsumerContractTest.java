package com.nexoqa.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.google.gson.Gson;
import com.nexoqa.builder.SubscribersBuilder;
import com.nexoqa.model.Client;
import com.nexoqa.model.Clients;
import com.nexoqa.model.User;
import com.nexoqa.service.EmailService;
import com.nexoqa.service.SubscriberService;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PactConsumerContractTest {

//     @Autowired
//     private ClientBuilder clientBuilder;

    @Autowired
    private SubscribersBuilder subscribersBuilder;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private EmailService emailService;

    private Clients testClientsData;

    private Client modifiedClientData;

    private static final String END_POINT_CLIENT = "/client-provider/client";
    private static final String END_POINT_CLIENTS = "/client-provider/clients";

    @Rule
    public PactProviderRule rule = new PactProviderRule("client-provider", this);


    @Pact(provider = "client-provider", consumer = "consumer-service")
    public RequestResponsePact subscribePact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        testClientsData = subscribersBuilder.build();

        Client clientData = testClientsData.getClients().get(0);

        User userData = clientData.getUser();


        PactDslJsonBody clients = new PactDslJsonBody()
                .minArrayLike("clients", 1)
                .booleanType("activated")
                .object("user")
                .stringType("name")
                .stringType("lastName")
                .stringType("address")
                .integerType("age")
                .integerType("phoneNumber");


        PactDslJsonBody user = new PactDslJsonBody()
                .stringType("name", userData.getName())
                .stringType("lastName", userData.getLastName())
                .stringType("address", userData.getAddress())
                .integerType("age", userData.getAge())
                .integerType("phoneNumber", userData.getPhoneNumber())
                .stringType("email", userData.getEmail());
        
        PactDslJsonBody user2 = new PactDslJsonBody() 
                .stringType("name")
                .stringType("lastName")
                .stringType("address")
                .integerType("age")
                .integerType("phoneNumber");

        PactDslJsonBody client = new PactDslJsonBody()
                .booleanType("activated")
                .object("user")
                .stringType("name", userData.getName())
                .stringType("lastName", userData.getLastName())
                .stringType("address", userData.getAddress())
                .integerType("age", userData.getAge())
                .integerType("phoneNumber", userData.getPhoneNumber())
                .stringType("email", userData.getEmail());

        return builder
                .given("test consumer service -  subscribe")
                        .uponReceiving("a request to create a new client")
                                .path(END_POINT_CLIENT)
                                .method("POST")
                                .headers(headers)
                                .body(new Gson().toJson(userData, User.class))
                                // .body(user)
                        .willRespondWith()
                                .headers(headers)
                                .status(200)
                                .body(new Gson().toJson(clientData, Client.class))
                                // .body(client)
                        .uponReceiving("a request to notify all active clients")
                                .path(END_POINT_CLIENTS)
                                .method("GET")
                        .willRespondWith()
                                .status(200)
                                .headers(headers)
                                .body(clients)
                .toPact();
    }


    @Pact(provider = "client-provider", consumer = "consumer-service")
    public RequestResponsePact unsubscribePact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        testClientsData = subscribersBuilder.build();

        Client clientData = testClientsData.getClients().get(0);

        User userData = clientData.getUser();

        PactDslJsonBody user = new PactDslJsonBody()
                .stringType("name", userData.getName())
                .stringType("lastName", userData.getLastName())
                .stringType("address", userData.getAddress())
                .integerType("age", userData.getAge())
                .integerType("phoneNumber", userData.getPhoneNumber())
                .stringType("email", userData.getEmail());

        return builder
                .given("test consumer service -  un subscribe")
                        .uponReceiving("a request to create a new client")
                                .path(END_POINT_CLIENT)
                                .method("POST")
                                .headers(headers)
                                .body(user)
                        .willRespondWith()
                                .headers(headers)
                                .status(200)
                                .body(new Gson().toJson(clientData, Client.class))
                                // .body(client)
                        .uponReceiving("a request to delete a new client")
                                .path(END_POINT_CLIENT)
                                .method("DELETE")
                                .headers(headers)
                                .body(user)
                        .willRespondWith()
                                .status(200)      
                .toPact();
    }

    @Pact(provider = "client-provider", consumer = "consumer-service")
    public RequestResponsePact unsubscribeNoUserPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        testClientsData = subscribersBuilder.build();

        Client clientData = testClientsData.getClients().get(0);

        User userData = clientData.getUser();

        PactDslJsonBody user = new PactDslJsonBody()
                .stringType("name", userData.getName())
                .stringType("lastName", userData.getLastName())
                .stringType("address", userData.getAddress())
                .integerType("age", userData.getAge())
                .integerType("phoneNumber", userData.getPhoneNumber())
                .stringType("email", userData.getEmail());

        return builder
                .given("test consumer service -  un subscribe no user")
                        .uponReceiving("a request to delete a non existing client")
                                .path(END_POINT_CLIENT)
                                .method("DELETE")
                                .headers(headers)
                                .body(user)
                        .willRespondWith()
                                .status(409)        
                .toPact();
    }

    @Pact(provider = "client-provider", consumer = "consumer-service")
    public RequestResponsePact modifySubscriptionPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        testClientsData = subscribersBuilder.build();

        Client clientData = testClientsData.getClients().get(0);
        
        User userData = clientData.getUser();

        User userToModify = new User();
        userToModify.setAge(userData.getAge() + 1);
        userToModify.setName(userData.getName());
        userToModify.setAddress(userData.getAddress());
        userToModify.setEmail(userData.getEmail());
        userToModify.setLastName(userData.getLastName());
        userToModify.setPhoneNumber(userData.getPhoneNumber());

        modifiedClientData = new Client(userToModify,true);

        PactDslJsonBody user = new PactDslJsonBody()
                .stringType("name", userData.getName())
                .stringType("lastName", userData.getLastName())
                .stringType("address", userData.getAddress())
                .integerType("age", userData.getAge())
                .integerType("phoneNumber", userData.getPhoneNumber())
                .stringType("email", userData.getEmail());

        PactDslJsonBody modifiedUser = new PactDslJsonBody()
                .stringType("name", userToModify.getName())
                .stringType("lastName", userToModify.getLastName())
                .stringType("address", userToModify.getAddress())
                .integerType("age", userToModify.getAge())
                .integerType("phoneNumber", userToModify.getPhoneNumber())
                .stringType("email", userToModify.getEmail());
        
        PactDslJsonBody modifiedClient = new PactDslJsonBody().object("user", modifiedUser).booleanType("activated");

        return builder
                .given("test consumer service -  modify subscription")
                        .uponReceiving("a request to create a new client") //nombre de la prueba
                                .path(END_POINT_CLIENT)
                                .method("POST")
                                .headers(headers)
                                .body(user)
                        .willRespondWith()
                                .headers(headers)
                                .status(200)
                                .body(new Gson().toJson(clientData, Client.class))
                                // .body(client)
                        .uponReceiving("a request to modify the client")
                                .path(END_POINT_CLIENT)
                                .method("PUT")
                                .headers(headers)
                                .body(modifiedUser)
                        .willRespondWith()
                                .headers(headers)
                                .status(200)  
                                .body(modifiedClient)    
                .toPact();
    }
    

    @Test
    @PactVerification(value = "client-provider", fragment = "subscribePact")
    public void runSubscribePactTest() {
        MockServer server = rule.getMockServer();
        subscriberService.setBackendURL("http://localhost:" + server.getPort());
        ResponseEntity<Client> subscribedClient = subscriberService
                .subscribeUser(testClientsData.getClients().get(0).getUser());
        assertTrue(subscribedClient.getStatusCode().is2xxSuccessful());

        emailService.setBackendURL("http://localhost:" + server.getPort());
        ResponseEntity<List<Client>> notifiedClients = emailService.notifyActiveUsers();
        assertTrue(notifiedClients.getStatusCode().is2xxSuccessful());
    }

    @Test
    @PactVerification(value = "client-provider", fragment = "unsubscribePact")
    public void runUnsubscribePactTest() {
        MockServer server = rule.getMockServer();
        subscriberService.setBackendURL("http://localhost:" + server.getPort());

        ResponseEntity<Client> subscribedClient = subscriberService
                .subscribeUser(testClientsData.getClients().get(0).getUser());
        assertTrue(subscribedClient.getStatusCode().is2xxSuccessful());

        ResponseEntity<Void> unsubscribedClient = subscriberService
                .unsubscribeUser(testClientsData.getClients().get(0).getUser());
        assertTrue(unsubscribedClient.getStatusCode().is2xxSuccessful());

    }

    @Test
    @PactVerification(value = "client-provider", fragment = "unsubscribeNoUserPact")
    public void runUnsubscribeNonExistingPactTest() {
        MockServer server = rule.getMockServer();
        subscriberService.setBackendURL("http://localhost:" + server.getPort());

        ResponseEntity<Void> unsubscribedInvalidClient = subscriberService
        .unsubscribeUser(testClientsData.getClients().get(0).getUser());
        assertTrue(unsubscribedInvalidClient.getStatusCode().is4xxClientError());

    }

    @Test
    @PactVerification(value = "client-provider", fragment = "modifySubscriptionPact")
    public void runModifySubscriptionPactTest() {
        MockServer server = rule.getMockServer();
        subscriberService.setBackendURL("http://localhost:" + server.getPort());

        ResponseEntity<Client> subscribedClient = subscriberService
                .subscribeUser(testClientsData.getClients().get(0).getUser());
        assertTrue(subscribedClient.getStatusCode().is2xxSuccessful());

        ResponseEntity<Client> modifiedClient = subscriberService
                .modifyUser(modifiedClientData.getUser());
        assertTrue(modifiedClient.getStatusCode().is2xxSuccessful());

    }

}
