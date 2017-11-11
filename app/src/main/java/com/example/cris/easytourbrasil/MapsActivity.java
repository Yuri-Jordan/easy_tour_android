package com.example.cris.easytourbrasil;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Adiciona o botão de zoom no mapa
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Adiciona o marcador para Igreja do Galo / Museu da Arte Sacra
        LatLng ponto01 = new LatLng(-5.78661, -35.20909);
        MarkerOptions marker01 = new MarkerOptions();
        marker01.position(ponto01);
        marker01.title("Igreja do Galo / Museu da Arte Sacra");

        // Adiciona o marcador para Memorial Câmara Cascudo
        LatLng ponto02 = new LatLng(-5.7859, -35.20904);
        MarkerOptions marker02 = new MarkerOptions();
        marker02.position(ponto02);
        marker02.title("Memorial Câmara Cascudo");

        // Adiciona o marcador para Igreja Matriz N. Sra da Apresentação
        LatLng ponto03 = new LatLng(-5.78567, -35.209);
        MarkerOptions marker03 = new MarkerOptions();
        marker03.position(ponto03);
        marker03.title("Igreja Matriz N. Sra da Apresentação");

        // Adiciona o marcador para Instituto Histórico e Geográfico do Rn
        LatLng ponto04 = new LatLng(-5.78534, -35.2088);
        MarkerOptions marker04 = new MarkerOptions();
        marker04.position(ponto04);
        marker04.title("Instituto Histórico e Geográfico do Rn");

        // Adiciona o marcador para Praça André de Albuquerque Maranhão
        LatLng ponto05 = new LatLng(-5.78459, -35.20878);
        MarkerOptions marker05 = new MarkerOptions();
        marker05.position(ponto05);
        marker05.title("Praça André de Albuquerque Maranhão");

        // Adiciona o marcador para Palácio Potengi / Palácio da Cultura
        LatLng ponto06 = new LatLng(-5.784759, -35.208306);
        MarkerOptions marker06 = new MarkerOptions();
        marker06.position(ponto06);
        marker06.title("Palácio Potengi / Palácio da Cultura");

        // Adiciona o marcador para Museu Café Filho
        LatLng ponto07 = new LatLng(-5.78519, -35.20826);
        MarkerOptions marker07 = new MarkerOptions();
        marker07.position(ponto07);
        marker07.title("Museu Café Filho");

        mMap.addMarker(marker01);
        mMap.addMarker(marker02);
        mMap.addMarker(marker03);
        mMap.addMarker(marker04);
        mMap.addMarker(marker05);
        mMap.addMarker(marker06);
        mMap.addMarker(marker07);

        mMap.moveCamera( CameraUpdateFactory.newLatLng(ponto04));
        mMap.moveCamera( CameraUpdateFactory.zoomTo(17));
    }
}
