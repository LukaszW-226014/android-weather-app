package com.example.martinruiz.myapplication.activities;

import android.graphics.Color;
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
import com.example.martinruiz.myapplication.R;
import com.example.martinruiz.myapplication.models.CityWeather;
import com.example.martinruiz.myapplication.models.Weather;
import com.example.martinruiz.myapplication.models.WeatherItem;
import com.example.martinruiz.myapplication.utils.IconProvider;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


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

        drawPlot(cityWeather.getWeeklyWeather());

        }

    private void drawPlot(List<Weather> weatherList) {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        DataPoint[] dataTemp = new DataPoint[weatherList.size()];
        DataPoint[] dataHum = new DataPoint[weatherList.size()];
        float minTemp = 200, maxTemp = -200;

        for(int i = 0; i <  weatherList.size(); i++) {
            float temp = weatherList.get(i).getTemp().getDay();
            dataTemp[i] = new DataPoint(weatherList.get(i).getDate()*1000, temp);
            if(minTemp > temp) minTemp = temp;
            if(maxTemp < temp) maxTemp = temp;
            dataHum[i] = new DataPoint(weatherList.get(i).getDate()*1000, weatherList.get(i).getHumidity());
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataTemp);
        graph.addSeries(series);

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(dataHum);
        graph.getSecondScale().addSeries(series2);
        graph.getViewport().setMaxY(maxTemp);
        graph.getViewport().setMinY(minTemp);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

// set manual x bounds to have nice steps
        graph.getViewport().setMinX(new Date(weatherList.get(0).getDate()*1000).getTime());
        graph.getViewport().setMaxX(new Date(weatherList.get(weatherList.size()-1).getDate()*1000).getTime());
        graph.getViewport().setXAxisBoundsManual(true);

// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(1);
        nf.setMinimumIntegerDigits(1);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(null, nf) {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return getRealDate((long) value);
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + "\u00b0C";
                }
            }
        });

        graph.getSecondScale().setMinY(0);
        graph.getSecondScale().setMaxY(100);
        series2.setColor(Color.RED);
        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.RED);
        graph.getSecondScale().setLabelFormatter(new DefaultLabelFormatter(null, nf) {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return getRealDate((long) value);
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + "%";
                }
            }
        });

        series.setTitle("Temperture");
        series2.setTitle("Humidity");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    private String getRealDate(long date) {
        DateFormat simple = new SimpleDateFormat("EEE dd.MM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date*1000);
        return simple.format(calendar.getTime());
    }

}

