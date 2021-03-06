package com.ducks.goodsduck.commons.service;

import com.ducks.goodsduck.commons.model.dto.JwtDto;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface JwtService {

    String createJwt(String subject, JwtDto jwtDto);
    String getSubject(String token);
    Map<String, Object> getPayloads(String token);
}
