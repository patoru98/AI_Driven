package com.ai_driven.back_end.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TodoUpdateResponse(
		@JsonProperty("code") int code,
		@JsonProperty("p_key") Integer pKey,
		@JsonProperty("task") String task,
		@JsonProperty("registed_date") String registedDate,
		@JsonProperty("limit_date") String limitDate,
		@JsonProperty("memo") String memo,
		@JsonProperty("messages") List<String> messages) {

	public static TodoUpdateResponse success(
			Integer pKey, String task, String registedDate, String limitDate, String memo) {
		return new TodoUpdateResponse(200, pKey, task, registedDate, limitDate, memo, null);
	}

	public static TodoUpdateResponse error(int code, List<String> messages) {
		return new TodoUpdateResponse(code, null, null, null, null, null, messages);
	}

}
