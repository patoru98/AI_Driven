package com.ai_driven.back_end.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.ai_driven.back_end.dto.TodoRegistRequest;
import com.ai_driven.back_end.entity.TodoEntity;
import com.ai_driven.back_end.repository.TodoRepository;

@DataJpaTest
@Import({ TodoRegistService.class, TodoDeleteService.class })
class TodoDeleteServiceTest {

	@Autowired
	private TodoDeleteService todoDeleteService;

	@Autowired
	private TodoRegistService todoRegistService;

	@Autowired
	private TodoRepository todoRepository;

	@Test
	void delete_removesTodoWithMatchingPrimaryKey() {
		TodoEntity created = todoRegistService.regist(
				new TodoRegistRequest("削除対象のタスク", null, null, null));

		assertThat(todoDeleteService.delete(created.getPKey())).isTrue();
		assertThat(todoRepository.findById(created.getPKey())).isEmpty();
	}

	@Test
	void delete_returnsFalseWhenPrimaryKeyDoesNotExist() {
		assertThat(todoDeleteService.delete(999)).isFalse();
	}

}
