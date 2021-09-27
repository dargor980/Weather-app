package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapp.API.API;
import com.example.weatherapp.API.APIServices.WeatherService;
import com.example.weatherapp.Models.City;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText EditTextSearch;
    private TextView textViewCity;
    private TextView textViewDescription;
    private TextView textViewTemp;
    private ImageView img;
    private Button btn;
    private final int INTERNET_CODE = 100;
    private final int NETWORK_ACCESS_CODE = 200;

    private WeatherService service;
    private Call<City> cityCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUI();
        service = API.getApi().create(WeatherService.class);
        setCurrentCity();
        btn.setOnClickListener(this);
        startService(new Intent(this, ServiceWeather.class));


    }

    private void setUI() {
        EditTextSearch = (EditText) findViewById(R.id.editTextSearch);
        textViewCity = (TextView) findViewById(R.id.textViewCity);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        textViewTemp = (TextView) findViewById(R.id.textViewTemp);
        img = (ImageView) findViewById(R.id.imageView);
        btn = (Button) findViewById(R.id.buttonSearch);
    }

    @Override
    public void onClick(View view) {
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (CheckPermission(Manifest.permission.INTERNET) && CheckPermission(Manifest.permission.ACCESS_NETWORK_STATE)) {*/
        String city = EditTextSearch.getText().toString();
        if (city != "") {
            cityCall = service.getCity(city, API.APPKEY, "metric", "es");
            cityCall.enqueue(new Callback<City>() {
                @Override
                public void onResponse(Call<City> call, Response<City> response) {
                    City city = response.body();
                    setResult(city);

                }

                @Override
                public void onFailure(Call<City> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error" + t, Toast.LENGTH_LONG).show();
                }
            });
        }


            /*} else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.INTERNET) || !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_NETWORK_STATE)) {
                    requestPermissions(new String[]{Manifest.permission.INTERNET}, INTERNET_CODE);
                    requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, NETWORK_ACCESS_CODE);

                } else {
                    Toast.makeText(MainActivity.this, "Por favor, habilite los permisos", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(i);

                }
            }*/
        //}
    }

    private void setResult(City city) {
        textViewCity.setText(city.getName() + ", " + city.getCountry());
        textViewDescription.setText(city.getDescription());
        textViewTemp.setText((city.getTemperature() + "°C"));
        Picasso.with(this).load(API.BASE_ICONS + city.getIcon() + API.EXTENSION_ICONS).into(img);
    }

    private void setCurrentCity() {
        String currentCity = "Santiago,CL";
        cityCall = service.getCity(currentCity, API.APPKEY, "metric", "es");
        cityCall.enqueue(new Callback<City>() {
            @Override
            public void onResponse(Call<City> call, Response<City> response) {
                City city = response.body();
                setResult(city);
            }

            @Override
            public void onFailure(Call<City> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error al obtener información", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*private boolean CheckPermission(String permission){
        int result = this.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }*/
}