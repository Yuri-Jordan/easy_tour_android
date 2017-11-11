package com.example.cris.easytourbrasil;

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

public class RoteirosFragment extends Fragment {


    protected ListView listView;
    protected JSONArray roteiros;
    protected String[] roteirosNome;
    protected  int roteiroId;
    public static final String TAG = RoteirosFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate( R.layout.fragment_roteiros, container, false);

        listView = (ListView) view.findViewById( R.id.listViewRoteiros);

        roteiroId = this.getArguments().getInt("roteirosId");

        if(isInternetDisponivel()){
            new RoteirosTask().execute();
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "Internet indisponível. Verifique sua conexão.", Toast.LENGTH_LONG).show();
        }

        // Inflate the layout for this fragment
        return view;
    }

    private boolean isInternetDisponivel() {
        ConnectivityManager gerenciador = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = gerenciador.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnected())
            return true;

        return false;

    }

    private void atualizarLista() {
        if(roteiros == null){
            Toast.makeText(getActivity().getApplicationContext(), "Nao existem categorias cadastradas.", Toast.LENGTH_LONG).show();
        }else{
            try {
                roteirosNome =  new String[roteiros.length()];

                for(int i = 0; i < roteiros.length(); i++){
                    JSONObject objeto = roteiros.getJSONObject(i);
                    String atributo = objeto.getString("nome");
                    roteirosNome[i] = atributo;
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, roteirosNome){
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
                            bundle.putInt("roteiroId", roteiros.getJSONObject(position).getInt("id"));
                            RoteirosMapFragment rmFragment = new RoteirosMapFragment();
                            rmFragment.setArguments(bundle);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace( R.id.container, rmFragment)
                                    .addToBackStack(null)
                                    .commit();

                        } catch (JSONException e) {
                            Log.e(TAG, "Erro ao criar intent JsonArray categoriaRoteiros: ", e);
                        }

                    }
                });


            } catch (JSONException e) {
                Log.e(TAG, "Erro ao logar JsonArray categoriaRoteiros: ", e);
            }
        }
    }


    private class RoteirosTask extends AsyncTask<Object, Void, JSONArray> {


        @Override
        protected JSONArray doInBackground(Object... params) {

            int responseStatus = 0;
            JSONArray jsonArray = null;

            try{
                URL apiCatParceirosURL = new URL("http://easy-tour-brasil-api.herokuapp.com/categorias/" + roteiroId + "/roteiros");
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
                Log.v(TAG, String.valueOf(jsonArray));

                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject objeto = jsonArray.getJSONObject(i);
                    String atributo = objeto.getString("nome");
                    Log.v(TAG, atributo);
                }

                responseStatus = connection.getResponseCode();
                Log.i(TAG, "Status: "+ responseStatus);

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
            roteiros = resultDoInBackground;
            atualizarLista();
        }
    }


}
