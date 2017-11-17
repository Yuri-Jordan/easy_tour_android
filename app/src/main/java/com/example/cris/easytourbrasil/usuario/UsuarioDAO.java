package com.example.cris.easytourbrasil.usuario;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UsuarioDAO extends SQLiteOpenHelper{

    private static final int BANCO_VERSAO = 1;
    private static final String BANCO_NOME = "EasyTourMobile";
    private static final String TABELA_NOME = "usuarios";
    private static final String ID = "id";
    private static final String NOME ="nome";
    private static final String SENHA = "senha";
    private static final String EMAIL = "email";

    private SQLiteDatabase sqLite;
    String sqlCriarTabela = "create table " +
            TABELA_NOME + " (" + ID + " integer primary key autoincrement, " +
            NOME + " text not null, " +
            EMAIL + " text not null, " +
            SENHA + " text not null );";

    public UsuarioDAO(Context context) {
        super(context, BANCO_NOME, null, BANCO_VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(sqlCriarTabela);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABELA_NOME;
        db.execSQL(sql);
        db.execSQL(sqlCriarTabela);
    }

    public void inserirUsuario(Usuario u){

        sqLite = getWritableDatabase();
        ContentValues conteudo = new ContentValues();
        conteudo.put(NOME, u.getNome());
        conteudo.put(EMAIL, u.getEmail());
        conteudo.put(SENHA, u.getSenha());

        sqLite.insert(TABELA_NOME, null, conteudo);

    }

    public String buscarUsuario(String nomeOuEmail){

        sqLite = getReadableDatabase();
        String senhaRetorno = "fdf1sf5f15d1fs5f15df(*%*¨%$(";
        nomeOuEmail = nomeOuEmail.trim();

        String sqlNome = "select * from " + TABELA_NOME + " where nome = " + "'" + nomeOuEmail + "';";
        String sqlemail = "select * from " + TABELA_NOME + " where email = " + "'" + nomeOuEmail + "';";

        Cursor cursor = this.sqLite.rawQuery(sqlNome,null);
        if(cursor.moveToFirst()){
            senhaRetorno = cursor.getString(3);
        }else{
            cursor = this.sqLite.rawQuery(sqlemail,null);
            if(cursor.moveToFirst()){
                senhaRetorno = cursor.getString(3);
            }
        }

        return senhaRetorno;
    }
}
