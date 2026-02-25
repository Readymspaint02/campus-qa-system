package org.jeecg.modules.campusqa.mini.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MiniFeedbackRequest {
    @Schema(description = "Knowledge ID")
    private String knowledgeId;

    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Rating: like/dislike")
    private String rating;

    @Schema(description = "Content")
    private String content;
}
