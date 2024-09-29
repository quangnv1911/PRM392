package com.example.demo.controller;

import com.example.demo.model.Flipper;
import com.example.demo.repo.FlipperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FlipperController {
    @Autowired
    private FlipperRepository flipperRepository;

    @GetMapping("/flipper")
    public List<Flipper> getFlipper (){
        return flipperRepository.findAll();
    }
}
