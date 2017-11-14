package com.example.cris.easytourbrasil.roteiro;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cris.easytourbrasil.R;
import com.example.cris.easytourbrasil.parceiro.ParceirosMapFragment;
import com.example.cris.easytourbrasil.utilitarios.ConversorJson;
import com.example.cris.easytourbrasil.utilitarios.PolylineUtil;
import com.example.cris.easytourbrasil.utilitarios.RequisicaoHTTP;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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


public class RoteirosMapFragment extends Fragment implements OnMapReadyCallback,
        LocationListener, GoogleMap.OnMyLocationButtonClickListener {

    protected JSONArray roteiro;
    protected int roteiroId;
    public static final String TAG = RoteirosMapFragment.class.getSimpleName();
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    LocationManager locManager;
    Location location;
    String provider;

    boolean botaoDeLocalizacaoClicado = false;
    Marker currentMarker = null;
    boolean rotaTracada = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        roteiroId = this.getArguments().getInt("roteiroId");


        View view = inflater.inflate(R.layout.fragment_roteiros_map, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.roteirosMap);

        locManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        provider = locManager.getBestProvider(new Criteria(), false);


        if (isInternetDisponivel()) {
            new PontosTask().execute();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Internet indisponível. Verifique sua conexão.", Toast.LENGTH_LONG).show();
        }

        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        return view;
    }

    private boolean isInternetDisponivel() {
        ConnectivityManager gerenciador = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = gerenciador.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected())
            return true;

        return false;

    }

    @Override
    public void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 5, this);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, this);
        location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onPause() {
        super.onPause();

        locManager.removeUpdates(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);

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

    @Override
    public void onLocationChanged(Location location) {

        if(botaoDeLocalizacaoClicado){

            Double lat = location.getLatitude();
            Double lng = location.getLongitude();

            LatLng localizacao = new LatLng(lat, lng);

            if (currentMarker!=null) {
                currentMarker.remove();
                currentMarker=null;
            }

            if (currentMarker==null) {
                currentMarker = mMap.addMarker(new MarkerOptions().position(localizacao).title("Minha localizacao"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localizacao, 15));
            }

            Log.d("Coordenada Lat", lat.toString());
            Log.d("Coordenada Lng", lng.toString());

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        botaoDeLocalizacaoClicado = true;
        if(!rotaTracada)
            new CriarRotaAteRoteiroTask().execute(location);
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
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


    private class CriarRotaAteRoteiroTask extends AsyncTask<Location, Void, String[]> {
        @Override
        protected  String[] doInBackground(Location... locations) {

            String url= "";
            String req = "";
            JSONObject ponto = null;

            try {
                ponto = roteiro.getJSONObject(0).getJSONObject("ponto");

                Location loc = locations[0];

                url= "https://maps.googleapis.com/maps/api/directions/json?origin="
                        + loc.getLatitude() +","+loc.getLongitude()+"&destination="
                        + ponto.getDouble("latitude")+","+ponto.getDouble("longitude")+"&sensor=false&waypoints=optimize:true";

                for(int i = 1; i < roteiro.length(); i++){

                    try {
                        JSONObject p = roteiro.getJSONObject(i).getJSONObject("ponto");
                        url += "|" + p.getDouble("latitude") + "," + p.getDouble("longitude");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.v("url waypoints: ", url);

            try {
                req = new RequisicaoHTTP().lerUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String[] listaDirecoes = new ConversorJson().converterDirecoes(req);

            return listaDirecoes;
        }
        @Override
        protected void onPostExecute(String[] resultDoInBackground){
            if(!rotaTracada)
                tracarRota(resultDoInBackground);
        }
    }

    private void tracarRota(String[] listaDirecoes) {

        rotaTracada = true;

        PolylineUtil polylineUtil = new PolylineUtil();

        int qtdDir = listaDirecoes.length;
        for(int i = 0; i < qtdDir; i++)
        {
            PolylineOptions options = new PolylineOptions()
                    .color(Color.BLACK)
                    .width(10)
                    .addAll(polylineUtil.decode(listaDirecoes[i]));

            mMap.addPolyline(options);

        }
    }

}
