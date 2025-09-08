package com.stefanie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stefanie.model.entity.User;
import org.apache.ibatis.annotations.Mapper;


/**
 * 用户 映射层。
 *
 *
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
