package anderson.br.projeto.testemqtt.events;

import anderson.br.projeto.testemqtt.model.Message;

public class EventMessageReceived {

    private Message message;

    public EventMessageReceived(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
