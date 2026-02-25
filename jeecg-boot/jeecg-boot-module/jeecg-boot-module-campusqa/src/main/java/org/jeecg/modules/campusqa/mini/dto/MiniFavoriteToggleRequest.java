package org.jeecg.modules.campusqa.mini.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MiniFavoriteToggleRequest {
    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Knowledge ID")
    private String knowledgeId;
}
