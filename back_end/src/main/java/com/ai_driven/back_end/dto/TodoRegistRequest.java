package com.ai_driven.back_end.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TodoRegistRequest(
		@JsonProperty("task") String task,
		@JsonProperty("registed_date") String registedDate,
		@JsonProperty("limit_date") String limitDate,
		@JsonProperty("memo") String memo) {
}
