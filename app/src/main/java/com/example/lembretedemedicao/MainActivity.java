package com.example.lembretedemedicao;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lembretedemedicao.Notificacao.agendaNotif;
import com.example.lembretedemedicao.bancodedados.medicamento;
import com.example.lembretedemedicao.bancodedados.medicamentoDAO;
import com.example.lembretedemedicao.listademedicacoes.AdaptadorMedicamento;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Calendar;

import android.Manifest;


public class MainActivity extends AppCompatActivity {

    public TextView nomemedicamento;
    Button horario;
    Button salvar;
    Button adicionartelefone;

    RecyclerView lista;

    TextView horarioselecionado;
    String horarios;
    com.example.lembretedemedicao.bancodedados.medicamento medicamento = new medicamento();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        verificarPermissaoSMS();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });


        atualizarlista();

        horario = findViewById(R.id.horarioeditar);
        salvar = findViewById(R.id.salvar);
        adicionartelefone = findViewById(R.id.adicionartelefone);
        nomemedicamento = findViewById(R.id.nomemedicamento);
        horarioselecionado = findViewById(R.id.horarioselecionado);

        //TODO: BOTÃO PARA SELECIONAR O HORÁRIO E COLOCAR NO TEXTVIEW PARA APRESENTAR AO USUÁRIO
        horario.setOnClickListener(v -> {
            Calendar calendario = Calendar.getInstance();
            int horarioatual = calendario.get(Calendar.HOUR_OF_DAY);
            int minutoatual = calendario.get(Calendar.MINUTE);

            @SuppressLint("SetTextI18n") TimePickerDialog timePickerDialog = new TimePickerDialog(
                    MainActivity.this,
                    (TimePicker view, int horario, int minuto) -> {
                        @SuppressLint("DefaultLocale") String hora = String.format("%02d:%02d", horario, minuto);
                        horarioselecionado.setText("O horário selecionado foi: " + hora);
                        horarios = hora;
                    },
                    horarioatual,
                    minutoatual,
                    true

            );
            timePickerDialog.show();
        });

        // TODO: TELA PARA COLOCAR O NÚMERO DE TELEFONE
        adicionartelefone.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
            View botaovisualizar = getLayoutInflater().inflate(R.layout.telatelefone, null);
            bottomSheetDialog.setContentView(botaovisualizar);
            bottomSheetDialog.show();

            EditText telefone = botaovisualizar.findViewById(R.id.telefone);
            Button salvartelefone = botaovisualizar.findViewById(R.id.salvartelefone);

            salvartelefone.setOnClickListener(v1 -> {

                if (telefone.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Preencha o campo telefone!", Toast.LENGTH_SHORT).show();
                } else {
                    String numero = telefone.getText().toString();

                    SharedPreferences preferences = getSharedPreferences("meutelefone", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("numero_salvo", numero);
                    editor.apply();

                    Toast.makeText(MainActivity.this, "Número salvo com sucesso!! " + numero, Toast.LENGTH_SHORT).show();

                }
            });
        });

        //TODO: BOTÃO PARA SALVAR OS DADOS NO BANCO DE DADOS
        salvar.setOnClickListener(new View.OnClickListener() {
            medicamentoDAO dao = new medicamentoDAO(getApplicationContext());

            @Override
            public void onClick(View v) {
                String nome = nomemedicamento.getText().toString();

                if (!nome.isEmpty() && horarios != null && !horarios.isEmpty()) {
                    medicamento.setNomemedicamento(nome);

                    String horarioAtual = horarioselecionado.getText().toString()
                            .replace("O horário selecionado foi: ", "").trim();

                    if (!horarioAtual.isEmpty()) {
                        medicamento.setHorario(horarioAtual);
                    }


                    boolean b = (!nomemedicamento.getText().toString().isEmpty() && !horarios.isEmpty());
                    if (b) {
                        dao.insert(medicamento);
                        nomemedicamento.setText("");
                        horarioselecionado.setText("");
                        agendaNotif.agendar(MainActivity.this, medicamento.getHorario(), medicamento.getId(), medicamento.getNomemedicamento());
                        Toast.makeText(MainActivity.this, "Medicamento salvo com sucesso!!", Toast.LENGTH_SHORT).show();
                        atualizarlista();

                    }
                    agendaNotif.agendar(MainActivity.this, medicamento.getHorario(), medicamento.getId(), medicamento.getNomemedicamento());
                    atualizarlista();
                } else {
                    Toast.makeText(MainActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                }
                atualizarlista();

            }
        });

    }

    //Todo: VERIFICAR PERMISSÃO DE ENVIO DE SMS PARA ANDROID ACIMA DA VERSÃO 11
    private void verificarPermissaoSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 1);
        }
    }

    //TODO: ATUALIZAR A RECYCLERVIEW (LISTA)
    private void atualizarlista() {
        lista = findViewById(R.id.lista);
        lista.setLayoutManager(new LinearLayoutManager(this));
        medicamentoDAO dao = new medicamentoDAO(this);
        AdaptadorMedicamento adapter = new AdaptadorMedicamento(this, dao.listar());
        lista.setAdapter(adapter);
    }


    public void abrirEditor(medicamento medicamento) {
        BottomSheetDialog editarDialog = new BottomSheetDialog(MainActivity.this);
        View editarView = getLayoutInflater().inflate(R.layout.telaeditar, null);
        editarDialog.setContentView(editarView);

        EditText nomeEdit = editarView.findViewById(R.id.editNomeMedicamento);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button horarioEdit = editarView.findViewById(R.id.horarioeditar);
        TextView horarioSelecionado = editarView.findViewById(R.id.horarioselecionado);
        Button salvarEdicao = editarView.findViewById(R.id.botaoSalvarEdicao);


        nomeEdit.setText(medicamento.getNomemedicamento());
        horarioSelecionado.setText("O horario salvo é: " + medicamento.getHorario());

        final String[] novoHorario = {medicamento.getHorario()}; //TODO Variável para armazenar o novo horário selecionado

        //TODO: BOTÃO PARA SELECIONAR O HORÁRIO E COLOCAR NO TEXTVIEW PARA APRESENTAR AO USUÁRIO
        horarioEdit.setOnClickListener(v -> {
            Calendar calendario = Calendar.getInstance();
            int hora = calendario.get(Calendar.HOUR_OF_DAY);
            int minuto = calendario.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                    (view, horaSelecionada, minutoSelecionado) -> {
                        @SuppressLint("DefaultLocale")
                        String horarioFormatado = String.format("%02d:%02d", horaSelecionada, minutoSelecionado);
                        horarioSelecionado.setText("O horário selecionado foi: " + horarioFormatado);
                        novoHorario[0] = horarioFormatado;
                    }, hora, minuto, true);
            timePickerDialog.show();
        });

        salvarEdicao.setOnClickListener(v -> {
            String novoNome = nomeEdit.getText().toString();

            if (!novoNome.isEmpty() && !novoHorario[0].isEmpty()) {
                medicamento.setNomemedicamento(novoNome);
                medicamento.setHorario(novoHorario[0]);

                medicamentoDAO dao = new medicamentoDAO(MainActivity.this);
                dao.update(medicamento);
                agendaNotif.agendar(MainActivity.this, novoHorario[0], medicamento.getId(), novoNome);

                Toast.makeText(MainActivity.this, "Medicamento atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                editarDialog.dismiss();

                atualizarlista();
            } else {
                Toast.makeText(MainActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            }
        });

        editarDialog.show();
    }

}