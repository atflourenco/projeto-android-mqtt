package anderson.br.projeto.testemqtt.events;

public class EventTopic {

    private String topico;
    private int evento;
    private int status;

    public EventTopic(String topico, int evento, int status) {
        this.topico = topico;
        this.evento = evento;
        this.status = status;
    }

    public String getTopico() {
        return topico;
    }

    public int getEvento() {
        return evento;
    }

    public int getStatus() {
        return status;
    }
}
