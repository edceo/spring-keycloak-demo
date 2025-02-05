package com.edceo.springkeycloakdemo.controller;

import com.edceo.springkeycloakdemo.annotations.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class DemoController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a public endpoint";
    }

    @GetMapping("/private")
    public String privateEndpoint() {
        return "This is a private endpoint";
    }

    @GetMapping("/user")
    public String userEndpoint(@CurrentUser Object user) {
        return "This is a user endpoint. User: " + user;
    }
}
