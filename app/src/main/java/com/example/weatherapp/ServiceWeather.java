package com.example.weatherapp;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.weatherapp.API.API;
import com.example.weatherapp.API.APIServices.WeatherService;
import com.example.weatherapp.Models.City;
import com.example.weatherapp.Notifications.NotificationHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceWeather extends Service {
    MyTask mytask;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Servicio creado!", Toast.LENGTH_LONG).show();
        mytask = new MyTask();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mytask.execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Servicio destruido!", Toast.LENGTH_LONG).show();

    }

    private class MyTask extends AsyncTask<String, String, String> {
        private DateFormat dateFormat;
        private String date;
        private boolean cent;
        private String title;
        private String message;
        private int counter = 0;
        private NotificationHandler notificationHandler = new NotificationHandler(ServiceWeather.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dateFormat = new SimpleDateFormat("HH:mm:ss");
            cent = true;
        }

        @Override
        protected String doInBackground(String... params) {
            while (cent) {
                date = dateFormat.format((new Date()));
                try {
                    String city = "Santiago";
                    WeatherService service = API.getApi().create(WeatherService.class);
                    Call<City> cityCall = service.getCity(city, API.APPKEY, "metric", "es");
                    cityCall.enqueue(new Callback<City>() {
                        @Override
                        public void onResponse(Call<City> call, Response<City> response) {
                            City city = response.body();
                            setResult(city);

                        }

                        @Override
                        public void onFailure(Call<City> call, Throwable t) {

                        }
                    });
                    publishProgress(title, message);
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(getApplicationContext(), "Temperatura en " + values[0] + ": " + values[1], Toast.LENGTH_LONG).show();
            sendNotification(values[0], values[1]);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cent = false;
        }

        private void setResult(City city) {
            title = city.getName();
            message = Integer.toString(city.getTemperature()) + "°C";
        }

        private void sendNotification(String title, String message) {
            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(message)) {
                Notification.Builder nb = notificationHandler.createNotification(title, message, true);
                notificationHandler.getManager().notify(++counter, nb.build());
                notificationHandler.publishNotificationSummaryGroup(true);
            }
        }
    }
}