package com.ducks.goodsduck.commons.controller;

import com.ducks.goodsduck.commons.model.dto.JwtDto;
import com.ducks.goodsduck.commons.service.JwtService;
<<<<<<< HEAD
=======
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
>>>>>>> baf33038316f11c98cca7fc007f0ffbf59bb7433
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
<<<<<<< HEAD
@RequestMapping("/jwt")
public class JwtController {

    @Autowired
    private JwtService jwtService;
=======
@RequiredArgsConstructor
@RequestMapping("/jwt")
public class JwtController {

    private final JwtService jwtService;
>>>>>>> baf33038316f11c98cca7fc007f0ffbf59bb7433

    // 개발 테스트용 (토큰 발급)
    @GetMapping("/gen/token")
    public Map<String, Object> genToken(@RequestParam(value="subject") String subject) {
        String token = jwtService.createJwt(subject, new JwtDto(10L));
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("result", token);
        return map;
    }

    @GetMapping("/get/subject")
    public Map<String, Object> getSubject(@RequestHeader("token") String token) {
        String subject = jwtService.getSubject(token);
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("result", subject);
        return map;
    }

    @GetMapping("/get/payloads")
    public Map<String, Object> getPayloads(@RequestHeader("token") String token) {
        return jwtService.getPayloads(token);
    }
}
