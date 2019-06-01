package anderson.br.projeto.testemqtt;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import anderson.br.projeto.testemqtt.events.EventMessageReceived;
import anderson.br.projeto.testemqtt.events.EventMessageSent;
import anderson.br.projeto.testemqtt.events.EventMessageStatus;

public class StatusFragment extends Fragment{

    private View view;
    private ListView listTopic;
    private ArrayAdapter<String> adapter;
    private TextView status;
    private TextView enviadas;
    private TextView recebidas;
    int qtdeRecebidas = 0;
    int qtdeEnviadas = 0;
    List<String> topicos;

    private List<String> topicList = new ArrayList<String>();;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_status, container, false);
        getActivity().setTitle(R.string.status);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.handlerComponents();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void handlerComponents(){
        status = view.findViewById(R.id.status_conection);
        enviadas = view.findViewById(R.id.msgs_sent);
        recebidas = view.findViewById(R.id.msgs_received);

        topicos = new ArrayList<>();
        topicos.addAll(OptionsActivity.getTopicos());

        for(String t: topicos){
            qtdeEnviadas += OptionsActivity.getMensagensEnviadasByTopico(t).size();
            qtdeRecebidas += OptionsActivity.getMensagensRecebidasByTopico(t).size();
        }

        status.setText("Status: Desconectado");

        if(ConnectFragment.getClient()!=null){
            status.setText("Status: "+ (ConnectFragment.getClient().isConnected()?"Conectado":"Desconectado"));
        }

        enviadas.setText("Msgs. Enviadas: "+ String.valueOf(qtdeEnviadas));
        recebidas.setText("Msgs. Recebidas: "+ String.valueOf(qtdeRecebidas));

        listTopic = view.findViewById(R.id.list_topic);
        adapter = new ArrayAdapter<>(view.getContext(),android.R.layout.simple_list_item_1,topicos);
        listTopic.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessageStatus event) {

        status.setText("Status: Desconectado");
        if(ConnectFragment.getClient()!=null){
            status.setText("Status: "+ (ConnectFragment.getClient().isConnected()?"Conectado":"Desconectado"));
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessageReceived event) {
        recebidas.setText("Msgs. Recebidas: "+ String.valueOf(++qtdeRecebidas));
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessageSent event) {
        enviadas.setText("Msgs. Enviadas: "+ String.valueOf(++qtdeEnviadas));
    }
}
