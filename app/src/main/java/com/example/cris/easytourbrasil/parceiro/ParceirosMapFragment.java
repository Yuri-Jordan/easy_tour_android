package com.example.cris.easytourbrasil.parceiro;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cris.easytourbrasil.R;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;


public class ParceirosMapFragment extends Fragment implements OnMapReadyCallback,
        LocationListener, GoogleMap.OnMyLocationButtonClickListener{

    protected JSONObject parceiro;
    public static final String TAG = ParceirosMapFragment.class.getSimpleName();
    private GoogleMap mMap;
    LocationManager locManager;
    Location location;
    String provider;

    Marker currentMarker = null;
    boolean botaoDeLocalizacaoClicado = false;
    boolean rotaTracada = false;
    Polyline rotaAtual;


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

        locManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        provider = locManager.getBestProvider(new Criteria(), false);

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
        location = locManager.getLastKnownLocation(provider);

        return view;

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
        locManager.requestLocationUpdates(provider, 3000, 5, this);
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

        LatLng localParceiro = null;
        try {
            localParceiro = new LatLng(parceiro.getDouble("latitude"), parceiro.getDouble("longitude"));
            mMap.addMarker(new MarkerOptions().position(localParceiro).title(parceiro.getString("nome_fantasia")));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localParceiro, 15));

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

                float[] results = new float[10];
                try {
                    location.distanceBetween(lat, lng, parceiro.getDouble("latitude"), parceiro.getDouble("longitude"), results);
                    currentMarker.setSnippet("Você está a " + String.format("%.0f", results[0]) + "m de " + parceiro.getString("nome_fantasia"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.d("Coordenada Lat", lat.toString());
            Log.d("Coordenada Lng", lng.toString());

            if(rotaAtual != null)
                rotaAtual.remove();

            if(!rotaTracada)
                new CriarRotaAteParceiroTask().execute(location);
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
        Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
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
                url= "http://maps.googleapis.com/maps/api/directions/json?origin="
                        + loc.getLatitude() +","+loc.getLongitude()+"&destination="
                        + parceiro.getDouble("latitude")+","+parceiro.getDouble("longitude")+"&sensor=false";
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
