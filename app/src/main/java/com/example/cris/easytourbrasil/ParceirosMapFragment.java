package com.example.cris.easytourbrasil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;


public class ParceirosMapFragment extends Fragment implements OnMapReadyCallback {

    protected JSONObject parceiro;
    public static final String TAG = ParceirosMapFragment.class.getSimpleName();
    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            parceiro = new JSONObject(this.getArguments().getString("localizacaoParceiro"));
        } catch (JSONException e) {
            Log.e(TAG, "Erro ao criar objeto parceiro vindo do intent: ", e);
        }

        View view = inflater.inflate( R.layout.fragment_parceiros_map, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById( R.id.parceirosMap);
        mapFragment.getMapAsync(this);

        return view;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng localParceiro = null;
        try {
            localParceiro = new LatLng(parceiro.getDouble("latitude"), parceiro.getDouble("longitude"));
            mMap.addMarker(new MarkerOptions().position(localParceiro).title(parceiro.getString("nome_fantasia")));
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(localParceiro, 15));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
