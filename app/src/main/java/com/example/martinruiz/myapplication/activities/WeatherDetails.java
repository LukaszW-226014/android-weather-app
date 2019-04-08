package com.example.martinruiz.myapplication.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martinruiz.myapplication.API.API;
import com.example.martinruiz.myapplication.API.APIServices.WeatherServices;
import com.example.martinruiz.myapplication.R;
import com.example.martinruiz.myapplication.models.CityWeather;
import com.example.martinruiz.myapplication.utils.IconProvider;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDetails extends AppCompatActivity {
    @BindView(R.id.textViewCardCityName) TextView textViewCityName;
    @BindView(R.id.textViewCardWeatherDescription) TextView textViewWeatherDescription;
    @BindView(R.id.textViewCardCurrentTemp) TextView textViewCurrentTemp;
    @BindView(R.id.textViewCardMaxTemp) TextView textViewMaxTemp;
    @BindView(R.id.textViewCardMinTemp) TextView  textViewMinTemp;
    @BindView(R.id.imageViewCardWeatherIcon) ImageView imageViewWeatherIcon;
    @BindView(R.id.detailList) ListView listView;


    private CityWeather cityWeather;
    private CityWeather pastWeather;
    private WeatherServices weatherServices;
    String[] namesOfDays = {
            "SAT","SUN","MON", "TUE", "WED", "THU", "FRI",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if(! bundle.isEmpty()){
            cityWeather = (CityWeather) bundle.getSerializable("city");
        }
        weatherServices = API.getHistory().create(WeatherServices.class);

        setCardData();

    }

    private void getPastWeather(String cityName, int start, int end) {
        Call<CityWeather> cityWeather = weatherServices.getPastWeatherCity(cityName,"days", start, end);
        cityWeather.enqueue(new Callback<CityWeather>() {
            @Override
            public void onResponse(Call<CityWeather> call, Response<CityWeather> response) {
                if(response.code()==200){
                    pastWeather = response.body();
                }
            }

            @Override
            public void onFailure(Call<CityWeather> call, Throwable t) {
                Toast.makeText(WeatherDetails.this,"Sorry, can't refresh right now.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setCardData() {
        textViewCityName.setText(cityWeather.getCity().getName()+", "+cityWeather.getCity().getCountry());
        textViewWeatherDescription.setText(cityWeather.getWeeklyWeather().get(0).getWeatherDetails().get(0).getLongDescription());
        textViewCurrentTemp.setText((int) cityWeather.getWeeklyWeather().get(0).getTemp().getDay()+"°");
        textViewMaxTemp.setText((int) cityWeather.getWeeklyWeather().get(0).getTemp().getMax()+"°");
        textViewMinTemp.setText((int) cityWeather.getWeeklyWeather().get(0).getTemp().getMin()+"°");



        String weatherDescription = cityWeather.getWeeklyWeather().get(0).getWeatherDetails().get(0).getShotDescription();
        Picasso.with(this).load(IconProvider.getImageIcon(weatherDescription)).into(imageViewWeatherIcon);




        }
        
    }

