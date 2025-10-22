package com.company.cbf.starter.cache.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存服务
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class RedisCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("设置缓存失败, key: {}", key, e);
        }
    }

    /**
     * 设置缓存（带过期时间）
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("设置缓存失败, key: {}, timeout: {}", key, timeout, e);
        }
    }

    /**
     * 设置缓存（带过期时间）
     */
    public void set(String key, Object value, Duration timeout) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout);
        } catch (Exception e) {
            log.error("设置缓存失败, key: {}, timeout: {}", key, timeout, e);
        }
    }

    /**
     * 获取缓存
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("获取缓存失败, key: {}", key, e);
            return null;
        }
    }

    /**
     * 获取缓存（指定类型）
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null && clazz.isAssignableFrom(value.getClass())) {
                return (T) value;
            }
            return null;
        } catch (Exception e) {
            log.error("获取缓存失败, key: {}, clazz: {}", key, clazz, e);
            return null;
        }
    }

    /**
     * 删除缓存
     */
    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("删除缓存失败, key: {}", key, e);
            return false;
        }
    }

    /**
     * 批量删除缓存
     */
    public long delete(Collection<String> keys) {
        try {
            Long count = redisTemplate.delete(keys);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("批量删除缓存失败, keys: {}", keys, e);
            return 0;
        }
    }

    /**
     * 判断缓存是否存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("判断缓存是否存在失败, key: {}", key, e);
            return false;
        }
    }

    /**
     * 设置过期时间
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            log.error("设置过期时间失败, key: {}, timeout: {}", key, timeout, e);
            return false;
        }
    }

    /**
     * 获取过期时间
     */
    public long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key);
            return expire != null ? expire : -1;
        } catch (Exception e) {
            log.error("获取过期时间失败, key: {}", key, e);
            return -1;
        }
    }

    /**
     * 根据模式获取所有key
     */
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("根据模式获取key失败, pattern: {}", pattern, e);
            return null;
        }
    }

    /**
     * 递增
     */
    public long increment(String key) {
        try {
            Long value = redisTemplate.opsForValue().increment(key);
            return value != null ? value : 0;
        } catch (Exception e) {
            log.error("递增失败, key: {}", key, e);
            return 0;
        }
    }

    /**
     * 递增（指定步长）
     */
    public long increment(String key, long delta) {
        try {
            Long value = redisTemplate.opsForValue().increment(key, delta);
            return value != null ? value : 0;
        } catch (Exception e) {
            log.error("递增失败, key: {}, delta: {}", key, delta, e);
            return 0;
        }
    }

    /**
     * 递减
     */
    public long decrement(String key) {
        try {
            Long value = redisTemplate.opsForValue().decrement(key);
            return value != null ? value : 0;
        } catch (Exception e) {
            log.error("递减失败, key: {}", key, e);
            return 0;
        }
    }

    /**
     * 递减（指定步长）
     */
    public long decrement(String key, long delta) {
        try {
            Long value = redisTemplate.opsForValue().decrement(key, delta);
            return value != null ? value : 0;
        } catch (Exception e) {
            log.error("递减失败, key: {}, delta: {}", key, delta, e);
            return 0;
        }
    }
}
