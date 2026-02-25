package org.jeecg.modules.campusqa.subscribe.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.campusqa.common.entity.CampusQaEntity;

/**
 * Subscriptions.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("campus_qa_subscribe")
@Schema(description = "Campus QA Subscribe")
public class QaSubscribe extends CampusQaEntity {
    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Department Code")
    private String deptCode;

    @Schema(description = "Category ID")
    private String categoryId;
}
