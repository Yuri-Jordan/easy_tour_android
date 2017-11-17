package com.example.cris.easytourbrasil;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.cris.easytourbrasil.parceiro.CategoriaParceirosFragment;
import com.example.cris.easytourbrasil.roteiro.CategoriaRoteirosFragment;
import com.example.cris.easytourbrasil.app.LoginActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.container, new MapsActivity(), "Maps Easy Tour Brasil");

        transaction.commitAllowingStateLoss();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.action_easy_tour_brasil) {
            fragmentManager.beginTransaction().replace(R.id.container, new EasyTourBrasilFragment()).commit();
        } else if (id == R.id.action_quero_ser_parceiro) {
            fragmentManager.beginTransaction().replace(R.id.container, new QueroSerParceiroFragment()).commit();
        } else if (id == R.id.action_contato) {
            fragmentManager.beginTransaction().replace(R.id.container, new ContatoFragment()).commit();
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_perfil) {
            fragmentManager.beginTransaction().replace(R.id.container, new AlterarPerfilFragment()).commit();
        } else if (id == R.id.nav_central_de_ajuda) {
            fragmentManager.beginTransaction().replace(R.id.container, new CentralDeAjudaFragment()).commit();
        } else if (id == R.id.nav_configuracoes) {
            fragmentManager.beginTransaction().replace(R.id.container, new ConfiguracoesFragment()).commit();
        } else if (id == R.id.nav_sair) {
            //fragmentManager.beginTransaction().replace(R.id.container, new SairFragment()).commit();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fragmentManager = getSupportFragmentManager();

            switch (item.getItemId()) {
                case R.id.navigation_roteiros:
                    fragmentManager.beginTransaction().replace(R.id.container, new CategoriaRoteirosFragment()).commit();
                    return true;
                case R.id.navigation_parceiros:
                    fragmentManager.beginTransaction().replace(R.id.container, new CategoriaParceirosFragment()).commit();
                    return true;
                case R.id.navigation_quanto_custa:
                    fragmentManager.beginTransaction().replace(R.id.container, new QuantoCustaFragment()).commit();
                    return true;
            }
            return false;
        }
    };

}
