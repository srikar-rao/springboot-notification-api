package com.dev.org.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pure domain model representing a Todo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo {

    private Long id;

    private Long userId;

    private String title;

    private Boolean completed;
}
