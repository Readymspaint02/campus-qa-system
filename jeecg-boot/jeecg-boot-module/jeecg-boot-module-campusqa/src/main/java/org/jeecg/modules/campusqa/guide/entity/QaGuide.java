package org.jeecg.modules.campusqa.guide.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.campusqa.common.entity.CampusQaEntity;

/**
 * Service guide item.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("campus_qa_guide")
@Schema(description = "Campus QA Guide")
public class QaGuide extends CampusQaEntity {
    @Schema(description = "Category ID")
    private String categoryId;

    @Schema(description = "Title")
    private String title;

    @Schema(description = "Content")
    private String content;

    @Schema(description = "Steps (JSON/Text)")
    private String steps;

    @Schema(description = "Contact")
    private String contact;

    @Schema(description = "Location")
    private String location;

    @Schema(description = "Status: enable/disable")
    private String status;
}
