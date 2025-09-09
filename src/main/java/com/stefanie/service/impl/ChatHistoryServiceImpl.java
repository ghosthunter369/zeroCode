package com.stefanie.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stefanie.constant.UserConstant;
import com.stefanie.exception.ErrorCode;
import com.stefanie.exception.ThrowUtils;
import com.stefanie.mapper.ChatHistoryMapper;
import com.stefanie.model.dto.chathistory.ChatHistoryQueryRequest;
import com.stefanie.model.entity.App;
import com.stefanie.model.entity.ChatHistory;
import com.stefanie.model.entity.User;
import com.stefanie.model.enums.ChatHistoryMessageTypeEnum;
import com.stefanie.service.AppService;
import com.stefanie.service.ChatHistoryService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 服务层实现。
 *
 */
@Service
@Slf4j
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    @Resource
    @Lazy
    private AppService appService;

    @Override
    public boolean addChatMessage(Long appId, String message, String messageType, Long userId) {
        // 基础校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "消息内容不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(messageType), ErrorCode.PARAMS_ERROR, "消息类型不能为空");
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        // 验证消息类型是否有效
        ChatHistoryMessageTypeEnum messageTypeEnum = ChatHistoryMessageTypeEnum.getEnumByValue(messageType);
        ThrowUtils.throwIf(messageTypeEnum == null, ErrorCode.PARAMS_ERROR, "不支持的消息类型");
        // 插入数据库
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setAppId(appId);
        chatHistory.setMessage(message);
        chatHistory.setMessageType(messageType);
        chatHistory.setUserId(userId);
        return this.save(chatHistory);
    }

    @Override
    public boolean deleteByAppId(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq( "appId", appId);
        return this.remove(queryWrapper);
    }

    @Override
    public Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                                      LocalDateTime lastCreateTime,
                                                      User loginUser) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        ThrowUtils.throwIf(pageSize <= 0 || pageSize > 50, ErrorCode.PARAMS_ERROR, "页面大小必须在1-50之间");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 验证权限：只有应用创建者和管理员可以查看
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        boolean isAdmin = UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole());
        boolean isCreator = app.getUserId().equals(loginUser.getId());
        ThrowUtils.throwIf(!isAdmin && !isCreator, ErrorCode.NO_AUTH_ERROR, "无权查看该应用的对话历史");
        // 构建查询条件
        ChatHistoryQueryRequest queryRequest = new ChatHistoryQueryRequest();
        queryRequest.setAppId(appId);
        queryRequest.setLastCreateTime(lastCreateTime);
        QueryWrapper<ChatHistory> queryWrapper = this.getQueryWrapper(queryRequest);
        // 查询数据
        return this.page(Page.of(1, pageSize), queryWrapper);
    }

    @Override
    public int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount) {
        try {
            QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(appId != null, "appId", appId)
                    .orderByDesc("createTime")
                    .last("limit 1," + maxCount);
            List<ChatHistory> historyList = this.list(queryWrapper);
            if (CollUtil.isEmpty(historyList)) {
                return 0;
            }
            // 反转列表，确保按照时间正序（老的在前，新的在后）
            historyList = historyList.reversed();
            // 按照时间顺序将消息添加到记忆中
            int loadedCount = 0;
            // 先清理历史缓存，防止重复加载
            chatMemory.clear();
            for (ChatHistory history : historyList) {
                if (ChatHistoryMessageTypeEnum.USER.getValue().equals(history.getMessageType())) {
                    chatMemory.add(UserMessage.from(history.getMessage()));
                } else if (ChatHistoryMessageTypeEnum.AI.getValue().equals(history.getMessageType())) {
                    chatMemory.add(AiMessage.from(history.getMessage()));
                }
                loadedCount++;
            }
            log.info("成功为 appId: {} 加载 {} 条历史消息", appId, loadedCount);
            return loadedCount;
        } catch (Exception e) {
            log.error("加载历史对话失败，appId: {}, error: {}", appId, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 获取查询包装类
     *
     * @param chatHistoryQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<ChatHistory> getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest) {
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        if (chatHistoryQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chatHistoryQueryRequest.getId();
        String message = chatHistoryQueryRequest.getMessage();
        String messageType = chatHistoryQueryRequest.getMessageType();
        Long appId = chatHistoryQueryRequest.getAppId();
        Long userId = chatHistoryQueryRequest.getUserId();
        LocalDateTime lastCreateTime = chatHistoryQueryRequest.getLastCreateTime();
        String sortField = chatHistoryQueryRequest.getSortField();
        String sortOrder = chatHistoryQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq(id != null, "id", id)
                .like(StringUtils.isNotBlank(message), "message", message)
                .eq(StringUtils.isNotBlank(messageType), "messageType", messageType)
                .eq(appId != null, "appId", appId)
                .eq(userId != null, "userId", userId);
        // 游标查询逻辑 - 只使用 createTime 作为游标
        if (lastCreateTime != null) {
            queryWrapper.lt("createTime", lastCreateTime);
        }
        // 排序
        if (StrUtil.isNotBlank(sortField)) {
            if ("ascend".equals(sortOrder)) {
                queryWrapper.orderByAsc(sortField);
            } else {
                queryWrapper.orderByDesc(sortField);
            }
        } else {
            // 默认按创建时间降序排列
            queryWrapper.orderByDesc("createTime");
        }
        return queryWrapper;
    }
}
