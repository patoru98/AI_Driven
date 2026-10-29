package com.ai_driven.back_end.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.ai_driven.back_end.dto.TodoRegistRequest;
import com.ai_driven.back_end.entity.TodoEntity;
import com.ai_driven.back_end.repository.TodoRepository;
import com.ai_driven.back_end.validation.TodoRegistValidator;

@Service
public class TodoRegistService {

	private final TodoRepository todoRepository;

	public TodoRegistService(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}

	public TodoEntity regist(TodoRegistRequest request) {
		LocalDate today = LocalDate.now();

		TodoEntity entity = new TodoEntity();
		entity.setTask(request.task());
		entity.setRegistedDate(parse(request.registedDate()));
		entity.setLimitDate(parse(request.limitDate()));
		entity.setMemo(request.memo());
		entity.setDoneFlag(false);
		entity.setCreateDate(today);
		entity.setUpdateDate(today);

		return todoRepository.save(entity);
	}

	private LocalDate parse(String value) {
		return value == null ? null : LocalDate.parse(value, TodoRegistValidator.DATE_FORMATTER);
	}

}
