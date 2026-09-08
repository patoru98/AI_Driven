package com.ai_driven.back_end.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ai_driven.back_end.dto.TodoRegistRequest;

import tools.jackson.databind.JsonNode;

/**
 * Todo Regist APIのインプットパラメータに対する入力チェックを行う。
 */
@Component
public class TodoRegistValidator {

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu/MM/dd")
			.withResolverStyle(ResolverStyle.STRICT);

	private static final int TASK_MAX_LENGTH = 100;
	private static final int MEMO_MAX_LENGTH = 500;

	public List<String> validate(JsonNode body) {
		List<String> messages = new ArrayList<>();

		validateString(body, "task", TASK_MAX_LENGTH, true, messages);
		validateString(body, "memo", MEMO_MAX_LENGTH, false, messages);
		validateDate(body, "registed_date", messages);
		validateDate(body, "limit_date", messages);

		return messages;
	}

	public TodoRegistRequest toRequest(JsonNode body) {
		return new TodoRegistRequest(
				text(body, "task"),
				text(body, "registed_date"),
				text(body, "limit_date"),
				text(body, "memo"));
	}

	private void validateString(JsonNode body, String key, int maxLength, boolean required, List<String> messages) {
		JsonNode node = body == null ? null : body.get(key);
		if (node == null || node.isNull()) {
			if (required) {
				messages.add(key + "は必須です。");
			}
			return;
		}
		if (!node.isString()) {
			messages.add(key + "は文字列で指定してください。");
			return;
		}
		String value = node.stringValue();
		if (required && value.isEmpty()) {
			messages.add(key + "は必須です。");
			return;
		}
		if (value.length() > maxLength) {
			messages.add(key + "は" + maxLength + "文字以内で指定してください。");
		}
	}

	private void validateDate(JsonNode body, String key, List<String> messages) {
		JsonNode node = body == null ? null : body.get(key);
		if (node == null || node.isNull()) {
			return;
		}
		if (!node.isString()) {
			messages.add(key + "は文字列で指定してください。");
			return;
		}
		String value = node.stringValue();
		if (value.isEmpty()) {
			return;
		}
		try {
			LocalDate.parse(value, DATE_FORMATTER);
		} catch (DateTimeParseException e) {
			messages.add(key + "はYYYY/MM/DD形式で指定してください。");
		}
	}

	private String text(JsonNode body, String key) {
		JsonNode node = body == null ? null : body.get(key);
		if (node == null || !node.isString() || node.stringValue().isEmpty()) {
			return null;
		}
		return node.stringValue();
	}

}
