package org.jeecg.modules.campusqa.favorite.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.campusqa.common.entity.CampusQaEntity;

/**
 * Favorites.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("campus_qa_favorite")
@Schema(description = "Campus QA Favorite")
public class QaFavorite extends CampusQaEntity {
    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Knowledge ID")
    private String knowledgeId;
}
