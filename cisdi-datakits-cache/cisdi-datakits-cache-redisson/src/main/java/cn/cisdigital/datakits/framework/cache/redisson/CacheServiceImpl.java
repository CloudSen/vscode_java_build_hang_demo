package cn.cisdigital.datakits.framework.cache.redisson;

import cn.cisdigital.datakits.framework.cache.abs.CacheAutoConfiguration;
import cn.cisdigital.datakits.framework.cache.abs.CacheErrorCode;
import cn.cisdigital.datakits.framework.cache.abs.CacheException;
import cn.cisdigital.datakits.framework.cache.abs.CacheService;
import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.model.interfaces.Executable;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.RedisException;
import org.redisson.client.codec.StringCodec;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.ToLongFunction;

/**
 * 缓存实现
 *
 * @author xxx
 * @since 2022-09-23
 */
@Service
@Slf4j
@RequiredArgsConstructor
@AutoConfigureAfter(CacheAutoConfiguration.class)
public class CacheServiceImpl implements CacheService {

    private final RedissonClient redissonClient;

    /**
     * 防止缓存击穿的默认值，如果获取到该值，则给上层返回对应类型默认的值（如，null、空字符串、0等等）
     */
    public static final String NONE = "None";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        OBJECT_MAPPER.findAndRegisterModules();
        OBJECT_MAPPER.setDateFormat(dateFormat);
        OBJECT_MAPPER.configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void set(String key, String value) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(value);
        log.debug("[ Redisson ] 设置String：{}={}", key, value);
    }

    @Override
    public void set(String key, String value, Long timeToLive) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(value, timeToLive, TimeUnit.MILLISECONDS);
        log.debug("[ Redisson ] 设置String：{}={}", key, value);
    }


    @Override
    public boolean setIfAbsent(String key, String value) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        RBucket<String> bucket = redissonClient.getBucket(key);
        log.debug("[ Redisson ] 如果key不存在，设置String：{}={}",
            key, value);
        return bucket.trySet(value);
    }

    @Override
    public void setBatch(Map<String, String> mapData) {
        if (MapUtil.isEmpty(mapData)) {
            return;
        }
        if (mapData.keySet().stream().anyMatch(Objects::isNull)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        RBatch batch = redissonClient.createBatch();
        mapData.forEach((key, value) -> {
            RBucketAsync<String> bucket = batch.getBucket(key);
            bucket.setAsync(value);
        });
        batch.execute();
    }

    @Override
    public void setLong(String key,
                        Long value) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        if (value == null) {
            return;
        }
        RBucket<Long> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }

    @Override
    public <V extends Serializable> void setObject(
        String key, V value) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(value);
        try {
            log.debug("[ Redisson ] 设置Object：{}={}", key,
                OBJECT_MAPPER.writeValueAsString(value));
        } catch (Exception e) {
            log.error("序列化异常", e);
        }
    }

    @Override
    public <V extends Serializable> void replaceList(String key, List<V> value) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        if (CollectionUtils.isEmpty(value)) {
            return;
        }
        RList<Object> list = redissonClient.getList(key);
        list.delete();
        list.addAll(value);
    }

    @Override
    public <V extends Serializable> void addAllList(
        String key, List<V> value) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        if (CollectionUtils.isEmpty(value)) {
            return;
        }
        RList<Object> list = redissonClient.getList(key);
        list.addAll(value);
    }

    @Override
    public <V extends Serializable> void appendList(
        String key, V value) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        if (value == null) {
            return;
        }
        RList<Object> list = redissonClient.getList(key);
        list.add(value);
    }

    @Override
    public <V extends Serializable> void addAllSet(
        String key, Set<V> value) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        if (CollectionUtils.isEmpty(value)) {
            return;
        }
        RSet<Object> set = redissonClient.getSet(key);
        set.addAll(value);
    }

    @Override
    public <V extends Serializable> void addSet(String key, V value) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        if (value == null) {
            return;
        }
        RSet<Object> set = redissonClient.getSet(key);
        set.add(value);
    }

    @Override
    public <V extends Serializable> void putAllHash(
        String key, Map<String, V> value) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        if (MapUtil.isEmpty(value)) {
            return;
        }
        RMap<Object, Object> map = redissonClient.getMap(key);
        map.putAll(value);
    }

    @Override
    public <V extends Serializable> void putHash(String key, String hKey, V value) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        if (StrUtil.isEmpty(key)) {
            throw new CacheException("hash key不能为空");
        }
        if (value == null) {
            return;
        }
        RMap<Object, Object> map = redissonClient.getMap(key);
        map.put(hKey, value);
    }

    @Override
    public String get(String key) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        RBucket<String> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    @Override
    public String get(String key, Function<String, String> valueLoader) {
        if (valueLoader == null) {
            throw new CacheException(CacheErrorCode.NO_VALUE_LOADER);
        }
        String cacheValue = this.get(key);
        if (StrUtil.isBlank(cacheValue)) {
            String newValue = valueLoader.apply(key);
            this.set(key, StrUtil.isBlank(newValue) ? NONE : newValue);
            cacheValue = newValue;
        }
        return StrUtil.isBlank(cacheValue) ? StrUtil.EMPTY : cacheValue;
    }

    @Override
    public Long getLong(String key) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        RBucket<Long> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    @Override
    public Long getLong(String key,
                        ToLongFunction<String> valueLoader) {
        if (valueLoader == null) {
            throw new CacheException(CacheErrorCode.NO_VALUE_LOADER);
        }
        Long cacheLong = this.getLong(key);
        if (cacheLong == null) {
            Long newLong = valueLoader.applyAsLong(key);
            this.setLong(key, newLong);
            cacheLong = newLong;
        }
        return cacheLong;
    }

    @Override
    public <V extends Serializable> V getObject(
        String key, Class<V> clazz) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        RBucket<V> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    @Override
    public <V extends Serializable> V getObject(
        String key, Class<V> clazz, Function<String, V> valueLoader) {
        if (valueLoader == null) {
            throw new CacheException(CacheErrorCode.NO_VALUE_LOADER);
        }
        V cacheObj = this.getObject(key, clazz);
        if (cacheObj == null) {
            V newObj = valueLoader.apply(key);
            this.setObject(key, newObj);
            cacheObj = newObj;
        }
        return cacheObj;
    }

    @Override
    public <V extends Serializable> List<V> getList(
        String key, Class<V> clazz) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        return redissonClient.getList(key);
    }

    @Override
    public <V extends Serializable> List<V> getList(
        String key, Class<V> clazz, Function<String, List<V>> valueLoader) {
        if (valueLoader == null) {
            throw new CacheException(CacheErrorCode.NO_VALUE_LOADER);
        }
        List<V> cacheList = this.getList(key, clazz);
        if (CollectionUtils.isEmpty(cacheList)) {
            List<V> newList = valueLoader.apply(key);
            this.addAllList(key, newList);
            cacheList = newList;
        }
        return cacheList;
    }

    @Override
    public <V extends Serializable> Set<V> getSet(
        String key, Class<V> clazz) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        return redissonClient.getSet(key);
    }

    @Override
    public <V extends Serializable> Set<V> getSet(
        String key, Class<V> clazz, Function<String, Set<V>> valueLoader) {
        if (valueLoader == null) {
            throw new CacheException(CacheErrorCode.NO_VALUE_LOADER);
        }
        Set<V> cacheSet = this.getSet(key, clazz);
        if (CollectionUtils.isEmpty(cacheSet)) {
            Set<V> newSet = valueLoader.apply(key);
            this.addAllSet(key, newSet);
            cacheSet = newSet;
        }
        return cacheSet;
    }

    @Override
    public <V extends Serializable> Map<String, V> getMap(
        String key, Class<V> clazz) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        if (clazz == null) {
            throw new CacheException("元素类类型不能为空");
        }
        return redissonClient.getMap(key);
    }

    @Override
    public <V extends Serializable> Map<String, V> getMap(
        String key, Class<V> clazz,
        Function<String, Map<String, V>> valueLoader) {
        if (valueLoader == null) {
            throw new CacheException(CacheErrorCode.NO_VALUE_LOADER);
        }
        Map<String, V> cacheMap = this.getMap(key, clazz);
        if (MapUtil.isEmpty(cacheMap)) {
            Map<String, V> newMap = valueLoader.apply(key);
            this.putAllHash(key, newMap);
            cacheMap = newMap;
        }
        return cacheMap;
    }

    @Override
    public long increment(String key) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        RAtomicLong atomicLong = redissonClient.getAtomicLong(
            key);
        return atomicLong.incrementAndGet();
    }

    @Override
    public long increment(String key,
                          Long delta) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        if (delta == null) {
            throw new CacheException("缺失增量值delta");
        }
        RAtomicLong atomicLong = redissonClient.getAtomicLong(
            key);
        return atomicLong.addAndGet(delta);
    }

    @Override
    public long decrement(String key) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        RAtomicLong atomicLong = redissonClient.getAtomicLong(
            key);
        return atomicLong.decrementAndGet();
    }

    @Override
    public long decrement(String key,
                          Long delta) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        if (delta == null) {
            throw new CacheException("缺失增量值delta");
        }
        RAtomicLong atomicLong = redissonClient.getAtomicLong(
            key);
        return atomicLong.addAndGet(-delta);
    }

    @Override
    public boolean expire(String key,
                          Duration ttl) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.expire(ttl);
    }

    @Override
    public boolean expireIfExists(String key,
                                  Duration ttl) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (bucket.isExists()) {
            return bucket.expire(ttl);
        }
        return false;
    }

    @Override
    public boolean expireAtIfExists(String key,
                                    LocalDateTime until) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (bucket.isExists()) {
            return bucket.expire(until.toInstant(ZoneOffset.of("+8")));
        }
        return false;
    }

    @Override
    public boolean expireAt(String key,
                            LocalDateTime until) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.expire(until.toInstant(ZoneOffset.of("+8")));
    }

    @Override
    public boolean delete(String key) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        long removedNum = redissonClient.getKeys()
            .deleteByPattern(key);
        log.debug("[ Redisson ] 删除key: {}, 删除条数: {}",
            key, removedNum);
        return removedNum > 0;
    }

    @Override
    public boolean hdel(String key,
                        String... fields) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        RMap<Object, Object> map = redissonClient.getMap(key);
        long removedNum = map.fastRemove(fields);
        log.debug("[ Redisson ] 删除key: {}, 删除条数: {}",
            key, removedNum);
        return removedNum > 0;
    }

    @Override
    public boolean delete(List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return true;
        }
        AtomicLong removedNum = new AtomicLong();
        keys.forEach(k -> {
            removedNum.addAndGet(
                redissonClient.getKeys().deleteByPattern(k));
            log.debug("[ Redisson ] 删除key: {}, 删除条数: {}",
                keys, removedNum.get());
        });
        return removedNum.get() > 0;
    }

    @Override
    public boolean exists(String key) {
        return redissonClient.getKeys().countExists(key) >= 1;
    }

    @Override
    public void doInLock(String lockName, long waitTime, long leaseTime, Executable executable) throws InterruptedException {
        if (StrUtil.isBlank(lockName)) {
            throw new CacheException("lockName不能为空");
        }
        if (executable == null) {
            throw new CacheException("executable不能为空");
        }
        RLock lock = redissonClient.getLock(lockName);
        if (!lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) { // NOSONAR
            throw new CacheException("请求过于频繁，请稍后重试");
        }
        try {
            log.warn(
                "[ Redisson ] 获取到分布式锁: {}, interrupted:{}, hold:{}, threadId:{}",
                lock.getName(), Thread.currentThread().isInterrupted(),
                lock.isHeldByCurrentThread(), Thread.currentThread().getId());
            executable.execute();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                log.warn(
                    "[ Redisson ] 释放分布式锁: {}, interrupted:{}, hold:{}, threadId:{}",
                    lock.getName(), Thread.currentThread().isInterrupted(),
                    lock.isHeldByCurrentThread(),
                    Thread.currentThread().getId());
                lock.unlock();
            } else {
                log.warn(
                    "[ Redisson ] 非当前线程{}持有锁，或者锁已释放，无法释放分布式锁: {}",
                    Thread.currentThread().getName() + "-"
                        + Thread.currentThread().getId(), lockName);
                log.warn(
                    "[ Redisson ] 分布式锁: {}, interrupted:{}, hold:{}, threadId:{}",
                    lock, Thread.currentThread().isInterrupted(),
                    lock.isHeldByCurrentThread(),
                    Thread.currentThread().getId());
            }
        }
    }

    @Override
    public Boolean registerRateLimiter(String key, Integer qps) {
        if (StrUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        try {
            return redissonClient.getRateLimiter(key).trySetRate(RateType.OVERALL, qps, 1, RateIntervalUnit.SECONDS);
        } catch (Exception e) {
            log.error("注册流控器异常key:{}", key);
            return false;
        }
    }

    @Override
    public Boolean unRegisterRateLimiter(String key) {
        try {
            return redissonClient.getRateLimiter(key).delete();
        } catch (Exception e) {
            log.error("删除流控器失败key:{}", key);
            return false;
        }
    }

    @Override
    public Boolean acquireRateLimit(String key, long timeout, TimeUnit unit) {
        try {
            return redissonClient.getRateLimiter(key).tryAcquire(timeout, unit);
        } catch (Exception e) {
            log.error("获取流控器令牌异常key:{}", key);
            return false;
        }
    }

    private RTopic getTopic(String topic) {
        return redissonClient.getTopic(topic, StringCodec.INSTANCE);
    }

    @Override
    public void getTopicAndPublishMsg(String topic, String msg) {
        this.getTopic(topic).publish(msg);
    }

    @Override
    public Integer registerSubscribeListener(String topic, EventListener listener) {
        if (listener instanceof MessageListener) {
            try {
                return this.getTopic(topic).addListener(String.class, (MessageListener<String>) listener);
            } catch (Exception e) {
                log.error("订阅主题{}失败,Listener{}", topic, listener, e);
                throw new BusinessException(CacheErrorCode.SUBSCRIBE_ERROR);
            }
        } else {
            log.error("订阅主题{} 的监听器类型不是org.redisson.api.listener.MessageListener,Listener{},ListenerClass{}", topic, listener, listener.getClass());
            throw new BusinessException(CacheErrorCode.MESSAGE_LISTENER_CAST_ERROR);
        }
    }

    @Override
    public void unRegisterSubscribeListener(String topic, Integer... listenerIds) {
        this.getTopic(topic).removeListener(listenerIds);
    }

    private RPermitExpirableSemaphore getSemaphore(String key) {
        if (CharSequenceUtil.isBlank(key)) {
            throw new CacheException(CacheErrorCode.KEY_IS_EMPTY);
        }
        return redissonClient.getPermitExpirableSemaphore(key);
    }

    @Override
    public boolean initSemaphore(String key, int permits) {
        return this.getSemaphore(key).trySetPermits(permits);
    }

    @Override
    public boolean isSemaphoreExists(String key) {
        return this.getSemaphore(key).isExists();
    }

    @Override
    public int getSemaphoreAviablePermits(String key) {
        return this.getSemaphore(key).availablePermits();
    }

    @Override
    public void releaseSemaphore(String key, String permitsId) {
        try {
            this.getSemaphore(key).release(permitsId);
        } catch (RedisException redisException) {
            throw new CacheException(CacheErrorCode.SEMAPHORE_PERMITS_ID_ILLEGAL);
        }
    }

    @Override
    public boolean tryReleaseSemaphore(String key, String permitsId) {
        return this.getSemaphore(key).tryRelease(permitsId);
    }

    @Override
    public String tryAcquireSemaphore(String key, long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException  {
        return this.getSemaphore(key).tryAcquire(waitTime, leaseTime, timeUnit);
    }

    @Override
    public void doInSemaphore(String key, long waitTime, long leaseTime, TimeUnit timeUnit, Executable executable) {
        RPermitExpirableSemaphore semaphore = this.getSemaphore(key);
        String semaphoreId = null;
        try {
            semaphoreId = this.tryAcquireSemaphore(key, waitTime, leaseTime, timeUnit);
        } catch (InterruptedException e) {
            throw new CacheException(CacheErrorCode.NO_SEMAPHORE_PERMITS_AVAILABLE);
        }
        if (CharSequenceUtil.isBlank(semaphoreId)) {
            throw new CacheException(CacheErrorCode.NO_SEMAPHORE_PERMITS_AVAILABLE);
        }
        try {
            log.info("[凭证] 成功获取凭证 permitId：{}", semaphoreId);
            executable.execute();
        } finally {
            boolean successRelease = semaphore.tryRelease(semaphoreId);
            if (successRelease) {
                log.info("[凭证] 成功释放凭证 permitId：{}", semaphoreId);
            } else {
                log.warn("[凭证] 凭证释放失败 permitId：{}, 可能已经自动释放或者刷新了凭证配置", semaphoreId);
            }
        }
    }

    @Override
    public boolean resetSemaphore(@NonNull String key, int permits)  {
        log.info("[凭证] 重置凭证: {}", key);
        this.getSemaphore(key).delete();
        return this.initSemaphore(key, permits);
    }


}
