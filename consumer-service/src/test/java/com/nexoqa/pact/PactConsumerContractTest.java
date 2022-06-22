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

        private Clients testDeleteClientsData;

        private Client clientToUpdate;

        private Client clientUpdated;

        private Client clientToUpdateFail;

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
                                .integerType("id")
                                .stringType("name")
                                .stringType("lastName")
                                .stringType("address")
                                .stringType("email")
                                .integerType("age")
                                .integerType("phoneNumber");

                PactDslJsonBody user = new PactDslJsonBody()
                                .integerType("id", userData.getId())
                                .stringType("name", userData.getName())
                                .stringType("lastName", userData.getLastName())
                                .stringType("address", userData.getAddress())
                                .stringType("email", userData.getEmail())
                                .integerType("age", userData.getAge())
                                .integerType("phoneNumber", userData.getPhoneNumber());

                PactDslJsonBody client = new PactDslJsonBody()
                                .booleanType("activated")
                                .object("user")
                                .integerType("id", userData.getId())
                                .stringType("name", userData.getName())
                                .stringType("lastName", userData.getLastName())
                                .stringType("address", userData.getAddress())
                                .stringType("email", userData.getEmail())
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

        @Pact(provider = "client-provider", consumer = "consumer-service")
        public RequestResponsePact unSubscribePact(PactDslWithProvider builder) {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                testDeleteClientsData = subscribersBuilder.build();

                Client clientData = testDeleteClientsData.getClients().get(0);

                User userData = clientData.getUser();

                PactDslJsonBody user = new PactDslJsonBody()
                                .integerType("id", userData.getId())
                                .stringType("name", userData.getName())
                                .stringType("lastName", userData.getLastName())
                                .stringType("address", userData.getAddress())
                                .stringType("email", userData.getEmail())
                                .integerType("age", userData.getAge())
                                .integerType("phoneNumber", userData.getPhoneNumber());

                PactDslJsonBody client = new PactDslJsonBody()
                                .booleanType("activated")
                                .object("user")
                                .integerType("id", userData.getId())
                                .stringType("name", userData.getName())
                                .stringType("lastName", userData.getLastName())
                                .stringType("address", userData.getAddress())
                                .stringType("email", userData.getEmail())
                                .integerType("age", userData.getAge())
                                .integerType("phoneNumber", userData.getPhoneNumber());

                return builder
                                .given("test consumer service - unsubscribe")
                                .uponReceiving("a request to create a new client to delete")
                                .path(END_POINT_CLIENT)
                                .method("POST")
                                .headers(headers)
                                .body(user)
                                .willRespondWith()
                                .headers(headers)
                                .status(200)
                                .body(client)
                                .uponReceiving("a request to delete user")
                                .path(END_POINT_CLIENT)
                                .method("DELETE")
                                .headers(headers)
                                .body(user)
                                .willRespondWith()
                                .status(200)
                                .toPact();
        }

        @Pact(provider = "client-provider", consumer = "consumer-service")
        public RequestResponsePact failUnSubscribePact(PactDslWithProvider builder) {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                testDeleteClientsData = subscribersBuilder.build();

                Client clientData = testDeleteClientsData.getClients().get(0);

                User userData = clientData.getUser();

                PactDslJsonBody user = new PactDslJsonBody()
                                .integerType("id", userData.getId())
                                .stringType("name", userData.getName())
                                .stringType("lastName", userData.getLastName())
                                .stringType("address", userData.getAddress())
                                .stringType("email", userData.getEmail())
                                .integerType("age", userData.getAge())
                                .integerType("phoneNumber", userData.getPhoneNumber());

                return builder
                                .given("test consumer service - fail unsubscribe")
                                .uponReceiving("fail delete user")
                                .path(END_POINT_CLIENT)
                                .method("DELETE")
                                .headers(headers)
                                .body(user)
                                .willRespondWith()
                                .status(404)
                                .toPact();
        }

        @Pact(provider = "client-provider", consumer = "consumer-service")
        public RequestResponsePact modifyUserPact(PactDslWithProvider builder) {

                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                clientToUpdate = new Client(
                                new User(
                                                2,
                                                "Name",
                                                "LastName",
                                                "Address",
                                                "email@email.com",
                                                50,
                                                1234567890),
                                true);

                clientUpdated = new Client(
                                new User(2,
                                                "Name2",
                                                "LastName1",
                                                "Address1",
                                                "email1@email.com",
                                                51,
                                                1234567891),
                                true);

                PactDslJsonBody user = new PactDslJsonBody()
                                .integerType("id", clientToUpdate.getUser().getId())
                                .stringType("name", clientToUpdate.getUser().getName())
                                .stringType("lastName", clientToUpdate.getUser().getLastName())
                                .stringType("address", clientToUpdate.getUser().getAddress())
                                .stringType("email", clientToUpdate.getUser().getEmail())
                                .integerType("age", clientToUpdate.getUser().getAge())
                                .integerType("phoneNumber", clientToUpdate.getUser().getPhoneNumber());

                PactDslJsonBody client = new PactDslJsonBody()
                                .booleanType("activated", true)
                                .object("user")
                                .integerType("id", clientToUpdate.getUser().getId())
                                .stringType("name", clientToUpdate.getUser().getName())
                                .stringType("lastName", clientToUpdate.getUser().getLastName())
                                .stringType("address", clientToUpdate.getUser().getAddress())
                                .stringType("email", clientToUpdate.getUser().getEmail())
                                .integerType("age", clientToUpdate.getUser().getAge())
                                .integerType("phoneNumber", clientToUpdate.getUser().getPhoneNumber());

                PactDslJsonBody userModified = new PactDslJsonBody()
                                .integerType("id", clientToUpdate.getUser().getId())
                                .stringType("name", clientUpdated.getUser().getName())
                                .stringType("lastName", clientUpdated.getUser().getLastName())
                                .stringType("address", clientUpdated.getUser().getAddress())
                                .stringType("email", clientUpdated.getUser().getEmail())
                                .integerType("age", clientUpdated.getUser().getAge() + 1)
                                .integerType("phoneNumber", clientUpdated.getUser().getPhoneNumber());

                PactDslJsonBody clientModified = new PactDslJsonBody()
                                .booleanType("activated", true)
                                .object("user")
                                .integerType("id", clientUpdated.getUser().getId())
                                .stringType("name", clientUpdated.getUser().getName())
                                .stringType("lastName", clientUpdated.getUser().getLastName())
                                .stringType("address", clientUpdated.getUser().getAddress())
                                .stringType("email", clientUpdated.getUser().getEmail())
                                .integerType("age", clientUpdated.getUser().getAge())
                                .integerType("phoneNumber", clientUpdated.getUser().getPhoneNumber());

                return builder
                                .given("Test Update user")
                                .uponReceiving("Create User")
                                .path(END_POINT_CLIENT)
                                .headers(headers)
                                .method("POST")
                                .body(user)
                                .willRespondWith()
                                .headers(headers)
                                .status(200)
                                .body(client)
                                .uponReceiving("Update User")
                                .path(END_POINT_CLIENT)
                                .headers(headers)
                                .method("PUT")
                                .body(userModified)
                                .willRespondWith()
                                .headers(headers)
                                .status(200)
                                .body(clientModified)
                                .toPact();

        }

        @Pact(provider = "client-provider", consumer = "consumer-service")
        public RequestResponsePact failModifyUserPact(PactDslWithProvider builder) {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                clientToUpdateFail = new Client(
                                new User(
                                                1,
                                                "Name",
                                                "LastName",
                                                "Address",
                                                "email@email.com",
                                                50,
                                                1234567890),
                                true);

                PactDslJsonBody user = new PactDslJsonBody()
                                .integerType("id", clientToUpdateFail.getUser().getId())
                                .stringType("name", clientToUpdateFail.getUser().getName())
                                .stringType("lastName", clientToUpdateFail.getUser().getLastName())
                                .stringType("address", clientToUpdateFail.getUser().getAddress())
                                .stringType("email", clientToUpdateFail.getUser().getEmail())
                                .integerType("age", clientToUpdateFail.getUser().getAge())
                                .integerType("phoneNumber", clientToUpdateFail.getUser().getPhoneNumber());

                return builder
                                .given("Test Update User fail")
                                .uponReceiving("fail update user")
                                .path(END_POINT_CLIENT)
                                .method("PUT")
                                .headers(headers)
                                .body(user)
                                .willRespondWith()
                                .status(404)
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
        @PactVerification(value = "client-provider", fragment = "unSubscribePact")
        public void runUnSubscribePactTest() {
                MockServer server = rule.getMockServer();
                subscriberService.setBackendURL("http://localhost:" + server.getPort());
                ResponseEntity<Client> subscribedClient = subscriberService
                                .subscribeUser(testDeleteClientsData.getClients().get(0).getUser());
                assertTrue(subscribedClient.getStatusCode().is2xxSuccessful());

                ResponseEntity<Void> unSubscribedClient = subscriberService
                                .unSubscribeUser(testDeleteClientsData.getClients().get(0).getUser());
                assertTrue(unSubscribedClient.getStatusCode().is2xxSuccessful());
        }

        @Test
        @PactVerification(value = "client-provider", fragment = "failUnSubscribePact")
        public void runFailUnSubscribePactTest() {
                MockServer server = rule.getMockServer();
                subscriberService.setBackendURL("http://localhost:" + server.getPort());

                ResponseEntity<Void> unSubscribedClient = subscriberService
                                .unSubscribeUser(testDeleteClientsData.getClients().get(0).getUser());
                assertTrue(unSubscribedClient.getStatusCode().is4xxClientError());
        }

        @Test
        @PactVerification(value = "client-provider", fragment = "modifyUserPact")
        public void runUpdateSubscriptionPactTest() {
                MockServer server = rule.getMockServer();
                subscriberService.setBackendURL("http://localhost:" + server.getPort());
                ResponseEntity<Client> subscribedClient = subscriberService
                                .subscribeUser(clientToUpdate.getUser());
                assertTrue(subscribedClient.getStatusCode().is2xxSuccessful());

                ResponseEntity<Client> updateSubscriptionClient = subscriberService
                                .updateSubscribedUser(clientToUpdate.getUser());
                assertTrue(updateSubscriptionClient.getStatusCode().is2xxSuccessful());
        }

        @Test
        @PactVerification(value = "client-provider", fragment = "failModifyUserPact")
        public void runFailUpdateSubscriptionPactTest() {
                MockServer server = rule.getMockServer();
                subscriberService.setBackendURL("http://localhost:" + server.getPort());

                ResponseEntity<Client> unSubscribedClient = subscriberService
                                .updateSubscribedUser(clientToUpdateFail.getUser());
                assertTrue(unSubscribedClient.getStatusCode().is4xxClientError());
        }

}
