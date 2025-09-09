package com.stefanie.ai;

import com.stefanie.ai.model.HtmlCodeResult;
import com.stefanie.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AICodeGeneratorServiceTest {
    @Resource
    AICodeGeneratorService aiCodeGeneratorService;;

    @Test
    void generateHTMLCode() {
        HtmlCodeResult s = aiCodeGeneratorService.generateHTMLCode("生成数据看板页面，不超过20行");
        System.out.println(s);
    }

    @Test
    void generateMultiFileCode() {
        MultiFileCodeResult s = aiCodeGeneratorService.generateMultiFileCode("生成数据看板页面，不超过20行");
        System.out.println(s);
    }
}