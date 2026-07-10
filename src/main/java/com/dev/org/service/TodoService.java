package com.dev.org.service;

import com.dev.org.client.TodoClient;
import com.dev.org.integration.TodoIntegrationResponse;
import com.dev.org.model.TodoResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoClient todoClient;

    public List<TodoResponse> getAllTodos() {
        return todoClient.getAllTodos().stream().map(this::mapToResponse).toList();
    }

    private TodoResponse mapToResponse(TodoIntegrationResponse integrationResponse) {
        TodoResponse response = new TodoResponse();
        response.setId(integrationResponse.getId());
        response.setUserId(integrationResponse.getUserId());
        response.setTitle(integrationResponse.getTitle());
        response.setCompleted(integrationResponse.getCompleted());
        return response;
    }
}
