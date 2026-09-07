package com.ai_driven.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ai_driven.back_end.entity.TodoEntity;

public interface TodoRepository extends JpaRepository<TodoEntity, Integer> {
}
