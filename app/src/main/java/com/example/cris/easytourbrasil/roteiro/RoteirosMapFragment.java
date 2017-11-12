package com.example.cris.easytourbrasil.roteiro;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cris.easytourbrasil.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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


public class RoteirosMapFragment extends Fragment implements OnMapReadyCallback {

    protected JSONArray roteiro;
    protected  int roteiroId;
    public static final String TAG = RoteirosMapFragment.class.getSimpleName();
    private GoogleMap mMap;
    SupportMapFragment mapFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        roteiroId = this.getArguments().getInt("roteiroId");


        View view = inflater.inflate( R.layout.fragment_roteiros_map, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById( R.id.roteirosMap);


        if(isInternetDisponivel()){
            new PontosTask().execute();
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "Internet indisponível. Verifique sua conexão.", Toast.LENGTH_LONG).show();
        }


        return view;
    }

    private boolean isInternetDisponivel() {
        ConnectivityManager gerenciador = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = gerenciador.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnected())
            return true;

        return false;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for(int i = 0; i < roteiro.length(); i++){
            try {
                JSONObject ponto = roteiro.getJSONObject(i).getJSONObject("ponto");
                LatLng localParceiro = new LatLng(ponto.getDouble("latitude"), ponto.getDouble("longitude"));
                mMap.addMarker(new MarkerOptions().position(localParceiro).title(ponto.getString("nome")));
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(localParceiro, 17));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    private void iniciarMapa(){
        mapFragment.getMapAsync(this);
    }


    private class PontosTask extends AsyncTask<Object, Void, JSONArray> {


        @Override
        protected JSONArray doInBackground(Object... params) {

            int responseStatus = 0;
            JSONArray jsonArray = null;

            try{
                URL apiCatParceirosURL = new URL("http://easy-tour-brasil-api.herokuapp.com/roteiros/" + roteiroId + "/pontos");
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
                    String atributo = objeto.getJSONObject("ponto").getString("nome");
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
            roteiro = resultDoInBackground;

            iniciarMapa();

        }
    }

}
