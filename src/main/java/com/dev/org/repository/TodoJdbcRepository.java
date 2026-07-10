package com.dev.org.repository;

import com.dev.org.entity.TodoEntity;
import com.dev.org.util.TodoSqlUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoJdbcRepository {

    private final JdbcClient jdbcClient;
    private final TodoSqlUtil todoSqlUtil;

    public List<TodoEntity> findAll() {
        return jdbcClient.sql(todoSqlUtil.getGetAllTodosQuery()).query(TodoEntity.class).list();
    }
}
