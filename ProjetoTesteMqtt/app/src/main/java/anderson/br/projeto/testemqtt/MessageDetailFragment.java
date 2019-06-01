package anderson.br.projeto.testemqtt;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import anderson.br.projeto.testemqtt.model.Client;
import anderson.br.projeto.testemqtt.model.MessageSent;

public class MessageDetailFragment extends Fragment{

    private View view;
    private ListView listMensagens;
    private ArrayAdapter<String> adapter;
    private TextView titulo;
    private MessageSent messageSent;
    private List<String> listClients = new ArrayList<String>();;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_message, container, false);
        messageSent = (MessageSent) getArguments().getSerializable(Constants.MESSAGE_SELECTED);
        getActivity().setTitle("Assunto: " + messageSent.getMessage().getTopico());
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.handlerComponents();
    }

    public void handlerComponents(){

        titulo = view.findViewById(R.id.title_topic);
        titulo.setText("Msg.: " + messageSent.getMessage().getMessage());

        listMensagens = view.findViewById(R.id.list_messages);
        convertListDadosClients();
        adapter = new ArrayAdapter<>(view.getContext(),android.R.layout.simple_list_item_1,listClients);
        listMensagens.setAdapter(adapter);
    }

    private void convertListDadosClients(){
        String dados="";
        for(Client c: messageSent.getClients()){
            dados = "Recebido por: "+ c.getId();
            dados += "\n";
            dados += "Data/Hora: "+ new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(c.getHoraRecebimento());
            listClients.add(dados);
        }
    }

}
