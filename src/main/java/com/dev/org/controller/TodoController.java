package com.dev.org.controller;

import com.dev.org.controller.api.TodosApi;
import com.dev.org.model.TodoResponse;
import com.dev.org.service.TodoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Todo REST Controller implementing the generated OpenAPI contract.
 */
@RestController
@RequiredArgsConstructor
public class TodoController implements TodosApi {

    private final TodoService todoService;

    @Override
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        List<TodoResponse> response = todoService.getAllTodos();
        return ResponseEntity.ok(response);
    }
}
