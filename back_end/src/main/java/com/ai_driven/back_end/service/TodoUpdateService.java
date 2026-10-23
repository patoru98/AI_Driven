package com.ai_driven.back_end.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ai_driven.back_end.dto.TodoRegistRequest;
import com.ai_driven.back_end.entity.TodoEntity;
import com.ai_driven.back_end.repository.TodoRepository;
import com.ai_driven.back_end.validation.TodoRegistValidator;

@Service
public class TodoUpdateService {

	private final TodoRepository todoRepository;

	public TodoUpdateService(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}

	public Optional<TodoEntity> update(Integer pKey, TodoRegistRequest request) {
		Optional<TodoEntity> existing = todoRepository.findById(pKey);
		if (existing.isEmpty()) {
			return Optional.empty();
		}

		TodoEntity todo = existing.get();
		todo.setTask(request.task());
		todo.setRegistedDate(parse(request.registedDate()));
		todo.setLimitDate(parse(request.limitDate()));
		todo.setMemo(request.memo());
		todo.setUpdateDate(LocalDate.now());
		return Optional.of(todoRepository.save(todo));
	}

	private LocalDate parse(String value) {
		return value == null ? null : LocalDate.parse(value, TodoRegistValidator.DATE_FORMATTER);
	}

}
