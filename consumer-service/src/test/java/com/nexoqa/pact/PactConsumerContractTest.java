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

        // @Autowired
        // private ClientBuilder clientBuilder;

        @Autowired
        private SubscribersBuilder subscribersBuilder;

        @Autowired
        private SubscriberService subscriberService;

        @Autowired
        private EmailService emailService;

        private Clients testClientsData;

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
                                .integerType("phoneNumber", userData.getPhoneNumber());

                PactDslJsonBody client = new PactDslJsonBody()
                                .booleanType("activated")
                                .object("user")
                                .stringType("name", userData.getName())
                                .stringType("lastName", userData.getLastName())
                                .stringType("address", userData.getAddress())
                                .integerType("age", userData.getAge())
                                .integerType("phoneNumber", userData.getPhoneNumber());

                return builder
                                .given("test consumer service -  subscribe")
                                .uponReceiving("a request to create a new client")
                                .path(END_POINT_CLIENT)
                                .method("POST")
                                .headers(headers)
                                // .body(new Gson().toJson(userData, User.class))
                                .body(user)
                                .willRespondWith()
                                .headers(headers)
                                .status(200)
                                // .body(new Gson().toJson(clientData, Client.class))
                                .body(client)
                                .uponReceiving("a request to notify all active clients")
                                .path(END_POINT_CLIENTS)
                                .method("GET")
                                .willRespondWith()
                                .status(200)
                                .headers(headers)
                                .body(clients)
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

}
