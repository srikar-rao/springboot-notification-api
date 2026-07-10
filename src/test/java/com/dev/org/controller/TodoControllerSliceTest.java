package com.dev.org.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dev.org.model.TodoResponse;
import com.dev.org.service.TodoService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web Layer Slice Test for TodoController using BDD style (Option 1).
 * Tests only the controller HTTP layer and mocks the service.
 */
@Tag("unit")
@WebMvcTest(TodoController.class)
class TodoControllerSliceTest {

    @Autowired private MockMvc mockMvc;

    // Use @MockitoBean since Spring Boot 3.4+
    @MockitoBean private TodoService todoService;

    @Test
    @DisplayName(
            "Given todos exist, When requesting GET /todos, Then return 200 OK with todos list")
    void givenTodosWhenGetAllTodosThenReturnTodosList() throws Exception {
        // Given
        TodoResponse todo1 = new TodoResponse();
        todo1.setId(1L);
        todo1.setUserId(1L);
        todo1.setTitle("Learn Spring Boot BDD");
        todo1.setCompleted(false);

        TodoResponse todo2 = new TodoResponse();
        todo2.setId(2L);
        todo2.setUserId(1L);
        todo2.setTitle("Write Slice Tests");
        todo2.setCompleted(true);

        given(todoService.getAllTodos()).willReturn(List.of(todo1, todo2));

        // When & Then
        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Learn Spring Boot BDD"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Write Slice Tests"))
                .andExpect(jsonPath("$[1].completed").value(true));
    }
}
