package com.ai_driven.back_end.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ai_driven.back_end.entity.TodoEntity;
import com.ai_driven.back_end.repository.TodoRepository;

@Service
public class TodoGetService {

	private final TodoRepository todoRepository;

	public TodoGetService(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}

	public List<TodoEntity> getAll() {
		return todoRepository.findAll(Sort.by(Sort.Direction.ASC, "pKey"));
	}

}