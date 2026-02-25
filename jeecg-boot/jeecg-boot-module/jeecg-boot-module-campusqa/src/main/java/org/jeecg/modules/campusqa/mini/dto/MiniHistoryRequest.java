package org.jeecg.modules.campusqa.mini.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MiniHistoryRequest {
    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Query")
    private String query;

    @Schema(description = "Knowledge ID")
    private String knowledgeId;

    @Schema(description = "Source")
    private String source;
}
