package com.example.lembretedemedicao.Notificacao;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;


import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.lembretedemedicao.MainActivity;
import com.example.lembretedemedicao.bancodedados.Banco;
import com.example.lembretedemedicao.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Alarme extends BroadcastReceiver {
    public static void cancelarAlarme(MainActivity mainActivity, int id) {
        Intent intent = new Intent(mainActivity, Alarme.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mainActivity,
                id,
                intent
                , PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificacao = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelID = "1";

        SQLiteDatabase db = new Banco(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM medicamento", null);


        Calendar now = Calendar.getInstance();
        int Hora = now.get(Calendar.HOUR_OF_DAY);
        int Minuto = now.get(Calendar.MINUTE);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String medicamento = cursor.getString(1);
            String horario = cursor.getString(2);

            calendario cal = new calendario(horario);

            cal.formatarhorario(horario);
            Calendar medicamentoHorario = cal.getCalendar(horario);

            int medicamentoHora = medicamentoHorario.get(Calendar.HOUR_OF_DAY);
            int medicamentoMinuto = medicamentoHorario.get(Calendar.MINUTE);

            if (medicamentoHora == Hora && medicamentoMinuto == Minuto) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(
                            channelID,
                            "Tomar Medicamento!!",
                            NotificationManager.IMPORTANCE_HIGH
                    );
                    notificacao.createNotificationChannel(channel);

                    NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelID)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("Lembrete de Medicação")
                            .setContentText("Hora de tomar seu medicamento: " + medicamento)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .addAction(R.drawable.ic_launcher_background, "Já tomei", getPendingIntent(context, medicamento, id, horario)); // Ação para 'Já tomei'
                    notificacao.notify(id, notification.build());
                }
            }
        }
        cursor.close();

        //TODO: Verifica se a ação recebida foi "clicado" (quando o usuário clica em "Já tomei")
        if ("clicado".equals(intent.getStringExtra("acao"))) {
            int id = intent.getIntExtra("id", -1);
            String horario = intent.getStringExtra("horario");
            String medicamento = intent.getStringExtra("medicamento");

            if (id != -1 && horario != null) {
                agendaNotif.agendar(context, horario, id, medicamento);
                enviarSMS(context, medicamento);
                notificacao.cancel(id);
            }
        }
    }

    //TODO: Método para criar o PendingIntent da ação 'Já tomei'
    @SuppressLint("ScheduleExactAlarm")
    private PendingIntent getPendingIntent(Context context, String medicamento, int id, String horario) {
        Intent intent = new Intent(context, Alarme.class);
        intent.putExtra("medicamento", medicamento);
        intent.putExtra("id", id);
        intent.putExtra("horario", horario);
        intent.putExtra("acao", "clicado");

        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    //TODO Método para enviar o SMS
    private void enviarSMS(Context context, String medicamento) {
        SharedPreferences preferences = context.getSharedPreferences("meutelefone", Context.MODE_PRIVATE);
        String numero = preferences.getString("numero_salvo", null);

        if (numero == null){
            Toast.makeText(context, "Não é possivel enviar o SMS pois não há um número salvo", Toast.LENGTH_SHORT).show();
        }else {
        if (HorarioPermitido()) {
            @SuppressLint("SimpleDateFormat") String hora = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            String mensagemSMS = "Medicamento " + medicamento + " tomado às " + hora;

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numero, null, mensagemSMS, null, null);
        } else {
            Toast.makeText(context, "Não é possivel enviar o SMS pois está fora do horário permitido (6h-22h)", Toast.LENGTH_SHORT).show();
        }
        }
    }

    //TODO Método para verificar se o horário está dentro do permitido (6h-22h)
    public boolean HorarioPermitido() {
        Calendar agora = Calendar.getInstance();
        int hora = agora.get(Calendar.HOUR_OF_DAY);
        return hora >= 6 && hora < 22;
    }
}