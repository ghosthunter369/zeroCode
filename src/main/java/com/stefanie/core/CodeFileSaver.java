package com.stefanie.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.stefanie.ai.model.HtmlCodeResult;
import com.stefanie.ai.model.MultiFileCodeResult;
import com.stefanie.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 在tmp目录保存文件
 */
@Deprecated
public class CodeFileSaver {
    public static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 构建唯一目录路径:tmp/code_output/bizType_雪花ID
     * @param bizType
     * @return
     */
    public static String buildUniqueDir(String bizType){
        String uniqueDirName = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }
    public static void writeToFile(String dirPath,String fileName,String content){
            String filePath = dirPath + File.separator + fileName;
            FileUtil.writeString(content,filePath, StandardCharsets.UTF_8);
    }

    /**
     * 保存HtmlCodeResult到文件
     * @param htmlCodeResult
     * @return
     */
    public static File saveHTMLCodeResult(HtmlCodeResult htmlCodeResult){
        String dirPath = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        writeToFile(dirPath,"index.html",htmlCodeResult.getHtmlCode());
        return new File(dirPath);
    }
    /**
     * 保存MultiFileCodeResult到文件
     * @param multiFileCodeResult
     * @return
     */
    public static File saveMultiFileCodeResult(MultiFileCodeResult multiFileCodeResult){
        String dirPath = buildUniqueDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        writeToFile(dirPath,"index.html",multiFileCodeResult.getHtmlCode());
        writeToFile(dirPath,"style.css",multiFileCodeResult.getCssCode());
        writeToFile(dirPath,"script.js",multiFileCodeResult.getJsCode());
        return new File(dirPath);
    }
}
