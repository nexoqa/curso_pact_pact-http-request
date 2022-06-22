package com.nexoqa.service;

import com.nexoqa.model.Client;
import com.nexoqa.model.Clients;
import com.nexoqa.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClientService {

    private Map<Integer, Client> clients = new HashMap<>();

    public Client getClient(Integer id) {
        return searchClient(id);
    }

    public Clients getClients() {
        List<Client> listedClients = new ArrayList<>();

        clients.forEach((k, v) -> {
            listedClients.add(v);
        });

        return new Clients(listedClients);
    }

    public Client createClient(User user) {
        Client newClient = new Client(user, true);
        insertClient(newClient);
        return getClient(newClient.getUser().getId());
    }

    public boolean isRegistered(Integer id) {
        boolean isRegistered = false;

        if (getClient(id) != null) {
            isRegistered = true;
        }
        return isRegistered;
    }

    private void insertClient(Client client) {
        clients.put(client.getUser().getId(), client);
    }

    private Client searchClient(Integer id) {
        return clients.get(id);
    }

    public void deleteClient(User user) {
        if (isRegistered(user.getId())) {
            clients.remove(user.getId());
        }
    }

    public Client updateClient(User user) {
        Client clientBase = null;
        if (isRegistered(user.getId())) {
            clientBase = getClient(user.getId());
            User userBase = clientBase.getUser();
            userBase.setName(user.getName());
            userBase.setLastName(user.getLastName());
            userBase.setAddress(user.getAddress());
            userBase.setEmail(user.getEmail());
            userBase.setAge(user.getAge());
            userBase.setPhoneNumber(user.getPhoneNumber());
            clientBase.setUser(userBase);
            clients.replace(user.getId(), clientBase);
        }
        return getClient(user.getId());
    }

}
