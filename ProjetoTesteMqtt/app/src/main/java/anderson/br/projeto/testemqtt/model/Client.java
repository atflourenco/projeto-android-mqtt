package anderson.br.projeto.testemqtt.model;

import java.util.Date;

public class Client {
    private String id;
    private Date horaRecebimento;


    public Client(String id, Date horaRecebimento) {
        this.id = id;
        this.horaRecebimento = horaRecebimento;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getHoraRecebimento() {
        return horaRecebimento;
    }

    public void setHoraRecebimento(Date horaRecebimento) {
        this.horaRecebimento = horaRecebimento;
    }
}
