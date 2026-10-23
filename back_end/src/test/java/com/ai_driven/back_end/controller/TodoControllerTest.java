package com.ai_driven.back_end.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ai_driven.back_end.entity.TodoEntity;
import com.ai_driven.back_end.service.TodoGetService;
import com.ai_driven.back_end.service.TodoRegistService;
import com.ai_driven.back_end.service.TodoUpdateService;
import com.ai_driven.back_end.validation.TodoRegistValidator;

@WebMvcTest(TodoController.class)
@Import(TodoRegistValidator.class)
class TodoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TodoRegistService todoRegistService;

	@MockitoBean
	private TodoGetService todoGetService;

	@MockitoBean
	private TodoUpdateService todoUpdateService;

	@Test
	void getAll_returns200WithTodoList() throws Exception {
		TodoEntity first = entity("買い物へ行く", LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 31), "牛乳を買う");
		TodoEntity second = entity("書類を提出する", null, null, null);
		second.setPKey(2);
		second.setDoneFlag(true);
		given(todoGetService.getAll()).willReturn(List.of(first, second));

		mockMvc.perform(get("/api/todo/getAll"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.todos[0].p_key").value(1))
				.andExpect(jsonPath("$.todos[0].done_flag").value(false))
				.andExpect(jsonPath("$.todos[0].task").value("買い物へ行く"))
				.andExpect(jsonPath("$.todos[0].registed_date").value("2026/01/05"))
				.andExpect(jsonPath("$.todos[0].limit_date").value("2026/01/31"))
				.andExpect(jsonPath("$.todos[1].p_key").value(2))
				.andExpect(jsonPath("$.todos[1].done_flag").value(true))
				.andExpect(jsonPath("$.todos[1].task").value("書類を提出する"))
				.andExpect(jsonPath("$.todos[1].registed_date").doesNotExist())
				.andExpect(jsonPath("$.todos[1].memo").doesNotExist());
	}

	@Test
	void getAll_returns200WithEmptyArrayWhenNoTodosExist() throws Exception {
		given(todoGetService.getAll()).willReturn(List.of());

		mockMvc.perform(get("/api/todo/getAll"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.todos").isArray())
				.andExpect(jsonPath("$.todos").isEmpty());
	}

	@Test
	void getAll_returns301CodeWhenDatabaseAccessFails() throws Exception {
		given(todoGetService.getAll()).willThrow(new DataAccessResourceFailureException("DB接続エラー"));

		mockMvc.perform(get("/api/todo/getAll"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value(301))
				.andExpect(jsonPath("$.messages[0]").value("TODOの取得に失敗しました。"));
	}

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
	void regist_returns200WhenOptionalItemsAreNullOrEmpty() throws Exception {
		given(todoRegistService.regist(any())).willReturn(entity("買い物へ行く", null, null, null));

		mockMvc.perform(post("/api/todo/regist")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "task": "買い物へ行く",
						  "registed_date": "",
						  "limit_date": null,
						  "memo": ""
						}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
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
	void regist_returns300CodeWhenTaskIsEmpty() throws Exception {
		mockMvc.perform(post("/api/todo/regist")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"task\": \"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(300))
				.andExpect(jsonPath("$.messages[0]").value("taskは必須です。"));

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

	@Test
	void update_returns200WithUpdatedTodo() throws Exception {
		TodoEntity updated = entity("新しいタスク", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28), "更新後の備考");
		given(todoUpdateService.update(org.mockito.ArgumentMatchers.eq(1), any()))
				.willReturn(java.util.Optional.of(updated));

		mockMvc.perform(post("/api/todo/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "p_key": 1,
						  "task": "新しいタスク",
						  "registed_date": "2026/02/01",
						  "limit_date": "2026/02/28",
						  "memo": "更新後の備考"
						}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.p_key").value(1))
				.andExpect(jsonPath("$.task").value("新しいタスク"))
				.andExpect(jsonPath("$.registed_date").value("2026/02/01"))
				.andExpect(jsonPath("$.limit_date").value("2026/02/28"))
				.andExpect(jsonPath("$.memo").value("更新後の備考"));
	}

	@Test
	void update_returns300CodeWhenPrimaryKeyIsInvalid() throws Exception {
		mockMvc.perform(post("/api/todo/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"p_key\": \"1\", \"task\": \"更新タスク\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(300))
				.andExpect(jsonPath("$.messages[0]").value("p_keyは1以上の整数で指定してください。"));

		verify(todoUpdateService, never()).update(org.mockito.ArgumentMatchers.anyInt(), any());
	}

	@Test
	void update_returns404WhenTodoDoesNotExist() throws Exception {
		given(todoUpdateService.update(org.mockito.ArgumentMatchers.eq(99), any()))
				.willReturn(java.util.Optional.empty());

		mockMvc.perform(post("/api/todo/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"p_key\": 99, \"task\": \"更新タスク\"}"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value(302));
	}

	@Test
	void update_returns301CodeWhenDatabaseAccessFails() throws Exception {
		given(todoUpdateService.update(org.mockito.ArgumentMatchers.eq(1), any()))
				.willThrow(new DataAccessResourceFailureException("DB接続エラー"));

		mockMvc.perform(post("/api/todo/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"p_key\": 1, \"task\": \"更新タスク\"}"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value(301));
	}

	private TodoEntity entity(String task, LocalDate registedDate, LocalDate limitDate, String memo) {
		TodoEntity todo = new TodoEntity();
		todo.setPKey(1);
		todo.setTask(task);
		todo.setDoneFlag(false);
		todo.setRegistedDate(registedDate);
		todo.setLimitDate(limitDate);
		todo.setMemo(memo);
		todo.setCreateDate(LocalDate.now());
		todo.setUpdateDate(LocalDate.now());
		return todo;
	}

}
