package anderson.br.projeto.testemqtt.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageSent implements Serializable {
    private Message message;
    private List<Client> clients = new ArrayList<>();

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public void addClient(Client c){
        this.clients.add(c);
    }

}
