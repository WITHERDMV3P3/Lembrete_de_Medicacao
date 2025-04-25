package com.example.lembretedemedicao.Notificacao;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class agendaNotif {
    @SuppressLint("ScheduleExactAlarm")
    public static void agendar(Context context,String horario, int id, String medicamento) {
        //TODO: AGENDAR O HORARIO DO MEDICAMENTO PARA SER TOMADO E REAGENDAR AO CLICAR NO BOTAO "Já tomei" NA NOTIFICAÇÃO
        calendario cal = new calendario(horario);

        if (cal.verificarhorarioatual()) {
            cal.adddia();
        }

        Intent intent = new Intent(context, Alarme.class);
        intent.putExtra("id", id);
        intent.putExtra("horario", horario);
        intent.putExtra("medicamento", medicamento);

        int idUnico = (id + horario).hashCode();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                idUnico,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }
}
