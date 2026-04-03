package com.com2us.mobility.core.redis;

import com.com2us.mobility.core.exception.ErrorCode;
import com.com2us.mobility.core.exception.CoreException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    // ─────────────────────────────────────────
    // String
    // ─────────────────────────────────────────

    public void set(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    public void set(String key, String value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    public Optional<String> get(String key) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(key));
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    public boolean setIfAbsent(String key, String value, Duration ttl) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, ttl));
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    // ─────────────────────────────────────────
    // List
    // ─────────────────────────────────────────

    public void leftPush(String key, String value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    public void rightPush(String key, String value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    public Optional<String> leftPop(String key) {
        try {
            return Optional.ofNullable(redisTemplate.opsForList().leftPop(key));
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    public Optional<String> rightPop(String key) {
        try {
            return Optional.ofNullable(redisTemplate.opsForList().rightPop(key));
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    public List<String> getList(String key) {
        try {
            return redisTemplate.opsForList().range(key, 0, -1);
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    // ─────────────────────────────────────────
    // Set
    // ─────────────────────────────────────────

    public void sAdd(String key, String... values) {
        try {
            redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    public void sRemove(String key, String value) {
        try {
            redisTemplate.opsForSet().remove(key, value);
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    public Set<String> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    public boolean sIsMember(String key, String value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    // ─────────────────────────────────────────
    // Hash
    // ─────────────────────────────────────────

    public void hSet(String key, String field, String value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    public Optional<String> hGet(String key, String field) {
        try {
            return Optional.ofNullable((String) redisTemplate.opsForHash().get(key, field));
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    public void hDelete(String key, String field) {
        try {
            redisTemplate.opsForHash().delete(key, field);
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }
}