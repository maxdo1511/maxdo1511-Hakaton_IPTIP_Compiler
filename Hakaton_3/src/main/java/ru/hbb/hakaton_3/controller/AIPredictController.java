package ru.hbb.hakaton_3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hbb.hakaton_3.models.SampleModelRequest;
import ru.hbb.hakaton_3.service.TCPServer;

import java.io.IOException;

@RestController
@RequestMapping("/api/predict")
@CrossOrigin(origins = "http://localhost:3000")
public class AIPredictController {

    @Autowired
    private TCPServer tcpServer;

    @PostMapping("/getSample")
    public ResponseEntity<?> predict(@RequestBody SampleModelRequest request) throws InterruptedException, IOException {
        System.out.println(request.getName() + ":" + request.getData());
        String data = tcpServer.getSample(request.getName(), request.getData());

        return ResponseEntity.ok(data);
    }

    @PostMapping("/getSample2")
    public ResponseEntity<?> predict2(@RequestBody String data) throws InterruptedException, IOException {
        System.out.println(data);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/test")
    public String predict() throws InterruptedException {
        return "Hello";
    }
}
