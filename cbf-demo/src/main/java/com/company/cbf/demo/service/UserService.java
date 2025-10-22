package com.company.cbf.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.cbf.common.exception.BusinessException;
import com.company.cbf.demo.entity.User;
import com.company.cbf.demo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务类
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    /**
     * 创建用户
     */
    @Transactional(rollbackFor = Exception.class)
    public User createUser(User user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        userMapper.insert(user);
        log.info("创建用户成功: {}", user.getUsername());
        return user;
    }

    /**
     * 根据ID获取用户
     */
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    /**
     * 根据用户名获取用户
     */
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 分页查询用户
     */
    public IPage<User> getUserPage(int pageNum, int pageSize, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                   .or()
                   .like(User::getNickname, keyword)
                   .or()
                   .like(User::getEmail, keyword);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(page, wrapper);
    }

    /**
     * 更新用户
     */
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 检查用户是否存在
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }

        userMapper.updateById(user);
        log.info("更新用户成功: {}", user.getUsername());
        return user;
    }

    /**
     * 删除用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        userMapper.deleteById(id);
        log.info("删除用户成功: {}", user.getUsername());
    }

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectList(wrapper);
    }
}
