package org.jeecg.modules.campusqa.rag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RagAnswerResponse {
    @Schema(description = "Answer")
    private String answer;

    @Schema(description = "Source ID")
    private String sourceId;

    @Schema(description = "Source Question")
    private String sourceQuestion;

    @Schema(description = "Mode")
    private String mode;

    @Schema(description = "Confidence")
    private Double confidence;

    @Schema(description = "Intent Type")
    private String intentType;

    @Schema(description = "Intent Label")
    private String intentLabel;

    @Schema(description = "Intent Score")
    private Double intentScore;

    @Schema(description = "Matched Keywords")
    private String matchedKeywords;
}
