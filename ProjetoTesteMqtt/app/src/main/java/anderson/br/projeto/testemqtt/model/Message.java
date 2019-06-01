package anderson.br.projeto.testemqtt.model;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private Double id;
    private String topico;
    private Date horaEnvio;
    private Date horaRecebimento;
    private String message;
    private String clientId;

    public Message() {
        this.id =  Math.random();
    }

    public Double getId() {
        return id;
    }

    public void setId(Double id) {
        this.id = id;
    }


    public String getTopico() {
        return topico;
    }

    public void setTopico(String topico) {
        this.topico = topico;
    }

    public Date getHoraEnvio() {
        return horaEnvio;
    }

    public void setHoraEnvio(Date horaEnvio) {
        this.horaEnvio = horaEnvio;
    }

    public Date getHoraRecebimento() {
        return horaRecebimento;
    }

    public void setHoraRecebimento(Date horaRecebimento) {
        this.horaRecebimento = horaRecebimento;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

}
