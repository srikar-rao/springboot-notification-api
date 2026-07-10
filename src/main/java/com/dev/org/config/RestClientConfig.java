package com.dev.org.config;

import com.dev.org.client.TodoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private final RestClient.Builder builder;

    public RestClientConfig(RestClient.Builder builder) {
        this.builder = builder;
    }

    @Bean
    public TodoClient todoClient() {
        return new TodoClient(buildRestClient("https://jsonplaceholder.typicode.com"));
    }

    private RestClient buildRestClient(String baseUrl) {
        return this.builder.baseUrl(baseUrl).build();
    }
}
