package anderson.br.projeto.testemqtt;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anderson.br.projeto.testemqtt.events.EventTopic;
import anderson.br.projeto.testemqtt.events.EventMessageStatus;

public class SubscribeFragment extends Fragment implements View.OnClickListener {

    private static String TAG = SubscribeFragment.class.getName();
    private View view;
    private ListView listTopicos;
    private Button btnIncrever;
    private ArrayAdapter<String> adapter;
    private String topico = "";
    private static Map<String, String> topicosInscritos = new HashMap<>();
    private List<String> topics;

    public static Map<String, String> getTopicosInscritos() {
        return topicosInscritos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_subscribe, container, false);
        getActivity().setTitle(R.string.subscribed);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.handlerComponents();
        this.enableFields();
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

        listTopicos = view.findViewById(R.id.list_subscribe);
        adapter = new ArrayAdapter<>(view.getContext(),android.R.layout.simple_list_item_1,convertListTopic());
        listTopicos.setAdapter(adapter);
        listTopicos.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view,
                                            ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(Menu.NONE, 2, Menu.NONE, "Mensagens Enviadas");
                contextMenu.add(Menu.NONE, 1, Menu.NONE, "Mensagens Recebidas");
                contextMenu.add(Menu.NONE, 0, Menu.NONE, "Desinscrever");
            }
        });


        btnIncrever = view.findViewById(R.id.btn_subscribe);
        btnIncrever.setOnClickListener(this);

    }

    private List<String> convertListTopic(){
        topics = new ArrayList<>();
        for(String t: topicosInscritos.values()){
            topics.add(t);
        }
        return topics;
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_subscribe){
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Assunto");

            final EditText input = new EditText(view.getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    topico = input.getText().toString();
                    if(topico!=null && topico.isEmpty()){
                            Snackbar.make(view,R.string.subscribre_empty,Snackbar.LENGTH_SHORT).show();
                    }else{
                        if(!topicosInscritos.containsKey(topico)){
                            topicosInscritos.put(topico,topico);
                            updateListTopicos();
                            subscribeNewTopico();
                        }else{
                            Snackbar.make(view,R.string.subscribre_already,Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }

    private void updateListTopicos() {
        adapter.clear();
        adapter.addAll(convertListTopic());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void enableFields(){
        btnIncrever.setEnabled(false);
        listTopicos.setEnabled(false);
        if(ConnectFragment.getClient()!=null){
            btnIncrever.setEnabled(ConnectFragment.getClient().isConnected());
            listTopicos.setEnabled(ConnectFragment.getClient().isConnected());

        }
    }

    private void subscribeNewTopico(){
        try {
            ConnectFragment.getPahoMqttClient().subscribe(ConnectFragment.getClient(),topico,0);
        } catch (MqttException e) {
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
        }
    }

    private void unSubscribeTopicoRemoved(){
        try {
            ConnectFragment.getPahoMqttClient().unSubscribe(ConnectFragment.getClient(),topico);
        } catch (MqttException e) {
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
        }
    }

    private void subscribeTopicos(){
        for(String t: convertListTopic()){
            try {
                ConnectFragment.getPahoMqttClient().subscribe(ConnectFragment.getClient(),t,0);
            } catch (MqttException e) {
                Log.e(TAG,e.getMessage());
                e.printStackTrace();
            }
        }
    }
    private void unSubscribeTopicos(){
        for(String t: convertListTopic()) {
            try {
                ConnectFragment.getPahoMqttClient().unSubscribe(ConnectFragment.getClient(), t);
            } catch (MqttException e) {
                Log.e(TAG,e.getMessage());
                e.printStackTrace();
            }
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = menuInfo.position;
        topico = topics.get(position);

        switch(item.getItemId()) {
            case 0:{
                topicosInscritos.remove(topico);
                unSubscribeTopicoRemoved();
                updateListTopicos();
            }break;
            case 1:{
                Bundle bundle = new Bundle();
                bundle.putString(Constants.TOPICO_SELECIONADO, topico);
                bundle.putInt(Constants.TIPO_MENSAGENS, Constants.MENSAGENS_RECEBIDAS);
                MessageTopicFragment fragment = new MessageTopicFragment();
                fragment.setArguments(bundle);
                this.changeFragment(fragment);
            }break;
            case 2:{
                Bundle bundle = new Bundle();
                bundle.putString(Constants.TOPICO_SELECIONADO, topico);
                bundle.putInt(Constants.TIPO_MENSAGENS, Constants.MENSAGENS_ENVIADAS);
                MessageTopicFragment fragment = new MessageTopicFragment();
                fragment.setArguments(bundle);
                this.changeFragment(fragment);

            }break;
        }
        return super.onContextItemSelected(item);

    }

    private void changeFragment(Fragment f){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, f);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void statusSubscribeUnSubscribe(EventTopic event){
        if(event.getEvento()==Constants.SUBSCRIBE){
            if(event.getStatus()==Constants.OK){
                Snackbar.make(this.view,"Assunto " + event.getTopico() + " inscrito com sucesso!",Snackbar.LENGTH_SHORT).show();
            }else{
                Snackbar.make(this.view,"Falha ao se increver no assunto " + event.getTopico() + ".",Snackbar.LENGTH_SHORT).show();
            }
        }else{
            if(event.getEvento()==Constants.UNSUBSCRIBE){
                if(event.getStatus()==Constants.OK){
                    Snackbar.make(this.view,"Assunto " + event.getTopico() + " desinscrito com sucesso!",Snackbar.LENGTH_SHORT).show();
                }else{
                    Snackbar.make(this.view,"Falha ao se desincrever no assunto " + event.getTopico() + ".",Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void statusConnection(EventMessageStatus eventMessageStatus){
        if(eventMessageStatus.getStatus()==Constants.CONECTADO){
            subscribeTopicos();
        }else {
            unSubscribeTopicos();
        }
        Toast.makeText(this.view.getContext(), eventMessageStatus.getMessage(),Toast.LENGTH_SHORT).show();
    }
}
