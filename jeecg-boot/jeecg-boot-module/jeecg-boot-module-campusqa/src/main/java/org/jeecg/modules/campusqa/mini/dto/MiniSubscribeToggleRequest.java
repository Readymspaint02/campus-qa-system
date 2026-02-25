package org.jeecg.modules.campusqa.mini.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MiniSubscribeToggleRequest {
    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Department Code")
    private String deptCode;

    @Schema(description = "Category ID")
    private String categoryId;
}
