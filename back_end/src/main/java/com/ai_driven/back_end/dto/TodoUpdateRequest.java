package com.ai_driven.back_end.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TodoUpdateRequest(
		@JsonProperty("task") String task,
		@JsonProperty("registed_date") String registedDate,
		@JsonProperty("limit_date") String limitDate,
		@JsonProperty("memo") String memo,
		@JsonProperty("done_flag") Boolean doneFlag) {
}
