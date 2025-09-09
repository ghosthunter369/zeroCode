package com.stefanie.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.stefanie.service.ChatHistoryService;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class AICodeGeneratorServiceFactory {
    @Resource
    @Qualifier("qwenChatModel")
    private ChatModel chatModel;
    @Resource
    @Qualifier("qwenStreamingChatModel")
    private StreamingChatModel streamingChatModel;
    @Resource
    private QwenStreamingChatModel qwenStreamingChatModel;
    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;
    @Resource
    private ChatHistoryService chatHistoryService;

    /**
     * AI 服务实例缓存
     * 缓存策略：
     * - 最大缓存 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<Long, AICodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，appId: {}, 原因: {}", key, cause);
            })
            .build();
    /**
     * 根据 appId 获取服务
     *
     * @param appId
     * @return
     */
    public AICodeGeneratorService getAiCodeGeneratorService(long appId) {
        return serviceCache.get(appId, this::createAICodeGeneratorService);
    }
    /**
     * 创建新的 AI 服务实例
     *
     * @param appId
     * @return
     */
    private AICodeGeneratorService createAICodeGeneratorService(long appId) {
        log.info("为 appId: {} 创建新的 AI 服务实例", appId);
        // 根据 appId 构建独立的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        // 从数据库中加载对话历史到记忆中
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);
        return AiServices.builder(AICodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemory(chatMemory)
                .build();
    }

    /**
     * 创建 AI 代码生成器服务
     *
     * @return
     */
    @Bean
    public AICodeGeneratorService aiCodeGeneratorService() {
        return getAiCodeGeneratorService(0);
    }

}
