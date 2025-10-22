package com.company.cbf.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.company.cbf.common.model.CommonResponse;
import com.company.cbf.demo.entity.User;
import com.company.cbf.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户控制器
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    public CommonResponse<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return CommonResponse.success("创建用户成功", createdUser);
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户", description = "根据ID获取用户信息")
    public CommonResponse<User> getUserById(
            @Parameter(description = "用户ID") @PathVariable @NotNull Long id) {
        User user = userService.getUserById(id);
        return CommonResponse.success(user);
    }

    /**
     * 分页查询用户
     */
    @GetMapping
    @Operation(summary = "分页查询用户", description = "分页查询用户列表")
    public CommonResponse<IPage<User>> getUserPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        IPage<User> page = userService.getUserPage(pageNum, pageSize, keyword);
        return CommonResponse.success(page);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新用户信息")
    public CommonResponse<User> updateUser(
            @Parameter(description = "用户ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody User user) {
        user.setId(id);
        User updatedUser = userService.updateUser(user);
        return CommonResponse.success("更新用户成功", updatedUser);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除用户")
    public CommonResponse<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable @NotNull Long id) {
        userService.deleteUser(id);
        return CommonResponse.success("删除用户成功");
    }

    /**
     * 获取所有用户
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有用户", description = "获取所有用户列表")
    public CommonResponse<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return CommonResponse.success(users);
    }

    /**
     * 根据用户名获取用户
     */
    @GetMapping("/username/{username}")
    @Operation(summary = "根据用户名获取用户", description = "根据用户名获取用户信息")
    public CommonResponse<User> getUserByUsername(
            @Parameter(description = "用户名") @PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return CommonResponse.error("用户不存在");
        }
        return CommonResponse.success(user);
    }
}
