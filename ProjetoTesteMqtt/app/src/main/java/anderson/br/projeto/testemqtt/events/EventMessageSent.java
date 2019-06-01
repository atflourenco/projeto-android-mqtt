package anderson.br.projeto.testemqtt.events;


import anderson.br.projeto.testemqtt.model.Message;

public class EventMessageSent {
    private Message message;

    public EventMessageSent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
