package com.example.lembretedemedicao.bancodedados;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class medicamentoDAO {
    private final Context context;

    public medicamentoDAO(Context context) {
        this.context = context;
    }

    //TODO METODO PARA LISTAR TODOS OS MEDICAMENTOS
    public List<medicamento> listar() {
        List<medicamento> lista = new ArrayList<>();
        try (SQLiteDatabase db = new Banco(context).getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT * FROM medicamento", null)) {
            while (cursor.moveToNext()) {
                medicamento medicamento = new medicamento();
                medicamento.setId(cursor.getInt(0));
                medicamento.setNomemedicamento(cursor.getString(1));
                medicamento.setHorario(cursor.getString(2));
                lista.add(medicamento);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    //TODO: METODO PARA INSERIR O MEDICAMENTO
    public void insert(medicamento medicamento) {
        try (SQLiteDatabase db = new Banco(context).getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("nomemedicamento", medicamento.getNomemedicamento());
            values.put("horario", medicamento.getHorario());
            db.insert("medicamento", null, values);
        }
    }

    //TODO: METODO PARA DELETAR O MEDICAMENTO
    public void delete(medicamento medicamento, int id) {
        try (SQLiteDatabase db = new Banco(context).getWritableDatabase()) {
            db.delete("medicamento", "id = ? AND nomemedicamento = ?", new String[]{
                    String.valueOf(medicamento.getId()), medicamento.getNomemedicamento()
            });
        }
    }

    //TODO: METODO PARA BUSCAR O MEDICAMENTO PELO ID
    public medicamento Buscar(int id) {
        medicamento medicamento = null;
        try (SQLiteDatabase db = new Banco(context).getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT * FROM medicamento WHERE id = ?", new String[]{String.valueOf(id)})) {
            if (cursor.moveToFirst()) {
                medicamento = new medicamento();
                medicamento.setId(cursor.getInt(0));
                medicamento.setNomemedicamento(cursor.getString(1));
                medicamento.setHorario(cursor.getString(2));
            }
            return medicamento;
        }
    }

    //TODO: METODO PARA ATUALIZAR O MEDICAMENTO
    public void update(medicamento medicamento) {
        try (SQLiteDatabase db = new Banco(context).getWritableDatabase()) {
            ContentValues valores = new ContentValues();
            valores.put("nomemedicamento", medicamento.getNomemedicamento());
            valores.put("horario", medicamento.getHorario());
            valores.put("id", medicamento.getId());
            db.update("medicamento", valores, "id = ?", new String[]{String.valueOf(medicamento.getId())});
        }
    }


    public medicamento buscarPorNome(String nome) {
        SQLiteDatabase db = new Banco(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM medicamento WHERE nomemedicamento = ?", new String[]{nome});

        if (cursor.moveToFirst()) {
            medicamento med = new medicamento();
            med.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            med.setNomemedicamento(cursor.getString(cursor.getColumnIndexOrThrow("nomemedicamento")));
            med.setHorario(cursor.getString(cursor.getColumnIndexOrThrow("horario")));
            cursor.close();
            return med;
        }

        cursor.close();
        return null;
    }
}

