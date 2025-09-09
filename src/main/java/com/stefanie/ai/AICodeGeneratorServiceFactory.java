package com.stefanie.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AICodeGeneratorServiceFactory {
    @Resource
    private ChatModel chatModel;
    @Resource
    private StreamingChatModel streamingChatModel;
    @Bean
    /**
     * 创建并配置AICodeGeneratorService实例的工厂方法
     * @return 配置完成的AICodeGeneratorService实例
     */
    public AICodeGeneratorService aiCodeGeneratorService(){
        return AiServices.builder(AICodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .build();
    }

}
