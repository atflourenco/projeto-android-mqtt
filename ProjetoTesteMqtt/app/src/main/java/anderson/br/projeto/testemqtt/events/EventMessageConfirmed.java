package anderson.br.projeto.testemqtt.events;

import anderson.br.projeto.testemqtt.model.Message;

public class EventMessageConfirmed {

    private Message message;

    public EventMessageConfirmed(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
