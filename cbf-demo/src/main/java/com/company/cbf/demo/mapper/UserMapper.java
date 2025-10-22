package com.company.cbf.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.cbf.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
