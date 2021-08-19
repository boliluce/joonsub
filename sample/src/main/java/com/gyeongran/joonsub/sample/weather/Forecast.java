package com.gyeongran.joonsub.sample.weather;

/**
 * Created by yarolegovich on 08.03.2017.
 */

public class Forecast {

    private String cityName;
    private int cityIcon;
    private String temperature;
    private Weather weather;

    public Forecast(String cityName, int cityIcon, String temperature, Weather weather) {
        this.cityName = cityName;
        this.cityIcon = cityIcon;
        this.temperature = temperature;
        this.weather = weather;
    }

    public String getCityName() {
        return cityName;
    }

    public int getCityIcon() {
        return cityIcon;
    }

    public String getTemperature() {
        return temperature;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setCityName(String s){ cityName = s; }
    public void setCityIcon(int i){ cityIcon = i; }
    public void setNumber(String s){ temperature = s; }
    public void setWeather(Weather w){ weather = w; }

}
