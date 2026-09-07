package com.ai_driven.back_end.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TodoRegistResponse(
		@JsonProperty("code") int code,
		@JsonProperty("task") String task,
		@JsonProperty("registed_date") String registedDate,
		@JsonProperty("limit_date") String limitDate,
		@JsonProperty("memo") String memo,
		@JsonProperty("messages") List<String> messages) {

	public static TodoRegistResponse success(String task, String registedDate, String limitDate, String memo) {
		return new TodoRegistResponse(200, task, registedDate, limitDate, memo, null);
	}

	public static TodoRegistResponse error(int code, List<String> messages) {
		return new TodoRegistResponse(code, null, null, null, null, messages);
	}

}
