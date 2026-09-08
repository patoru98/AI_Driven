package com.ai_driven.back_end.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ai_driven.back_end.entity.TodoEntity;
import com.ai_driven.back_end.service.TodoRegistService;
import com.ai_driven.back_end.validation.TodoRegistValidator;

@WebMvcTest(TodoController.class)
@Import(TodoRegistValidator.class)
class TodoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TodoRegistService todoRegistService;

	@Test
	void regist_returns200WithRegistedTodo() throws Exception {
		given(todoRegistService.regist(any())).willReturn(
				entity("買い物へ行く", LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 31), "牛乳を買う"));

		mockMvc.perform(post("/api/todo/regist")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "task": "買い物へ行く",
						  "registed_date": "2026/01/05",
						  "limit_date": "2026/01/31",
						  "memo": "牛乳を買う"
						}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.task").value("買い物へ行く"))
				.andExpect(jsonPath("$.registed_date").value("2026/01/05"))
				.andExpect(jsonPath("$.limit_date").value("2026/01/31"))
				.andExpect(jsonPath("$.memo").value("牛乳を買う"));
	}

	@Test
	void regist_returns200WhenOptionalItemsAreOmitted() throws Exception {
		given(todoRegistService.regist(any())).willReturn(entity("買い物へ行く", null, null, null));

		mockMvc.perform(post("/api/todo/regist")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"task\": \"買い物へ行く\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.task").value("買い物へ行く"))
				.andExpect(jsonPath("$.registed_date").doesNotExist())
				.andExpect(jsonPath("$.limit_date").doesNotExist())
				.andExpect(jsonPath("$.memo").doesNotExist());
	}

	@Test
	void regist_returns300CodeWhenTaskIsMissing() throws Exception {
		mockMvc.perform(post("/api/todo/regist")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"memo\": \"備考\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(300));

		verify(todoRegistService, never()).regist(any());
	}

	@Test
	void regist_returns300CodeWhenTaskIsNotString() throws Exception {
		mockMvc.perform(post("/api/todo/regist")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"task\": 123}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(300))
				.andExpect(jsonPath("$.messages[0]").value("taskは文字列で指定してください。"));
	}

	@Test
	void regist_returns300CodeWhenDateFormatIsInvalid() throws Exception {
		mockMvc.perform(post("/api/todo/regist")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"task\": \"買い物へ行く\", \"limit_date\": \"2026-01-31\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(300))
				.andExpect(jsonPath("$.messages[0]").value("limit_dateはYYYY/MM/DD形式で指定してください。"));
	}

	@Test
	void regist_returns300CodeWhenTaskExceedsMaxLength() throws Exception {
		mockMvc.perform(post("/api/todo/regist")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"task\": \"" + "あ".repeat(101) + "\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(300))
				.andExpect(jsonPath("$.messages[0]").value("taskは100文字以内で指定してください。"));
	}

	@Test
	void regist_returns300CodeWhenMemoExceedsMaxLength() throws Exception {
		mockMvc.perform(post("/api/todo/regist")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"task\": \"買い物へ行く\", \"memo\": \"" + "あ".repeat(501) + "\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(300))
				.andExpect(jsonPath("$.messages[0]").value("memoは500文字以内で指定してください。"));
	}

	@Test
	void regist_returns301CodeWhenDatabaseAccessFails() throws Exception {
		given(todoRegistService.regist(any()))
				.willThrow(new DataAccessResourceFailureException("DB接続エラー"));

		mockMvc.perform(post("/api/todo/regist")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"task\": \"買い物へ行く\"}"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value(301));
	}

	@Test
	void regist_returns300CodeWhenBodyIsNotJson() throws Exception {
		mockMvc.perform(post("/api/todo/regist")
				.contentType(MediaType.APPLICATION_JSON)
				.content("not json"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(300));
	}

	private TodoEntity entity(String task, LocalDate registedDate, LocalDate limitDate, String memo) {
		TodoEntity todo = new TodoEntity();
		todo.setPKey(1);
		todo.setTask(task);
		todo.setRegistedDate(registedDate);
		todo.setLimitDate(limitDate);
		todo.setMemo(memo);
		todo.setCreateDate(LocalDate.now());
		todo.setUpdateDate(LocalDate.now());
		return todo;
	}

}
