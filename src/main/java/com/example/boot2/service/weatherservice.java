package com.example.boot2.service;

import com.example.boot2.cache.AppCache;
import com.example.boot2.constants.PlaceHolder;
import com.journal.response.pojo.weatherpojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;

@Service
public class weatherservice {

    @Value("${weather.api.key}")
    public String apikey;

    @Autowired
    private AppCache cache;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    public weatherpojo getCurrentWeather(String city) {
        weatherpojo weatherpo = redisService.get("Weather_0f_" + city, weatherpojo.class);
        if (weatherpo != null) {
            return weatherpo;
        }

        String apiTemplate = cache.appCache.get("weather_api");

        if (apiTemplate == null) {
            return null;
        }

        String finalUrl = apiTemplate
                .replace(PlaceHolder.API_KEY, apikey)
                .replace(PlaceHolder.CITY, city);

        ResponseEntity<weatherpojo> response = restTemplate.exchange(finalUrl, HttpMethod.GET, null, weatherpojo.class);
        weatherpojo weather = response.getBody();

        if (weather != null) {
            redisService.set("Weather_Of_" + city, weather, 300L);
        }
        return weather;
    }
}
//json to java object is called deserialization
// example if you want to send the post request to the server
//String requestbody ="{/n"+
//           " "username":"Shravan" " +,+
//           " "password":"desiboy""
//           };
//HttpEntity<String> entity = new HttpEntity<>(requestbody);
//to senf the headers or user
//HttpHeader httpheader = new HttpHeader();
//httpheader.set("Key","Value");
//Users user = user.builder().username("Shravan").password("desiboy").build();
//HttpEntity<String> entity = new HttpEntity<>(users ,httpheader);