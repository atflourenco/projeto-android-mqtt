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

public class MessageFragment extends Fragment{

    private View view;
    private ListView listMensagens;
    private ArrayAdapter<String> adapter;
    private TextView titulo;
    private List<String> mensagensList = new ArrayList<String>();;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_message, container, false);
        getActivity().setTitle(R.string.messages);
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
        titulo.setVisibility(View.INVISIBLE);
        listMensagens = view.findViewById(R.id.list_messages);
        adapter = new ArrayAdapter<>(view.getContext(),android.R.layout.simple_list_item_1,mensagensList);
        listMensagens.setAdapter(adapter);

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
        String msg = "Assunto: " +event.getMessage().getTopico()+"\nMensagem: "+event.getMessage().getMessage();
        mensagensList.add(msg);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

}
