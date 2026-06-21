package com.example.location.controller;

import com.example.location.model.Location;
import com.example.location.model.Weather;
import com.example.location.repository.LocationRepository;
import com.example.location.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationRepository repository;

    @Autowired
    private LocationService locationService;

    @GetMapping
    public Object findLocations(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location) {
        String searchName = resolveName(name, location);
        if (searchName != null) {
            return repository.findByName(searchName)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        }
        return repository.findAll();
    }

    @PostMapping
    public Location save(@RequestBody Location location) {
        location.setId(0);
        return repository.save(location);
    }

    @PutMapping
    public Location update(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestBody Location body) {
        String searchName = requireName(name, location);
        Location existing = repository.findByName(searchName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        existing.setLongitude(body.getLongitude());
        existing.setLatitude(body.getLatitude());
        existing.setName(body.getName());
        return repository.save(existing);
    }

    @DeleteMapping
    public void delete(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location) {
        String searchName = requireName(name, location);
        Location existing = repository.findByName(searchName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        repository.delete(existing);
    }

    @GetMapping("/weather")
    public Weather getWeather(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location) {
        return locationService.getWeatherByLocationName(requireName(name, location));
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

    private String requireName(String name, String location) {
        String searchName = resolveName(name, location);
        if (searchName == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Укажите параметр name или location");
        }
        return searchName;
    }
}
