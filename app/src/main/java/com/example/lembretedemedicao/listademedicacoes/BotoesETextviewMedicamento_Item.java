package com.example.lembretedemedicao.listademedicacoes;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lembretedemedicao.R;

public class BotoesETextviewMedicamento_Item extends RecyclerView.ViewHolder {
    TextView nome, horario;
    Button excluir,editar;

    public BotoesETextviewMedicamento_Item(@NonNull View itemView) {

        super(itemView);
        nome = itemView.findViewById(R.id.NomeMedicamento);
        horario = itemView.findViewById(R.id.HorarioMedicamento);
        excluir = itemView.findViewById(R.id.excluir);
        editar = itemView.findViewById(R.id.editar);
    }
}
