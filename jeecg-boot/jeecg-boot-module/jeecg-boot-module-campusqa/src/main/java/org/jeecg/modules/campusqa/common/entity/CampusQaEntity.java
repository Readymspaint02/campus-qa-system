package org.jeecg.modules.campusqa.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * Base entity for Campus QA module.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CampusQaEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    private String id;

    @Schema(description = "Create By")
    private String createBy;

    @Schema(description = "Create Time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date createTime;

    @Schema(description = "Update By")
    private String updateBy;

    @Schema(description = "Update Time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;

    @Schema(description = "Org Code")
    private String sysOrgCode;

    @Schema(description = "Tenant ID")
    private String tenantId;

    @Schema(description = "Delete Flag")
    private String delFlag;
}
