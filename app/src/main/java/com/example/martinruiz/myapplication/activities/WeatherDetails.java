package com.example.martinruiz.myapplication.activities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martinruiz.myapplication.API.API;
import com.example.martinruiz.myapplication.API.APIServices.WeatherServices;
import com.example.martinruiz.myapplication.R;
import com.example.martinruiz.myapplication.models.CityWeather;
import com.example.martinruiz.myapplication.models.Weather;
import com.example.martinruiz.myapplication.models.WeatherItem;
import com.example.martinruiz.myapplication.utils.IconProvider;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    ArrayAdapter adapter;
    private ArrayList<WeatherItem> weatherList;
    private CityWeather cityWeather;
    String[] namesOfDays = {
            "SAT","SUN","MON", "TUE", "WED", "THU", "FRI",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        weatherList = new ArrayList<>();
        if(! bundle.isEmpty()){
            cityWeather = (CityWeather) bundle.getSerializable("city");
        }
        adapter = new ArrayAdapter<WeatherItem>(this,R.layout.list_item, weatherList) {
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View listItem = convertView;
                if(listItem == null)
                    listItem = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);

               WeatherItem currentWeather = weatherList.get(position);


                TextView date = (TextView) listItem.findViewById(R.id.text1);
                date.setText(currentWeather.getDate());
                TextView temp = (TextView) listItem.findViewById(R.id.text2);
                temp.setText(currentWeather.getTemp());
                TextView humidity = (TextView) listItem.findViewById(R.id.text3);
                humidity.setText(currentWeather.getHumidity());


                return listItem;
            }
        };
        listView.setAdapter(adapter);

        setCardData();
    }


    private void setCardData() {
        textViewCityName.setText(cityWeather.getCity().getName()+", "+cityWeather.getCity().getCountry());
        textViewWeatherDescription.setText(cityWeather.getWeeklyWeather().get(0).getWeatherDetails().get(0).getLongDescription());
        textViewCurrentTemp.setText((int) cityWeather.getWeeklyWeather().get(0).getTemp().getDay()+"°");
        textViewMaxTemp.setText((int) cityWeather.getWeeklyWeather().get(0).getTemp().getMax()+"°");
        textViewMinTemp.setText((int) cityWeather.getWeeklyWeather().get(0).getTemp().getMin()+"°");



        String weatherDescription = cityWeather.getWeeklyWeather().get(0).getWeatherDetails().get(0).getShotDescription();
        Picasso.with(this).load(IconProvider.getImageIcon(weatherDescription)).into(imageViewWeatherIcon);
        weatherList.add(new WeatherItem("Date", "Temperature", "Humidity"));
        for(Weather weather : cityWeather.getWeeklyWeather()) {
            weatherList.add(new WeatherItem(getRealDate(weather.getDate()), weather.getTemp().getDay() + "\u00b0C", weather.getHumidity() + "%"));
        }
        adapter.notifyDataSetChanged();

        }

    private String getRealDate(int date) {
        DateFormat simple = new SimpleDateFormat("dd.MM");
        Date res = new Date(date);
        return simple.format(res);
    }

}

