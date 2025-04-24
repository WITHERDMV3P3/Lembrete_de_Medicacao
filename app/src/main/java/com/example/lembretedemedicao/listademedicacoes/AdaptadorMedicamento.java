package com.example.lembretedemedicao.listademedicacoes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lembretedemedicao.MainActivity;
import com.example.lembretedemedicao.R;
import com.example.lembretedemedicao.bancodedados.medicamento;
import com.example.lembretedemedicao.bancodedados.medicamentoDAO;


import java.util.List;

public class AdaptadorMedicamento extends RecyclerView.Adapter<BotoesETextviewMedicamento_Item> {

    private Context context;
    private List<medicamento> lista;


    public AdaptadorMedicamento(Context context, List<medicamento> lista) {
        this.context = context;
        this.lista = lista;
    }


    @NonNull
    @Override
    public BotoesETextviewMedicamento_Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.medicamento_item, parent, false);
        return new BotoesETextviewMedicamento_Item(view);
    }

    //TODO: MÉTODO PARA MOSTRAR O MEDICAMENTO E HORARIO EM UMA RECYCLERVIEW
    @Override
    public void onBindViewHolder(@NonNull BotoesETextviewMedicamento_Item holder, @SuppressLint("RecyclerView") int position) {
        medicamento m = lista.get(position);

        holder.nome.setText(m.getNomemedicamento());
        holder.horario.setText(m.getHorario());


        holder.excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicamentoDAO dao = new medicamentoDAO(context);

                dao.delete(m, m.getId());
                lista.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });

        //TODO: METODO AONDE O USUARIO CLICA NO BOTAO PARA EDITAR O MEDICAMENTO
        holder.editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MainActivity) {
                    ((MainActivity) context).abrirEditor(lista.get(position));
                }
                }
        });


    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}