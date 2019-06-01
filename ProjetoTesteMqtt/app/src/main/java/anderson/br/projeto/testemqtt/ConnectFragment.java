package anderson.br.projeto.testemqtt;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Random;

import anderson.br.projeto.testemqtt.events.EventMessageStatus;
import anderson.br.projeto.testemqtt.mqtt.PahoMqttClient;

public class ConnectFragment extends Fragment implements View.OnClickListener {

    private View view;
    private Button btnConnect;
    private EditText campoHost;
    private EditText campoPort;
    private EditText campoClientId;
    private EditText campoUser;
    private EditText campoPassword;

    private static MqttAndroidClient client;
    private String TAG = "ConnectFragment";
    private static PahoMqttClient pahoMqttClient;
    private static boolean conectado = false;


    public static MqttAndroidClient     getClient() {
        return client;
    }

    public static PahoMqttClient getPahoMqttClient() {
        return pahoMqttClient;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_connect, container, false);
        getActivity().setTitle(R.string.text_connection);
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

        campoHost = view.findViewById(R.id.edt_host);
        campoPort = view.findViewById(R.id.edt_port);
        campoClientId = view.findViewById(R.id.edt_client_id);

        campoUser = view.findViewById(R.id.edt_name_user);
        campoPassword = view.findViewById(R.id.edt_password);

        btnConnect = view.findViewById(R.id.btn_connect);
        campoClientId.setText(campoClientId.getText().toString()+String.valueOf(new Random().nextInt()));

        if(conectado && client!=null){
            campoHost.setText(client.getServerURI().substring(0,client.getServerURI().lastIndexOf(":")));
            campoPort.setText(client.getServerURI().substring(client.getServerURI().lastIndexOf(":")+1));
            campoClientId.setText(client.getClientId());

            btnConnect.setText("D");
        }

        btnConnect.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String url;
        String clientId;
        String user;
        String password;
        url = campoHost.getText().toString()+":"+campoPort.getText().toString();
        clientId = campoClientId.getText().toString();
        user = campoUser.getText().toString();
        password = campoPassword.getText().toString();


        if(v.getId()==R.id.btn_connect){
            if(!conectado){
                pahoMqttClient = new PahoMqttClient();
                client = pahoMqttClient.getMqttClient(getContext(),url,clientId,user,password);
            }else{
                try {
                    if(client.isConnected() && conectado){
                        for(String t:OptionsActivity.getTopicos()){
                            pahoMqttClient.unSubscribe(client,t);
                        }
                        pahoMqttClient.disconnect(client);
                    }
                } catch (MqttException e) {
                    Log.d(TAG, "Falha ao desconetar.");
                    e.printStackTrace();
                }
            }
        }

    }


    private void enableFields(){
        campoHost.setEnabled(!conectado);
        campoPort.setEnabled(!conectado);
        campoClientId.setEnabled(!conectado);
        campoUser.setEnabled(!conectado);
        campoPassword.setEnabled(!conectado);
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
    public void messageStatus(EventMessageStatus messageStatus){
        if (messageStatus.getStatus()==Constants.CONECTADO){
            conectado = true;
            btnConnect.setText("D");
        }else{
            btnConnect.setText("C");
            conectado = false;
        }
        Snackbar.make(this.view,messageStatus.getMessage(),Snackbar.LENGTH_SHORT).show();
        this.enableFields();
    }

}
