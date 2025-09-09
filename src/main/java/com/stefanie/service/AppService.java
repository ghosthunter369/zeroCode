package com.stefanie.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stefanie.model.dto.app.AppQueryRequest;
import com.stefanie.model.entity.App;
import com.stefanie.model.entity.User;
import com.stefanie.model.vo.AppVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 */
public interface AppService extends IService<App> {

    /**
     * 获取应用封装类
     *
     * @param app
     * @return
     */
    AppVO getAppVO(App app);

    /**
     * 获取应用封装类列表
     *
     * @param appList
     * @return
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 构造应用查询条件
     *
     * @param appQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    String deployApp(Long appId, User loginUser);
}
