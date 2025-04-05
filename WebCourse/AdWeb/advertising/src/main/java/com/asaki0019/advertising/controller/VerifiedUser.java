package com.asaki0019.advertising.controller;


import com.asaki0019.advertising.utils.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class VerifiedUser {
    @PostMapping("/verifiedUser")
    public ResponseEntity<Map<String, Object>> Verification(@RequestBody Map<String, String> body) {
        try {
            var jwt = body.get("jwt");
            if (Utils.validateJWT(jwt) == null) {
                return ResponseEntity.status(401).body(Map.of("error", "JWT失效"));
            }
            return ResponseEntity.ok(Map.of("code", 200, "authenticated", "成功"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));

        }
    }
}