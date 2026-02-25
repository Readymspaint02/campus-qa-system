package org.jeecg.modules.campusqa.category.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.campusqa.common.entity.CampusQaEntity;

/**
 * Category for QA/Notice/Guide.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("campus_qa_category")
@Schema(description = "Campus QA Category")
public class QaCategory extends CampusQaEntity {
    @Schema(description = "Name")
    private String name;

    @Schema(description = "Parent ID")
    private String parentId;

    @Schema(description = "Type: qa/notice/guide")
    private String type;

    @Schema(description = "Sort")
    private Integer sort;

    @Schema(description = "Status: enable/disable")
    private String status;

    @Schema(description = "Remark")
    private String remark;
}
