package com.stefanie.ai;


import com.stefanie.ai.model.HtmlCodeResult;
import com.stefanie.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.SystemMessage;
import reactor.core.publisher.Flux;

public interface AICodeGeneratorService {
    /**
     * 生成HTML代码
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generateHTMLCode(String userMessage);
    /**
     * 生成多文件代码
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiFileCodeResult generateMultiFileCode(String userMessage);
    /**
     * 生成HTML代码
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    Flux<String> generateHTMLCodeStream(String userMessage);
    /**
     * 生成多文件代码
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    Flux<String> generateMultiFileCodeStream(String userMessage);
}
