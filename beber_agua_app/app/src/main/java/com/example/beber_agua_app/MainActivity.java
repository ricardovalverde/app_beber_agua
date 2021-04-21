package com.example.beber_agua_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    ActionBar actionBar;
    private Button button_notify;
    private EditText edit_txt_number_interval;
    private TimePicker timePicker;
    private boolean activated = false;
    private SharedPreferences preferences;
    // SharedPreferences -> Mini banco de dados para guardar as preferências para que quando o app for reiniciado não perder os dados;

    private final View.OnClickListener notifyListenner = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (!activated) {

                if (!intervalIsValid()) return;

                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                int interval = Integer.parseInt(edit_txt_number_interval.getText().toString());

                updatePreferences(true, hour, minute, interval);
                setupUI(true, preferences);
                notificationAlarm(true, hour, minute, interval);
                alert(R.string.alerta);


                activated = true;


            } else {
                updatePreferences(false, 0, 0, 0);
                setupUI(false, preferences);
                notificationAlarm(false, 0, 0, 0);
                alert(R.string.alerta_pause);


                activated = false;
            }
        }
    };

    //AEB5BD
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#61b3de"));
        actionBar.setBackgroundDrawable(colorDrawable);

        preferences = getSharedPreferences("db", Context.MODE_PRIVATE);//MODE_PRIVATE para que nenhum outro app tenha acesso as informações;
        activated = preferences.getBoolean("activated", false);

        button_notify = findViewById(R.id.button_notify);
        edit_txt_number_interval = findViewById(R.id.edit_txt_number_interval);
        timePicker = findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);

        button_notify.setOnClickListener(notifyListenner);

        setupUI(activated, preferences);


    }

    private void setupUI(boolean activated, SharedPreferences preferences) {
        if (activated) {


            button_notify.setText(R.string.pause);
            button_notify.setBackgroundResource(R.drawable.bg_button_pause);


            /*Bloco onde vai procurar a horas e caso ainda não tenha um registro vai passar os valores atuais do timerpicker*/

            int interval = preferences.getInt("KEY_INTERVAL", 0);
            int hour = preferences.getInt("KEY_HOUR", timePicker.getCurrentHour());
            int minute = preferences.getInt("KEY_MINUTE", timePicker.getCurrentMinute());

            edit_txt_number_interval.setText(String.valueOf(interval));
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
        } else {
            button_notify.setText(R.string.notify);
            button_notify.setBackgroundResource(R.drawable.bg_button);
        }
    }

    private void alert(int resId) {
        Toast.makeText(MainActivity.this, resId, Toast.LENGTH_SHORT).show();
    }

    private void updatePreferences(boolean add, int hour, int minute, int interval) {

        SharedPreferences.Editor editor = preferences.edit(); // Editor para poder guardar os dados
        editor.putBoolean("activated", add);

        if (add) {
            editor.putInt("KEY_INTERVAL", interval);
            editor.putInt("KEY_HOUR", hour);
            editor.putInt("KEY_MINUTE", minute);
        } else {
            editor.remove("KEY_INTERVAL");
            editor.remove("KEY_HOUR");
            editor.remove("KEY_MINUTE");
        }
        editor.apply();

    }

    private void notificationAlarm(boolean add, int hour, int minute, int interval) {

        Intent notificationIntent = new Intent(MainActivity.this, NotificationPublisher.class);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (add) {
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
            notificationIntent.putExtra(NotificationPublisher.KEY_NOTIFICATION, "Hora de beber água");
            PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval * 60 * 1000, broadcast);
        } else {
            PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this, 0, notificationIntent, 0);
            alarmManager.cancel(broadcast);
        }

    }

    private boolean intervalIsValid() {

        String sInterval = edit_txt_number_interval.getText().toString();

        if (sInterval.isEmpty()) {
            alert(R.string.interval_empty);
            return false;
        } else if (sInterval.equals("0")) {
            alert(R.string.interval_null);
            return false;
        }
        return true;
    }
}

