package com.dev.org.client;

import com.dev.org.integration.TodoIntegrationResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class TodoClient {

    private final RestClient restClient;

    public TodoClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<TodoIntegrationResponse> getAllTodos() {
        return restClient
                .get()
                .uri("/todos")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
