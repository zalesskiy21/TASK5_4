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

import java.util.List;
import java.util.Locale;

@Service
public class LocationService {

    @Autowired
    private LocationRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${url.weather.service}")
    private String weatherServiceUrl;

    public Object findLocations(String name, String location) {
        String searchName = resolveName(name, location);
        if (searchName != null) {
            return findByName(searchName);
        }
        return findAll();
    }

    public List<Location> findAll() {
        return repository.findAll();
    }

    public Location findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Location save(Location location) {
        location.setId(0);
        return repository.save(location);
    }

    public Location update(String searchName, Location body) {
        Location existing = findByName(searchName);
        existing.setLongitude(body.getLongitude());
        existing.setLatitude(body.getLatitude());
        existing.setName(body.getName());
        return repository.save(existing);
    }

    public void delete(String searchName) {
        Location existing = findByName(searchName);
        repository.delete(existing);
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

    public String requireName(String name, String location) {
        String searchName = resolveName(name, location);
        if (searchName == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Укажите параметр name или location");
        }
        return searchName;
    }

    private String resolveName(String name, String location) {
        if (name != null && !name.isBlank()) {
            return name;
        }
        if (location != null && !location.isBlank()) {
            return location;
        }
        return null;
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
