package com.example.cris.easytourbrasil.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cris.easytourbrasil.MainActivity;
import com.example.cris.easytourbrasil.R;
import com.example.cris.easytourbrasil.usuario.UsuarioDAO;

public class LoginActivity extends AppCompatActivity {

    TextView linkCadastro;
    Button acessarButton;
    UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        usuarioDAO = new UsuarioDAO(this);

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

                if(usuarioAutenticado(v)){
                    startActivity(new Intent(v.getContext(), MainActivity.class));
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "Informações inválidas.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean usuarioAutenticado(View view){

        boolean autenticado = false;

        EditText nome = (EditText)findViewById(R.id.login);
        EditText senha = (EditText)findViewById(R.id.senha);

        String nomeStr = nome.getText().toString();
        String senhaStr = senha.getText().toString();

        if(usuarioDAO.buscarUsuario(nomeStr).equals(senhaStr))
            autenticado = true;

        return autenticado;
    }

}
