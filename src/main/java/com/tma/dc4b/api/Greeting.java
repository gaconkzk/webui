package com.tma.dc4b.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
@RestController
public class Greeting {
    @GetMapping("/greeting")
    public Map<String, Object> greeting() {
        Map<String, Object> model = new HashMap<>(  );
        model.put( "id", UUID.randomUUID().toString() );
        model.put( "content", "okie baby! like thissssss! "+model.get("id") );

        return model;
    }

    @GetMapping("/user")
    public Principal user(Principal user) {
        return user;
    }
}
