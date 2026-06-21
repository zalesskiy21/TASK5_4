package com.example.person.config;

import com.example.person.model.User;
import com.example.person.repository.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(PersonRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new User(
                        "Иван",
                        "Иванов",
                        "Иванович",
                        LocalDate.of(1990, 5, 15),
                        "Saransk"));
            }
        };
    }
}
