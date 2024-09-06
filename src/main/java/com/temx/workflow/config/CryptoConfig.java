package com.temx.workflow.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class CryptoConfig {

    @Value("${passphrase}")
    private String passphrase;

    @Bean
    public String privateKeyPem() throws IOException {
        // Load the private key from the PEM file located in the resources directory
        return new String(Files.readAllBytes(Paths.get("src/main/resources/private-key.pem")));
//        return "your_private_key";
    }

    @Bean
    public String passphrase() {
       return passphrase;
    }
}
