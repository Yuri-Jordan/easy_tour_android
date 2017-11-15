package com.example.cris.easytourbrasil.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cris.easytourbrasil.MainActivity;
import com.example.cris.easytourbrasil.R;

public class LoginActivity extends AppCompatActivity {

    TextView linkCadastro;
    Button acessarButton;

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

        acessarButton = (Button) findViewById(R.id.acessarButton);
        acessarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), MainActivity.class));
                finish();
            }
        });

    }
}
