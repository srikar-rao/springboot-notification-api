package com.dev.org.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.dev.org.client.TodoClient;
import com.dev.org.integration.TodoIntegrationResponse;
import com.dev.org.model.TodoResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pure Unit Test for TodoService using BDD style and MockitoExtension.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock private TodoClient todoClient;

    @InjectMocks private TodoService todoService;

    @Test
    @DisplayName("Given client returns todos, When fetching all todos, Then return the exact list")
    void givenClientReturnsTodosWhenGetAllTodosThenReturnList() {
        // Given
        TodoIntegrationResponse integrationTodo = new TodoIntegrationResponse();
        integrationTodo.setId(1L);
        integrationTodo.setUserId(1L);
        integrationTodo.setTitle("Test Todo");
        integrationTodo.setCompleted(false);

        List<TodoIntegrationResponse> expectedList = List.of(integrationTodo);
        given(todoClient.getAllTodos()).willReturn(expectedList);

        // When
        List<TodoResponse> actualList = todoService.getAllTodos();

        // Then
        assertThat(actualList).isNotNull();
        assertThat(actualList).hasSize(1);
        assertThat(actualList.get(0).getTitle()).isEqualTo("Test Todo");

        // Verify the client was actually called
        verify(todoClient).getAllTodos();
    }
}
