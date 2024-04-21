package com.example.springsecurity.models;

public class DemoUser {

    public void demo() {

        User user = User.builder()
                .firstname("Karikari")
                .lastname("Adade")
                .build();

    }

}
