package org.jeecg.modules.campusqa.feedback.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.campusqa.common.entity.CampusQaEntity;

/**
 * Feedback from users.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("campus_qa_feedback")
@Schema(description = "Campus QA Feedback")
public class QaFeedback extends CampusQaEntity {
    @Schema(description = "Knowledge ID")
    private String knowledgeId;

    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Rating: like/dislike")
    private String rating;

    @Schema(description = "Content")
    private String content;

    @Schema(description = "Reply")
    private String reply;

    @Schema(description = "Handled: 1/0")
    private Integer handled;
}
