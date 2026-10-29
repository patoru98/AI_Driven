package com.ai_driven.back_end.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TodoDeleteResponse(
		@JsonProperty("code") int code,
		@JsonProperty("messages") List<String> messages) {

	public static TodoDeleteResponse success() {
		return new TodoDeleteResponse(200, null);
	}

	public static TodoDeleteResponse error(int code, List<String> messages) {
		return new TodoDeleteResponse(code, messages);
	}

}
