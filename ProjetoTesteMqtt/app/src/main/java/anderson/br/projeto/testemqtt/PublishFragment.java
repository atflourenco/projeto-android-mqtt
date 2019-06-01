package anderson.br.projeto.testemqtt;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import anderson.br.projeto.testemqtt.events.EventMessageDelivery;

public class PublishFragment extends Fragment implements View.OnClickListener {

    private View view;
    private Button btnPublicar;
    private Spinner spnTopicos;
    private String topicoSelecionado;
    private EditText campoMensagem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_publish, container, false);
        getActivity().setTitle(R.string.publish);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.handlerComponents();
        this.enableFields();
    }

    public void handlerComponents(){

        spnTopicos = view.findViewById(R.id.spn_topic);
        loadSpinner();

        campoMensagem = view.findViewById(R.id.edt_message);

        btnPublicar = view.findViewById(R.id.btn_publish);
        btnPublicar.setOnClickListener(this);
    }


    private void loadSpinner(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, OptionsActivity.getTopicos());
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spnTopicos.setAdapter(spinnerArrayAdapter);

        spnTopicos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                topicoSelecionado = parent.getItemAtPosition(posicao).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                topicoSelecionado = null;
            }
        });

    }

    @Override
    public void onClick(View v) {
        String topico = topicoSelecionado;
        String mensagem = campoMensagem.getText().toString();
        if(v.getId()==R.id.btn_publish){
            try{
                if(topicoSelecionado==null || topicoSelecionado.isEmpty()){
                    Snackbar.make(this.view,R.string.select_topic,Snackbar.LENGTH_SHORT).show();
                }else{
                    ConnectFragment.getPahoMqttClient().publishMessage(ConnectFragment.getClient(),mensagem,0, topico);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void enableFields(){
        btnPublicar.setEnabled(false);
        spnTopicos.setEnabled(false);
        campoMensagem.setEnabled(false);

        if(ConnectFragment.getClient()!=null){
            btnPublicar.setEnabled(ConnectFragment.getClient().isConnected());
            spnTopicos.setEnabled(ConnectFragment.getClient().isConnected());
            campoMensagem.setEnabled(ConnectFragment.getClient().isConnected());
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
    public void onMessageDelivery(EventMessageDelivery event) {
        Snackbar.make(this.view,"Mensagem enviada com sucesso!",Snackbar.LENGTH_SHORT).show();
    }
}
