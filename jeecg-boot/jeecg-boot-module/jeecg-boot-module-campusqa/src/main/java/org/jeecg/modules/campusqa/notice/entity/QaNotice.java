package org.jeecg.modules.campusqa.notice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.campusqa.common.entity.CampusQaEntity;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Notice item.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("campus_qa_notice")
@Schema(description = "Campus QA Notice")
public class QaNotice extends CampusQaEntity {
    @Schema(description = "Title")
    private String title;

    @Schema(description = "Content")
    private String content;

    @Schema(description = "Department Code")
    private String deptCode;

    @Schema(description = "Level: H/M/L")
    private String level;

    @Schema(description = "Status: draft/published/expired")
    private String status;

    @Schema(description = "Publish Time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date publishTime;

    @Schema(description = "Expire Time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date expireTime;

    @Schema(description = "Attachments (JSON/Text)")
    private String attachments;
}
