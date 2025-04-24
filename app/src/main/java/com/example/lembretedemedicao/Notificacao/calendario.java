package com.example.lembretedemedicao.Notificacao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class calendario {

    //TODO: CLASSE PARA VERIFICAR HORARIO ENVIADO E FORMATAR O HORARIO QUE ESTÁ COMO STRING NO BANCO DE DADOS

    private Calendar calendar;

    public calendario(String horario) {
        calendar = Calendar.getInstance();
        String[] partes = horario.split(":");
        int hora = Integer.parseInt(partes[0]);
        int minuto = Integer.parseInt(partes[1]);

        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public void adddia() {
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
    }

    public boolean verificarhorarioatual() {
        Calendar atual = Calendar.getInstance();
        return (calendar.get(Calendar.HOUR_OF_DAY) == atual.get(Calendar.HOUR_OF_DAY) &&
                calendar.get(Calendar.MINUTE) == atual.get(Calendar.MINUTE));
    }

    public long getTimeInMillis() {
        return calendar.getTimeInMillis();
    }

    public Calendar getCalendar(String horario) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(horario));
            return cal;
        } catch (Exception e) {
            e.printStackTrace();
            return Calendar.getInstance();
        }
    }

    public String formatarhorario(String horario){
        try {
            SimpleDateFormat formato = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return formato.format(formato.parse(horario));
        } catch (Exception e) {
            e.printStackTrace();
            return horario;
        }
    }

}
