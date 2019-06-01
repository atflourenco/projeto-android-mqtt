package anderson.br.projeto.testemqtt;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anderson.br.projeto.testemqtt.events.EventTopic;
import anderson.br.projeto.testemqtt.events.EventMessageConfirmed;
import anderson.br.projeto.testemqtt.events.EventMessageReceived;
import anderson.br.projeto.testemqtt.events.EventMessageSent;
import anderson.br.projeto.testemqtt.model.Client;
import anderson.br.projeto.testemqtt.model.Message;
import anderson.br.projeto.testemqtt.model.MessageSent;

public class OptionsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = "OptionsActivity";
    private static List<String> topicos = new ArrayList<>();
    private static Map<String, List<Message>> mensagensRecebidas = new HashMap<>();
    private static Map<String, List<MessageSent>> mensagensEnviadas = new HashMap<>();
    private List<Message> messageReceivedList;
    private List<MessageSent> messageSentList;
    private AlertDialog alerta;
    private boolean mToolBarNavigationListenerIsRegistered = false;


    public static List<String> getTopicos() {
        return topicos;
    }


    public static List<Message> getMensagensRecebidasByTopico(String topico) {
        if(mensagensRecebidas.containsKey(topico)){
            return mensagensRecebidas.get(topico);
        }else{
            return new ArrayList<>();
        }
    }

    public static List<MessageSent> getMensagensEnviadasByTopico(String topico) {
        if(mensagensEnviadas.containsKey(topico)){
            return mensagensEnviadas.get(topico);
        }else{
            return new ArrayList<>();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, new ConnectFragment());
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getFragmentManager();
            if(fm.getBackStackEntryCount()>0){
                fm.popBackStack();
            }else{
                this.closeConectionAndExit();
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            this.changeFragment(new AboutFragment(),true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_connect) {
            this.changeFragment(new ConnectFragment(),false);

        } else if (id == R.id.nav_publish) {
            this.changeFragment(new PublishFragment(),false);

        } else if (id == R.id.nav_subscribe) {
            this.changeFragment(new SubscribeFragment(),false);

        } else if (id == R.id.nav_messages) {
            this.changeFragment(new MessageFragment(),false);
        } else if (id == R.id.nav_status) {
            this.changeFragment(new StatusFragment(),false);
        }else if (id == R.id.nav_exit) {
            this.confirmExit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void closeConectionAndExit(){
        if(ConnectFragment.getPahoMqttClient()!=null){
            try {
                if(ConnectFragment.getClient()!=null && ConnectFragment.getClient().isConnected()){
                    if(topicos!=null)
                        for(String t: topicos){
                            ConnectFragment.getClient().unsubscribe(t);
                        }
                    ConnectFragment.getPahoMqttClient().disconnect(ConnectFragment.getClient());
                }
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
    private void changeFragment(Fragment f, boolean stack){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, f);
        if(stack){
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    private void confirmExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmação");
        builder.setMessage("Deseja realmente sair?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                OptionsActivity.this.closeConectionAndExit();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                OptionsActivity.this.changeFragment(new ConnectFragment(),false);
            }
        });
        alerta = builder.create();
        alerta.show();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessageReceived event) {
        messageReceivedList= new ArrayList<>();
        if(mensagensRecebidas.containsKey(event.getMessage().getTopico())){
            messageReceivedList.addAll(mensagensRecebidas.get(event.getMessage().getTopico()));
            mensagensRecebidas.remove(event.getMessage().getTopico());
        }
        messageReceivedList.add(event.getMessage());
        System.out.println("Topico: " + event.getMessage().getTopico() + "-Qtde msgs: " + messageReceivedList.size());
        mensagensRecebidas.put(event.getMessage().getTopico(),messageReceivedList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopicEvent(EventTopic event){
        if(event.getEvento()==Constants.UNSUBSCRIBE){
            if(event.getStatus()==Constants.OK){
                topicos.remove(event.getTopico());
            }
        }else{
            if(event.getStatus()==Constants.OK){
                if(!topicos.contains(event.getTopico())){
                    topicos.add(event.getTopico());
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessageSent event) {
        MessageSent msgSent = new MessageSent();
        msgSent.setMessage(event.getMessage());

        messageSentList = new ArrayList<>();

        if(mensagensEnviadas.containsKey(event.getMessage().getTopico())){
            messageSentList.addAll(mensagensEnviadas.get(event.getMessage().getTopico()));
            mensagensEnviadas.remove(event.getMessage().getTopico());
        }
        messageSentList.add(msgSent);
        System.out.println("Topico: " + event.getMessage().getTopico() + "-Qtde msgs: " + messageSentList.size());
        mensagensEnviadas.put(event.getMessage().getTopico(),messageSentList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageConfirmedEvent(EventMessageConfirmed event) {
        List<MessageSent> auxList = new ArrayList<>();
        messageSentList = new ArrayList<>();
        if(mensagensEnviadas.containsKey(event.getMessage().getTopico())){
            List<MessageSent> list = mensagensEnviadas.get(event.getMessage().getTopico());
            for(MessageSent m: list){
                if(m.getMessage().getMessage().equals(event.getMessage().getMessage())
                        && m.getMessage().getTopico().equals(event.getMessage().getTopico())
                        && (Double.compare(m.getMessage().getId(),event.getMessage().getId())==0)){
                    m.addClient(new Client(event.getMessage().getClientId(),event.getMessage().getHoraRecebimento()));
                }
                auxList.add(m);
            }
            mensagensEnviadas.remove(event.getMessage().getTopico());
            mensagensEnviadas.put(event.getMessage().getTopico(),auxList);
            messageSentList.addAll(mensagensEnviadas.get(event.getMessage().getTopico()));
        }
    }


}
