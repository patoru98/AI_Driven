package com.ai_driven.back_end.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai_driven.back_end.dto.TodoRegistRequest;
import com.ai_driven.back_end.dto.TodoRegistResponse;
import com.ai_driven.back_end.entity.TodoEntity;
import com.ai_driven.back_end.service.TodoRegistService;
import com.ai_driven.back_end.validation.TodoRegistValidator;

import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/todo")
public class TodoController {

	/** 入力チェックエラーを表す業務コード。 */
	private static final int CODE_VALIDATION_ERROR = 300;

	/** DB関連エラーを表す業務コード。 */
	private static final int CODE_DATABASE_ERROR = 301;

	private final TodoRegistValidator validator;
	private final TodoRegistService todoRegistService;

	public TodoController(TodoRegistValidator validator, TodoRegistService todoRegistService) {
		this.validator = validator;
		this.todoRegistService = todoRegistService;
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

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<TodoRegistResponse> handleUnreadableBody(HttpMessageNotReadableException e) {
		return ResponseEntity.badRequest()
				.body(TodoRegistResponse.error(CODE_VALIDATION_ERROR, List.of("リクエストボディの形式が不正です。")));
	}

	private String format(LocalDate date) {
		return date == null ? null : date.format(TodoRegistValidator.DATE_FORMATTER);
	}

}
