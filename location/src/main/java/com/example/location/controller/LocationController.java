package com.example.location.controller;

import com.example.location.model.Location;
import com.example.location.model.Weather;
import com.example.location.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping
    public Object findLocations(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location) {
        return locationService.findLocations(name, location);
    }

    @PostMapping
    public Location save(@RequestBody Location location) {
        return locationService.save(location);
    }

    @PutMapping
    public Location update(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestBody Location body) {
        return locationService.update(locationService.requireName(name, location), body);
    }

    @DeleteMapping
    public void delete(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location) {
        locationService.delete(locationService.requireName(name, location));
    }

    @GetMapping("/weather")
    public Weather getWeather(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location) {
        return locationService.getWeatherByLocationName(locationService.requireName(name, location));
    }
}
