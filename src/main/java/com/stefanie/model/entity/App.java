package com.stefanie.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 应用
 * @TableName app
 */
@TableName(value ="app")
@Data
public class App implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 应用名称
     */
    @TableField(value = "appName")
    private String appName;

    /**
     * 应用封面
     */
    @TableField(value = "cover")
    private String cover;

    /**
     * 应用初始化的 prompt
     */
    @TableField(value = "initPrompt")
    private String initPrompt;

    /**
     * 代码生成类型（枚举）
     */
    @TableField(value = "codeGenType")
    private String codeGenType;

    /**
     * 部署标识
     */
    @TableField(value = "deployKey")
    private String deployKey;

    /**
     * 部署时间
     */
    @TableField(value = "deployedTime")
    private LocalDateTime deployedTime;

    /**
     * 优先级
     */
    @TableField(value = "priority")
    private Integer priority;

    /**
     * 创建用户id
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 编辑时间
     */
    @TableField(value = "editTime")
    private LocalDateTime editTime;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableField(value = "isDelete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}