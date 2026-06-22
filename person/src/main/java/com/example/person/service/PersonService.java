package com.example.person.service;

import com.example.person.model.User;
import com.example.person.model.Weather;
import com.example.person.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${url.location.service}")
    private String locationWeatherUrl;

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public User save(User user) {
        user.setId(0);
        return repository.save(user);
    }

    public User update(int id, User user) {
        User existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        existing.setFirstname(user.getFirstname());
        existing.setSurname(user.getSurname());
        existing.setLastname(user.getLastname());
        existing.setBirthday(user.getBirthday());
        existing.setLocation(user.getLocation());
        return repository.save(existing);
    }

    public void delete(int id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }

    public Optional<Weather> getWeather(int id) {
        return repository.findById(id).map(user -> {
            String encodedName = URLEncoder.encode(user.getLocation(), StandardCharsets.UTF_8);
            String url = locationWeatherUrl + "?name=" + encodedName;
            return restTemplate.getForObject(url, Weather.class);
        });
    }
}
