package org.jeecg.modules.campusqa.knowledge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.campusqa.common.entity.CampusQaEntity;

/**
 * Knowledge base item.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("campus_qa_knowledge")
@Schema(description = "Campus QA Knowledge")
public class QaKnowledge extends CampusQaEntity {
    @Schema(description = "Category ID")
    private String categoryId;

    @Schema(description = "Question")
    private String question;

    @Schema(description = "Answer")
    private String answer;

    @Schema(description = "Keywords")
    private String keywords;

    @Schema(description = "Tags")
    private String tags;

    @Schema(description = "Hot Flag: 1/0")
    private Integer hotFlag;

    @Schema(description = "Hits")
    private Integer hits;

    @Schema(description = "Status: enable/disable")
    private String status;

    @Schema(description = "Source")
    private String source;
}
