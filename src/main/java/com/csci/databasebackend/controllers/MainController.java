package com.csci.databasebackend.controllers;

import com.csci.databasebackend.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class MainController {

    @Autowired
    DBService dbService;

    @GetMapping("/")
    Mono<?> getAnObject() {
        return Mono.just(dbService.getAllUsers());
    }

}
