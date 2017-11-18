package com.example.cris.easytourbrasil.parceiro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cris.easytourbrasil.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CategoriaParceirosFragment extends Fragment {

    private ProgressBar spinner;

    protected ListView listView;
    public String[] catParceirosNome;
    protected JSONArray categoriaParceiros;
    public static final String TAG = CategoriaParceirosFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate( R.layout.fragment_categoria_parceiros, container, false);

        spinner = (ProgressBar)view.findViewById(R.id.ventilator_progress);

        // Acessando a listView pelo Id=listViewCatParceiros
        listView = (ListView) view.findViewById( R.id.listViewCatParceiros);

        if(isInternetDisponivel()){
            spinner.setVisibility(View.VISIBLE);
            new CategoriaParceirosTask().execute();
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "Internet indisponível. Verifique sua conexão.", Toast.LENGTH_LONG).show();
        }

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private boolean isInternetDisponivel() {
        ConnectivityManager gerenciador = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = gerenciador.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnected())
            return true;

        return false;

    }

    private void atualizarLista() {
        if(categoriaParceiros == null){
            Toast.makeText(getActivity().getApplicationContext(), "Nao existem categorias cadastradas.", Toast.LENGTH_LONG).show();
        }else{
            try {
                catParceirosNome =  new String[categoriaParceiros.length()];

                for(int i = 0; i < categoriaParceiros.length(); i++){
                    JSONObject objeto = categoriaParceiros.getJSONObject(i);
                    String atributo = objeto.getString("nome");
                    catParceirosNome[i] = atributo;
                }



                // Vinculando os valores a listView
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, catParceirosNome){
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getView(position, convertView, parent);
                        textView.setTextColor( Color.parseColor("#4a6112"));
                        return textView;
                    }
                };
                spinner.setVisibility(View.INVISIBLE);
                // Vinculando a lista com as informações
                listView.setAdapter(arrayAdapter);



                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putString("parceiros", categoriaParceiros.getJSONObject(position).toString());
                            ParceirosFragment pFragment = new ParceirosFragment();
                            pFragment.setArguments(bundle);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace( R.id.container, pFragment)
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

    private class CategoriaParceirosTask extends AsyncTask<Object, Void, JSONArray> {


        @Override
        protected JSONArray doInBackground(Object... params) {

            int responseStatus = 0;
            JSONArray jsonArray = null;

            try{
                URL apiCatParceirosURL = new URL("http://easytourbrasil.com.br/adminApi/api/categoriaParceiros");
                HttpURLConnection connection = (HttpURLConnection) apiCatParceirosURL.openConnection();
                connection.connect();

                InputStream inStream = connection.getInputStream();
                BufferedReader leitor = new BufferedReader(new InputStreamReader(inStream));
                StringBuffer conteudo = new StringBuffer();

                String linha = "";
                while((linha = leitor.readLine()) != null){
                    conteudo.append(linha);
                }

                jsonArray = new JSONArray(conteudo.toString());
//                Log.v(TAG, String.valueOf(jsonArray));
//
//                for(int i = 0; i < jsonArray.length(); i++){
//                    JSONObject objeto = jsonArray.getJSONObject(i);
//                    String atributo = objeto.getString("nome");
//                    Log.v(TAG, atributo);
//                }
//
//                responseStatus = connection.getResponseCode();
//                Log.i(TAG, "Status: "+ responseStatus);

            }catch(MalformedURLException e){
                Log.e(TAG, "Erro de URL: ", e);
            } catch (IOException e) {
                Log.e(TAG, "Erro de Conexão: ", e);
            } catch (JSONException e) {
                Log.e(TAG, "Erro de Conversão para Json: ", e);
            }
            return jsonArray;
        }
        @Override
        protected void onPostExecute(JSONArray resultDoInBackground){
            categoriaParceiros = resultDoInBackground;
            atualizarLista();
        }
    }

}
