package com.ai_driven.back_end.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai_driven.back_end.dto.TodoRegistRequest;
import com.ai_driven.back_end.dto.TodoRegistResponse;
import com.ai_driven.back_end.dto.TodoGetResponse;
import com.ai_driven.back_end.dto.TodoDeleteResponse;
import com.ai_driven.back_end.dto.TodoUpdateResponse;
import com.ai_driven.back_end.dto.TodoUpdateRequest;
import com.ai_driven.back_end.entity.TodoEntity;
import com.ai_driven.back_end.service.TodoDeleteService;
import com.ai_driven.back_end.service.TodoGetService;
import com.ai_driven.back_end.service.TodoRegistService;
import com.ai_driven.back_end.service.TodoUpdateService;
import com.ai_driven.back_end.validation.TodoRegistValidator;

import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/todo")
public class TodoController {

	/** 入力チェックエラーを表す業務コード。 */
	private static final int CODE_VALIDATION_ERROR = 300;

	/** DB関連エラーを表す業務コード。 */
	private static final int CODE_DATABASE_ERROR = 301;

	/** 対象のTODOが存在しないことを表す業務コード。 */
	private static final int CODE_NOT_FOUND = 302;

	private final TodoRegistValidator validator;
	private final TodoRegistService todoRegistService;
	private final TodoGetService todoGetService;
	private final TodoUpdateService todoUpdateService;
	private final TodoDeleteService todoDeleteService;

	public TodoController(TodoRegistValidator validator, TodoRegistService todoRegistService,
			TodoGetService todoGetService, TodoUpdateService todoUpdateService,
			TodoDeleteService todoDeleteService) {
		this.validator = validator;
		this.todoRegistService = todoRegistService;
		this.todoGetService = todoGetService;
		this.todoUpdateService = todoUpdateService;
		this.todoDeleteService = todoDeleteService;
	}

	@GetMapping("/getAll")
	public ResponseEntity<TodoGetResponse> getAll() {
		try {
			List<TodoGetResponse.TodoItem> todos = todoGetService.getAll().stream()
					.map(this::toTodoItem)
					.toList();
			return ResponseEntity.ok(TodoGetResponse.success(todos));
		} catch (DataAccessException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(TodoGetResponse.error(CODE_DATABASE_ERROR, List.of("TODOの取得に失敗しました。")));
		}
	}

	@PostMapping("/regist")
	public ResponseEntity<TodoRegistResponse> regist(@RequestBody JsonNode body) {
		List<String> messages = validator.validate(body);
		if (!messages.isEmpty()) {
			return ResponseEntity.badRequest().body(TodoRegistResponse.error(CODE_VALIDATION_ERROR, messages));
		}

		TodoRegistRequest request = validator.toRequest(body);

		TodoEntity saved;
		try {
			saved = todoRegistService.regist(request);
		} catch (DataAccessException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(TodoRegistResponse.error(CODE_DATABASE_ERROR, List.of("TODOの登録に失敗しました。")));
		}

		return ResponseEntity.ok(TodoRegistResponse.success(
				saved.getTask(),
				format(saved.getRegistedDate()),
				format(saved.getLimitDate()),
				saved.getMemo()));
	}

	@PostMapping("/update")
	public ResponseEntity<TodoUpdateResponse> update(@RequestBody JsonNode body) {
		List<String> messages = validator.validateUpdate(body);
		Integer pKey = primaryKey(body);
		if (pKey == null) {
			messages.add("p_keyは1以上の整数で指定してください。");
		}
		if (!messages.isEmpty()) {
			return ResponseEntity.badRequest().body(TodoUpdateResponse.error(CODE_VALIDATION_ERROR, messages));
		}

		TodoUpdateRequest request = validator.toUpdateRequest(body);
		Optional<TodoEntity> result;
		try {
			result = todoUpdateService.update(pKey, request);
		} catch (DataAccessException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(TodoUpdateResponse.error(CODE_DATABASE_ERROR, List.of("TODOの更新に失敗しました。")));
		}

		if (result.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(TodoUpdateResponse.error(CODE_NOT_FOUND, List.of("更新対象のTODOが見つかりません。")));
		}

		TodoEntity updated = result.get();
		return ResponseEntity.ok(TodoUpdateResponse.success(
				updated.getPKey(),
				updated.getTask(),
				format(updated.getRegistedDate()),
				format(updated.getLimitDate()),
				updated.getMemo()));
	}

	@PostMapping("/delete")
	public ResponseEntity<TodoDeleteResponse> delete(@RequestBody JsonNode body) {
		Integer pKey = primaryKey(body);
		if (pKey == null) {
			return ResponseEntity.badRequest().body(TodoDeleteResponse.error(
					CODE_VALIDATION_ERROR, List.of("p_keyは1以上の整数で指定してください。")));
		}

		boolean deleted;
		try {
			deleted = todoDeleteService.delete(pKey);
		} catch (DataAccessException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(TodoDeleteResponse.error(CODE_DATABASE_ERROR, List.of("TODOの削除に失敗しました。")));
		}

		if (!deleted) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(TodoDeleteResponse.error(CODE_NOT_FOUND, List.of("削除対象のTODOが見つかりません。")));
		}

		return ResponseEntity.ok(TodoDeleteResponse.success());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<TodoRegistResponse> handleUnreadableBody(HttpMessageNotReadableException e) {
		return ResponseEntity.badRequest()
				.body(TodoRegistResponse.error(CODE_VALIDATION_ERROR, List.of("リクエストボディの形式が不正です。")));
	}

	private Integer primaryKey(JsonNode body) {
		JsonNode node = body == null ? null : body.get("p_key");
		if (node == null || !node.isIntegralNumber() || !node.canConvertToInt() || node.intValue() < 1) {
			return null;
		}
		return node.intValue();
	}

	private String format(LocalDate date) {
		return date == null ? null : date.format(TodoRegistValidator.DATE_FORMATTER);
	}

	private TodoGetResponse.TodoItem toTodoItem(TodoEntity todo) {
		return new TodoGetResponse.TodoItem(
				todo.getPKey(),
				todo.getDoneFlag(),
				todo.getTask(),
				format(todo.getRegistedDate()),
				format(todo.getLimitDate()),
				todo.getMemo());
	}

}
