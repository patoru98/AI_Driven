package com.ai_driven.back_end.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.ai_driven.back_end.dto.TodoRegistRequest;
import com.ai_driven.back_end.entity.TodoEntity;
import com.ai_driven.back_end.repository.TodoRepository;

@DataJpaTest
@Import(TodoRegistService.class)
class TodoRegistServiceTest {

	@Autowired
	private TodoRegistService todoRegistService;

	@Autowired
	private TodoRepository todoRepository;

	@Test
	void regist_savesTodoWithCreateAndUpdateDate() {
		TodoRegistRequest request = new TodoRegistRequest("買い物へ行く", "2026/01/05", "2026/01/31", "牛乳を買う");

		TodoEntity saved = todoRegistService.regist(request);

		assertThat(saved.getPKey()).isNotNull();
		assertThat(todoRepository.findById(saved.getPKey())).hasValueSatisfying(todo -> {
			assertThat(todo.getTask()).isEqualTo("買い物へ行く");
			assertThat(todo.getRegistedDate()).isEqualTo(LocalDate.of(2026, 1, 5));
			assertThat(todo.getLimitDate()).isEqualTo(LocalDate.of(2026, 1, 31));
			assertThat(todo.getMemo()).isEqualTo("牛乳を買う");
			assertThat(todo.getCreateDate()).isEqualTo(LocalDate.now());
			assertThat(todo.getUpdateDate()).isEqualTo(LocalDate.now());
		});
	}

	@Test
	void regist_savesTodoWithoutOptionalItems() {
		TodoEntity saved = todoRegistService.regist(new TodoRegistRequest("買い物へ行く", null, null, null));

		assertThat(saved.getRegistedDate()).isNull();
		assertThat(saved.getLimitDate()).isNull();
		assertThat(saved.getMemo()).isNull();
	}

}
