package com.stefanie.model.dto.chathistory;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.stefanie.common.PageRequest;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话历史查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatHistoryQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 消息类型（user/ai）
     */
    private String messageType;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 游标查询 - 最后一条记录的创建时间
     * 用于分页查询，获取早于此时间的记录
     */
    private LocalDateTime lastCreateTime;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序
     */
    private String sortOrder;

    private static final long serialVersionUID = 1L;
}