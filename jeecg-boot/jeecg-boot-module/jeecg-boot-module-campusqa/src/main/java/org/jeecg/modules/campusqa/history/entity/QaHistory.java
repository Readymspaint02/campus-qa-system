package org.jeecg.modules.campusqa.history.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.campusqa.common.entity.CampusQaEntity;

/**
 * Query history.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("campus_qa_history")
@Schema(description = "Campus QA History")
public class QaHistory extends CampusQaEntity {
    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Query")
    private String query;

    @Schema(description = "Knowledge ID")
    private String knowledgeId;

    @Schema(description = "Source")
    private String source;
}
