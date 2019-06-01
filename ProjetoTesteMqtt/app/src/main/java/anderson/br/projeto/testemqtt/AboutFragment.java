package anderson.br.projeto.testemqtt;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class AboutFragment extends Fragment {
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_about, container, false);
        getActivity().setTitle(R.string.about);
//        ((OptionsActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((OptionsActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
