package com.ai_driven.back_end.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TodoGetResponse(
		@JsonProperty("code") int code,
		@JsonProperty("todos") List<TodoItem> todos,
		@JsonProperty("messages") List<String> messages) {

	public static TodoGetResponse success(List<TodoItem> todos) {
		return new TodoGetResponse(200, todos, null);
	}

	public static TodoGetResponse error(int code, List<String> messages) {
		return new TodoGetResponse(code, null, messages);
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record TodoItem(
			@JsonProperty("p_key") Integer pKey,
			@JsonProperty("done_flag") Boolean doneFlag,
			@JsonProperty("task") String task,
			@JsonProperty("registed_date") String registedDate,
			@JsonProperty("limit_date") String limitDate,
			@JsonProperty("memo") String memo) {
	}

}