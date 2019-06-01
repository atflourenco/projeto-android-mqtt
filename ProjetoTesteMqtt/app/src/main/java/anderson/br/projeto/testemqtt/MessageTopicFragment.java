package anderson.br.projeto.testemqtt;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;

import anderson.br.projeto.testemqtt.events.EventMessageReceived;
import anderson.br.projeto.testemqtt.model.Message;
import anderson.br.projeto.testemqtt.model.MessageSent;

public class MessageTopicFragment extends Fragment{

    private View view;
    private String topicoSelecionado;
    private int tipoMensangens;
    private ListView listMensagens;
    private ArrayAdapter<String> adapter;
    private TextView titulo;
    private List<MessageSent> mensagensEnviadasSelecionadas = new ArrayList<>();
    private List<Message> mensagensRecebidasSelecionadas = new ArrayList<>();
    private List<String> mensagensList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_message, container, false);

        topicoSelecionado = getArguments().getString(Constants.TOPICO_SELECIONADO);
        tipoMensangens  = getArguments().getInt(Constants.TIPO_MENSAGENS);
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


        titulo = view.findViewById(R.id.title_topic);
        listMensagens = view.findViewById(R.id.list_messages);

        //Clear lists
        mensagensEnviadasSelecionadas.clear();
        mensagensRecebidasSelecionadas.clear();

        if(tipoMensangens==Constants.MENSAGENS_ENVIADAS){
            titulo.setText("Assunto: "+topicoSelecionado+ "\nMsgs. Enviadas");
            mensagensEnviadasSelecionadas.addAll(OptionsActivity.getMensagensEnviadasByTopico(topicoSelecionado));
            convertMessagesSent();

            listMensagens.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();;
                    bundle.putSerializable(Constants.MESSAGE_SELECTED, mensagensEnviadasSelecionadas.get(position));
                    MessageDetailFragment fragment = new MessageDetailFragment();
                    fragment.setArguments(bundle);
                    changeFragment(fragment);
                }
            });

        }else{
            titulo.setText("Assunto: "+topicoSelecionado+"\nMsgs. Recebidas");
            mensagensRecebidasSelecionadas.addAll(OptionsActivity.getMensagensRecebidasByTopico(topicoSelecionado));
            convertMessagesReceived();
        }

        adapter = new ArrayAdapter<>(view.getContext(),android.R.layout.simple_list_item_1,mensagensList);
        listMensagens.setAdapter(adapter);

    }


    private void changeFragment(Fragment f){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, f);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void convertMessagesReceived(){
        mensagensList = new ArrayList<String>();
        for(Message m: mensagensRecebidasSelecionadas){
            mensagensList.add(m.getMessage());
        }
    }

    private void convertMessagesSent(){
        mensagensList = new ArrayList<String>();
        for(MessageSent m: mensagensEnviadasSelecionadas){
            mensagensList.add(m.getMessage().getMessage());
        }
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
    public void onMessageEvent(EventMessageReceived event) {
        if(tipoMensangens==Constants.MENSAGENS_RECEBIDAS){
            if(event.getMessage().getTopico().equals(topicoSelecionado)){
                mensagensList.add(event.getMessage().getMessage());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }

    }

}
