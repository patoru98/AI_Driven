package com.ai_driven.back_end.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.ai_driven.back_end.repository.TodoRepository;

@ExtendWith(MockitoExtension.class)
class TodoGetServiceTest {

	@Mock
	private TodoRepository todoRepository;

	@InjectMocks
	private TodoGetService todoGetService;

	@Test
	void getAll_requestsTodosInPrimaryKeyAscendingOrder() {
		todoGetService.getAll();

		verify(todoRepository).findAll(Sort.by(Sort.Direction.ASC, "pKey"));
	}

}