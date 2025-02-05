package com.edceo.springkeycloakdemo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    public Object getUser(JwtAuthenticationToken jwtAuthenticationToken) {
        return jwtAuthenticationToken.getPrincipal();
    }
}
