package com.gyeongran.joonsub.sample.weather;

/**
 * Created by yarolegovich on 08.03.2017.
 */

public enum Weather {

    PERIODIC_CLOUDS("Periodic Clouds"),
    CLOUDY("Cloudy"),
    MOSTLY_CLOUDY("Mostly Cloudy"),
    PARTLY_CLOUDY("Partly Cloudy"),
    CLEAR("Clear"),

    LIGHT_1("광합성하기 좋은 것 같아요"),
    LIGHT_2("선선한 날씨에요"),
    LIGHT_3("길어지면 시들지도 몰라요"),

    TEMPERATURE_1("얼기 전에 실내로 옮겨주세요"),
    TEMPERATURE_2("시원해요"),
    TEMPERATURE_3("따뜻해요"),
    TEMPERATURE_4("금방 말라버릴지도 몰라요"),

    HUMIDITY_1("물이 모자라요"),
    HUMIDITY_2("적당해요"),
    HUMIDITY_3("아주 좋아요"),
    HUMIDITY_4("뿌리가 썩을 것 같아요");

    private String displayName;

    Weather(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
