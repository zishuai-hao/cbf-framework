package com.company.cbf.starter.cache.service;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁服务
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class DistributedLockService {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 获取锁
     */
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 尝试获取锁
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁被中断, lockKey: {}", lockKey, e);
            return false;
        }
    }

    /**
     * 尝试获取锁（默认参数）
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, 10, 30, TimeUnit.SECONDS);
    }

    /**
     * 释放锁
     */
    public void unlock(String lockKey) {
        RLock lock = getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 释放锁
     */
    public void unlock(RLock lock) {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 执行带锁的操作
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
        return executeWithLock(lockKey, 10, 30, TimeUnit.SECONDS, supplier);
    }

    /**
     * 执行带锁的操作
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> supplier) {
        RLock lock = getLock(lockKey);
        try {
            if (lock.tryLock(waitTime, leaseTime, unit)) {
                try {
                    return supplier.get();
                } finally {
                    unlock(lock);
                }
            } else {
                throw new RuntimeException("获取锁失败: " + lockKey);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取锁被中断: " + lockKey, e);
        }
    }

    /**
     * 执行带锁的操作（无返回值）
     */
    public void executeWithLock(String lockKey, Runnable runnable) {
        executeWithLock(lockKey, () -> {
            runnable.run();
            return null;
        });
    }

    /**
     * 执行带锁的操作（无返回值）
     */
    public void executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Runnable runnable) {
        executeWithLock(lockKey, waitTime, leaseTime, unit, () -> {
            runnable.run();
            return null;
        });
    }
}
