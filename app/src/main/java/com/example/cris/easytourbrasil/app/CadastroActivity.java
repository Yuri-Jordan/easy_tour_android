package com.example.cris.easytourbrasil.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cris.easytourbrasil.R;
import com.example.cris.easytourbrasil.usuario.Usuario;
import com.example.cris.easytourbrasil.usuario.UsuarioDAO;

public class CadastroActivity extends AppCompatActivity {

    Button bCancelar;
    Button bCadastrar;
    UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        usuarioDAO = new UsuarioDAO(this);

        bCancelar = (Button) findViewById(R.id.cancelarCadastro);
        bCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bCancelar = (Button) findViewById(R.id.botaoCadastrar);
        bCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Usuario usuarioValidado = verificarCampos(v);

                if(usuarioValidado != null){
                    usuarioDAO.inserirUsuario(usuarioValidado);
                    Toast.makeText(CadastroActivity.this, "Usuário cadastrado.", Toast.LENGTH_SHORT).show();
                    usuarioDAO.close();
                    finish();
                }
            }
        });
    }

    private Usuario verificarCampos(View v){

        Usuario u = null;
        EditText[] listaEditText = new EditText[4];

        listaEditText[0] = (EditText)findViewById(R.id.loginCadastro);
        listaEditText[1] = (EditText)findViewById(R.id.emailCadastro);
        listaEditText[2] = (EditText)findViewById(R.id.senhaCadastro);
        listaEditText[3] = (EditText)findViewById(R.id.senhaCadastroNovamente);

        String[] listaET = listaEditTextToString(listaEditText);

        if(camposValidos(listaEditText)){

            if(listaET[2].equals(listaET[3])) {
                u = new Usuario();
                u.setNome(listaET[0]);
                u.setEmail(listaET[1]);
                u.setSenha(listaET[2]);
            }else{
                Toast.makeText(this, "Senhas não batem!", Toast.LENGTH_SHORT).show();
            }
        }
        return u;
    }

    private boolean camposValidos(EditText[] listaET){

        boolean camposValidos = true;

        for(int i = 0; i < listaET.length; i++){

            if (listaET[i].getText().toString().trim().equals("")) {
                listaET[i].setError("Preencha!");
                camposValidos = false;
            }
        }
        return  camposValidos;
    }

    private String[] listaEditTextToString(EditText[] lista){

        String[] listaString = new String[4];

        for(int i = 0; i < lista.length; i++){
            if(lista[i].getText().toString() != null) {
                listaString[i] = lista[i].getText().toString();
            }else{
                listaString[i] = "";
            }
        }
        return listaString;
    }
}
