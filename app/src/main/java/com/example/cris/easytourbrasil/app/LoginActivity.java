package com.example.cris.easytourbrasil.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.cris.easytourbrasil.R;

public class LoginActivity extends AppCompatActivity {

    TextView linkCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        linkCadastro = (TextView)findViewById(R.id.textoCadastro);
        linkCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), CadastroActivity.class));
            }
        });
    }
}
