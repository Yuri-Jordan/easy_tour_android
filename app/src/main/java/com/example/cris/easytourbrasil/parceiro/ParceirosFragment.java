package com.example.cris.easytourbrasil.parceiro;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cris.easytourbrasil.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParceirosFragment extends Fragment {

    protected ListView listView;
    protected JSONArray parceiros;
    protected String[] parceirosNome;
    public static final String TAG = ParceirosFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate( R.layout.fragment_parceiros, container, false);
        listView = (ListView) view.findViewById( R.id.listViewParceiros);


        try {

            JSONObject parceiro = new JSONObject(this.getArguments().getString("parceiros"));
            parceiros = parceiro.getJSONArray("parceiros");

        } catch (JSONException e) {
            Log.e(TAG, "Erro ao receber string: JsonArray de parceiros do intent catParceiros: ", e);
        }

        atualizarLista();
        Log.v(TAG, String.valueOf(parceiros));

        return view;

    }


    private void atualizarLista() {
        if(parceiros == null || parceiros.length() == 0){
            Toast.makeText(getActivity().getApplicationContext(), "Nao existem parceiros para esta categoria.", Toast.LENGTH_LONG).show();
        }else{
            try {
                parceirosNome =  new String[parceiros.length()];

                for(int i = 0; i < parceiros.length(); i++){
                    JSONObject objeto = parceiros.getJSONObject(i);
                    String atributo = objeto.getString("nome_fantasia");
                    parceirosNome[i] = atributo;
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, parceirosNome){
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getView(position, convertView, parent);
                        textView.setTextColor( Color.parseColor("#4a6112"));
                        return textView;
                    }
                };
                listView.setAdapter(arrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putString("localizacaoParceiro", parceiros.getJSONObject(position).toString());
                            ParceirosMapFragment pmFragment = new ParceirosMapFragment();
                            pmFragment.setArguments(bundle);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace( R.id.container, pmFragment)
                                    .addToBackStack(null)
                                    .commit();

                        } catch (JSONException e) {
                            Log.e(TAG, "Erro ao criar intent JsonArray catParceiros: ", e);
                        }

                    }
                });


            } catch (JSONException e) {
                Log.e(TAG, "Erro ao logar JsonArray catParceiros: ", e);
            }
        }
    }


}
