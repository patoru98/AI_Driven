package com.ai_driven.back_end.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ai_driven.back_end.entity.TodoEntity;
import com.ai_driven.back_end.repository.TodoRepository;

@Service
public class TodoDeleteService {

	private final TodoRepository todoRepository;

	public TodoDeleteService(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}

	@Transactional
	public boolean delete(Integer pKey) {
		Optional<TodoEntity> existing = todoRepository.findById(pKey);
		if (existing.isEmpty()) {
			return false;
		}

		todoRepository.delete(existing.get());
		return true;
	}

}
