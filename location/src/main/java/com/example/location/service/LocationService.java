package com.example.location.service;

import com.example.location.model.Location;
import com.example.location.model.Weather;
import com.example.location.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class LocationService {

    @Autowired
    private LocationRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${url.weather.service}")
    private String weatherServiceUrl;

    public Location findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Weather getWeatherByLocationName(String name) {
        Location location = findByName(name);
        String url = String.format(
                Locale.US,
                "%s?lat=%s&lon=%s",
                weatherServiceUrl,
                location.getLatitude(),
                location.getLongitude());
        try {
            WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);
            if (response == null || response.getMain() == null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Weather-сервис вернул пустой ответ");
            }
            return response.getMain();
        } catch (HttpStatusCodeException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Weather-сервис недоступен или вернул ошибку. Запущен ли он на порту 8082?");
        }
    }

    private static class WeatherResponse {
        private Weather main;

        public Weather getMain() {
            return main;
        }

        public void setMain(Weather main) {
            this.main = main;
        }
    }
}
