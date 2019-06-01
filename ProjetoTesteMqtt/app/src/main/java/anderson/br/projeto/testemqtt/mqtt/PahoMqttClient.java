package anderson.br.projeto.testemqtt.mqtt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import anderson.br.projeto.testemqtt.Constants;
import anderson.br.projeto.testemqtt.events.EventMessageDelivery;
import anderson.br.projeto.testemqtt.events.EventMessageSent;
import anderson.br.projeto.testemqtt.events.EventTopic;
import anderson.br.projeto.testemqtt.events.EventMessageConfirmed;
import anderson.br.projeto.testemqtt.events.EventMessageReceived;
import anderson.br.projeto.testemqtt.events.EventMessageStatus;
import anderson.br.projeto.testemqtt.model.Message;

public class PahoMqttClient {

    private static final String TAG = "PahoMqttClient";
    private MqttAndroidClient mqttAndroidClient;
    public PahoMqttClient() {
        EventBus.getDefault().register(this);
    }

    public MqttAndroidClient getMqttClient(Context context, String mqttBrokerUrl, final String clientId, String user, String password) {


        mqttAndroidClient = new MqttAndroidClient(context,mqttBrokerUrl,clientId);
        try {
            IMqttToken token = mqttAndroidClient.connect(getMqttConnectionOption(user,password));
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Connect Success");
                    EventBus.getDefault().post(new EventMessageStatus("Conectado com sucesso!", Constants.CONECTADO));
                    mqttAndroidClient.setBufferOpts(getDisconnectedBufferOptions());
                    mqttCallback();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failure " + exception.toString());
                    EventBus.getDefault().post(new EventMessageStatus("Falha ao conectar!",Constants.ERRO_CONEXAO));
                }

            });

        }catch (MqttException e){
            Log.d(TAG,e.getMessage());
            e.printStackTrace();
        }
        return  mqttAndroidClient;
    }

    public void disconnect(@NonNull MqttAndroidClient client) throws MqttException{
        IMqttToken mqttToken = client.disconnect();
        mqttToken.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d(TAG, "Successfully disconnected");
                EventBus.getDefault().post(new EventMessageStatus("Desconectado com sucesso!", Constants.DESCONECTADO));
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.d(TAG, "Failed to disconnected " + throwable.toString());
                EventBus.getDefault().post(new EventMessageStatus("Falha ao desconectar!",Constants.ERRO_DESCONEXAO));
            }
        });
    }

    public void publishMessage(@NonNull MqttAndroidClient client, @NonNull String msg, int qos,
                               @NonNull String topic)
            throws MqttException, UnsupportedEncodingException {

        byte[] encodedPayload = new byte[0];
        Message m = encodeMessage(topic,client.getClientId(),msg);

        //Convert to Json
        Gson gson = new Gson();
        String mJson = gson.toJson(m);

        encodedPayload = mJson.getBytes("UTF-8");

        MqttMessage message = new MqttMessage(encodedPayload);

        message.setId(320);
        message.setRetained(true);
        message.setQos(qos);


        client.publish(topic, message);
        EventBus.getDefault().post(new EventMessageSent(m));
    }

    public void publishMessageResponse(@NonNull MqttAndroidClient client, Message msg, int qos,
                               @NonNull String topic)
            throws MqttException, UnsupportedEncodingException {

        Gson gson = new Gson();
        msg.setClientId(client.getClientId());
        msg.setHoraRecebimento(new Date());

        String mJson = gson.toJson(msg);
        byte[] encodedPayload = new byte[0];
        encodedPayload = mJson.getBytes("UTF-8");

        MqttMessage message = new MqttMessage(encodedPayload);

        message.setId(320);
        message.setRetained(true);
        message.setQos(qos);

        client.publish(topic, message);
    }


    public Message encodeMessage(String topic, String clientId, String msg){
        Message m = new Message();
        m.setTopico(topic);
        m.setClientId(clientId);
        m.setHoraEnvio(new Date());
        m.setMessage(msg);
        return m;
    }

    public void subscribe(@NonNull MqttAndroidClient client, @NonNull final String topic,
                          int qos) throws MqttException {
        IMqttToken token = client.subscribe(topic, qos);
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                EventBus.getDefault().post(new EventTopic(topic,Constants.SUBSCRIBE, Constants.OK));
                Log.d(TAG, "Subscribe Successfully " + topic);
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                EventBus.getDefault().post(new EventTopic(topic,Constants.SUBSCRIBE, Constants.FAIL));
                Log.e(TAG, "Subscribe Failed " + topic);

            }
        });
    }

    public void unSubscribe(@NonNull MqttAndroidClient client, @NonNull final String topic) throws MqttException {

        IMqttToken token = client.unsubscribe(topic);

        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                EventBus.getDefault().post(new EventTopic(topic,Constants.UNSUBSCRIBE,Constants.OK));
                Log.d(TAG, "UnSubscribe Successfully " + topic);
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                EventBus.getDefault().post(new EventTopic(topic,Constants.UNSUBSCRIBE,Constants.FAIL));
                Log.e(TAG, "UnSubscribe Failed " + topic);
            }
        });
    }

    @NonNull
    private DisconnectedBufferOptions getDisconnectedBufferOptions() {
        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(false);
        disconnectedBufferOptions.setDeleteOldestMessages(false);
        return disconnectedBufferOptions;
    }

    @NonNull
    private MqttConnectOptions getMqttConnectionOption(String user, String password) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(true);
        //mqttConnectOptions.setWill(Constants.PUBLISH_TOPIC, "I am going offline".getBytes(), 1, true);
        if(user!=null && !user.isEmpty()){
            mqttConnectOptions.setUserName(user);
        }
        if(password!=null && !password.isEmpty()){
            mqttConnectOptions.setPassword(password.toCharArray());
        }
        return mqttConnectOptions;
    }

    // Called when a subscribed message is received
    protected void mqttCallback() {
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d(TAG, "Connection complete server: " + serverURI);
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "Connection lost...!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = "Assunto: " + topic + "\r\nMessage: " + message.toString() + "\r\n";
                Log.d(TAG, msg);
                Message m = decodeMessage(message);
                if(!m.getClientId().equals(mqttAndroidClient.getClientId())){
                    if(m.getHoraRecebimento()==null){
                        EventBus.getDefault().post(new EventMessageReceived(m));
                        PahoMqttClient.this.publishMessageResponse(mqttAndroidClient,m,0,topic);
                    }else{
                        EventBus.getDefault().post(new EventMessageConfirmed(m));
                    }
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "Delivery complete!");
                EventBus.getDefault().post(new EventMessageDelivery());
            }

        });
    }

    @Subscribe
    public void messageStatus(EventMessageStatus eventMessageStatus){
        System.out.println(eventMessageStatus.getMessage());
    }

    private Message decodeMessage(MqttMessage message){
        Gson gson = new Gson();
        Message m = gson.fromJson(message.toString(),Message.class);
        return m;
    }

}
