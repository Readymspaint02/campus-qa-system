package org.jeecg.modules.campusqa.rag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RagAskRequest {
    @Schema(description = "Question")
    private String question;

    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "TopK override")
    private Integer topK;
}
