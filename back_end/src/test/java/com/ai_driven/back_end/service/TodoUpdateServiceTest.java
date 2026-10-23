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
@Import({ TodoRegistService.class, TodoUpdateService.class })
class TodoUpdateServiceTest {

	@Autowired
	private TodoUpdateService todoUpdateService;

	@Autowired
	private TodoRegistService todoRegistService;

	@Autowired
	private TodoRepository todoRepository;

	@Test
	void update_changesRequestedFieldsAndPreservesOtherFields() {
		TodoEntity created = todoRegistService.regist(
				new TodoRegistRequest("元のタスク", "2026/01/05", "2026/01/31", "元の備考"));
		created.setDoneFlag(true);
		created = todoRepository.save(created);
		LocalDate createDate = created.getCreateDate();

		TodoEntity updated = todoUpdateService.update(created.getPKey(),
				new TodoRegistRequest("更新後のタスク", "2026/02/01", "2026/02/28", "更新後の備考"))
				.orElseThrow();

		assertThat(todoRepository.findById(created.getPKey())).hasValueSatisfying(todo -> {
			assertThat(todo.getTask()).isEqualTo("更新後のタスク");
			assertThat(todo.getRegistedDate()).isEqualTo(LocalDate.of(2026, 2, 1));
			assertThat(todo.getLimitDate()).isEqualTo(LocalDate.of(2026, 2, 28));
			assertThat(todo.getMemo()).isEqualTo("更新後の備考");
			assertThat(todo.getDoneFlag()).isTrue();
			assertThat(todo.getCreateDate()).isEqualTo(createDate);
			assertThat(todo.getUpdateDate()).isEqualTo(LocalDate.now());
		});
		assertThat(updated.getPKey()).isEqualTo(created.getPKey());
	}

	@Test
	void update_returnsEmptyWhenPrimaryKeyDoesNotExist() {
		assertThat(todoUpdateService.update(999, new TodoRegistRequest("タスク", null, null, null)))
				.isEmpty();
	}

}
