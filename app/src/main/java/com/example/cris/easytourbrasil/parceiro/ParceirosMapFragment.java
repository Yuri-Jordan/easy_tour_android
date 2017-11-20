package com.example.cris.easytourbrasil.parceiro;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cris.easytourbrasil.R;
import com.example.cris.easytourbrasil.utilitarios.ConversorJson;
import com.example.cris.easytourbrasil.utilitarios.PolylineUtil;
import com.example.cris.easytourbrasil.utilitarios.RequisicaoHTTP;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ParceirosMapFragment extends Fragment implements OnMapReadyCallback,
        LocationListener, GoogleMap.OnMyLocationButtonClickListener{

    public static final String TAG = ParceirosMapFragment.class.getSimpleName();
    private static final int INTERVALO_ATUALIZACAO_LOCALIZACAO = 5;
    private static final int DISTANCIA_DE_PONTO_METROS = 10;
    private static final String CHAVE_GOOGLE_MAPS = "AIzaSyDcAGiJxsR6ZQWgAgAYZZQLRy_aLhMLiVM";

    protected JSONObject parceiro;
    private GoogleMap mMap;
    LocationManager locManager;
    Location location;
    Marker currentMarker = null;
    String provider;
    boolean botaoDeLocalizacaoClicado = false;
    List<Polyline> rota = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            parceiro = new JSONObject(this.getArguments().getString("localizacaoParceiro"));
        } catch (JSONException e) {
            Log.e(TAG, "Erro ao criar objeto parceiro vindo do intent: ", e);
        }

        View view = inflater.inflate(R.layout.fragment_parceiros_map, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.parceirosMap);
        mapFragment.getMapAsync(this);

        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        provider = locManager.getBestProvider(new Criteria(), false);

        if (isInternetDisponivel()) {
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Internet indisponível. Verifique sua conexão.", Toast.LENGTH_LONG).show();
        }

        pegarLocalizacao();

        return view;

    }

    private void pegarLocalizacao() {

        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        boolean gpsDisponivel = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        boolean netDisponivel = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsDisponivel && !netDisponivel) {
            return;
        } else {
            if (gpsDisponivel) {
                if (location == null) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, INTERVALO_ATUALIZACAO_LOCALIZACAO, this);
                    Log.d(TAG, "GPS Habilitado");
                    if (locManager != null) {
                        location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            } else if (netDisponivel) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, INTERVALO_ATUALIZACAO_LOCALIZACAO, this);
                Log.d(TAG, "Network provider habilitado");
                if (locManager != null) {
                    location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        }

    }

    private boolean isInternetDisponivel() {
        ConnectivityManager gerenciador = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = gerenciador.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected())
            return true;

        return false;

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

        LatLng localParceiro;
        try {
            localParceiro = new LatLng(parceiro.getDouble("latitude"), parceiro.getDouble("longitude"));
            mMap.addMarker(new MarkerOptions().position(localParceiro).title(parceiro.getString("nome_fantasia")));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localParceiro, 17));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if(botaoDeLocalizacaoClicado)
            marcarUsuarioNoMapa(location);
    }


    private void marcarUsuarioNoMapa(Location location) {

            Double lat = location.getLatitude();
            Double lng = location.getLongitude();

            LatLng localizacao = new LatLng(lat, lng);

            if (currentMarker!=null) {
                currentMarker.remove();
                currentMarker=null;
            }

            if (currentMarker==null) {
                currentMarker = mMap.addMarker(new MarkerOptions().position(localizacao).title("Minha localização").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localizacao, 18));

                if(atualizaDistancia(lat, lng) < DISTANCIA_DE_PONTO_METROS){
                    Log.v(TAG, "Visitou");
                }
            }

            Log.d("Coordenada Lat", lat.toString());
            Log.d("Coordenada Lng", lng.toString());

    }


    private int atualizaDistancia(double lat, double lng){

        float[] results = new float[10];
        try {
            location.distanceBetween(lat, lng, parceiro.getDouble("latitude"), parceiro.getDouble("longitude"), results);
            currentMarker.setSnippet("Você está a " + String.format("%.0f", results[0]) + "m de " + parceiro.getString("nome_fantasia"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(String.format("%.0f", results[0]));
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
        Log.v(TAG, "CLICADO");

        if (location == null){
            pegarLocalizacao();
            if(location == null)
                return false;
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        if(locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            botaoDeLocalizacaoClicado = true;
            marcarUsuarioNoMapa(location);
            new CriarRotaAteParceiroTask().execute(location);

        }else if(locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            botaoDeLocalizacaoClicado = true;
            marcarUsuarioNoMapa(location);
            new CriarRotaAteParceiroTask().execute(location);

        }else{
            pegarLocalizacao();
        }

        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private class CriarRotaAteParceiroTask extends AsyncTask<Location, Void, String[]> {

        @Override
        protected  String[] doInBackground(Location... locations) {

            String url= "";
            String req = "";

            Location loc = locations[0];

            try {
                url= "https://maps.googleapis.com/maps/api/directions/json?origin="
                        + loc.getLatitude() +","+loc.getLongitude()+"&destination="
                        + parceiro.getDouble("latitude")+","+parceiro.getDouble("longitude")+"&sensor=false&mode=walking&key="+CHAVE_GOOGLE_MAPS;
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                tracarRota(resultDoInBackground);
        }
    }

    private void tracarRota(String[] listaDirecoes) {

        if(rota != null){
            deletarRota();
        }

        PolylineUtil polylineUtil = new PolylineUtil();

        int qtdDir = listaDirecoes.length;
        for(int i = 0; i < qtdDir; i++)
        {
            PolylineOptions options = new PolylineOptions()
                    .color(Color.BLACK)
                    .width(5)
                    .addAll(polylineUtil.decode(listaDirecoes[i]));

            rota.add(mMap.addPolyline(options));

        }
    }

    private void deletarRota(){
        for(Polyline line : rota)
        {
            line.remove();
        }
        rota.clear();
    }

}
