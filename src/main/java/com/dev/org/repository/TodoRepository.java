package com.dev.org.repository;

import com.dev.org.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for TodoEntity.
 */
@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {}
