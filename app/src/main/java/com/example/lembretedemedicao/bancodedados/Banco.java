package com.example.lembretedemedicao.bancodedados;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Banco extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bancodedados";
    private static final int DATABASE_VERSION = 1;

    public Banco(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    //TODO: METODO PARA CRIAR A TABELA NO BANCO DE DADOS
    @Override
    public void onCreate(SQLiteDatabase bancodedados) {
        bancodedados.execSQL("CREATE TABLE medicamento(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nomemedicamento TEXT," +
                "horario TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
