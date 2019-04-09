package com.example.martinruiz.myapplication.models;


public class WeatherItem {
    private String date;
    private String temp;
    private String humidity;
    public WeatherItem (String d, String t, String h) {
        date = d;
        temp = t;
        humidity = h;
    }
    public String getDate(){return date;}
    public String getTemp(){return temp;}
    public String getHumidity(){return humidity;}

}
