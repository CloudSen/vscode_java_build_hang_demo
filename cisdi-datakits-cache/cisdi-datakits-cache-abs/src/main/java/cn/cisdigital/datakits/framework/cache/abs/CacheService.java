package cn.cisdigital.datakits.framework.cache.abs;

import cn.cisdigital.datakits.framework.model.interfaces.Executable;
import lombok.NonNull;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.ToLongFunction;

/**
 * 缓存抽象层
 *
 * @author xxx
 */
public interface CacheService {

    /**
     * 设置String缓存
     *
     * @param key   key
     * @param value 缓存值
     */
    default void set(String key, String value) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * @param key        key
     * @param value      缓存值
     * @param timeToLive 缓存时间（单位 ms）
     */
    default void set(String key, String value, Long timeToLive) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 如果key不存在，才设置String缓存
     *
     * @param key   key
     * @param value 缓存值
     * @return true:当前key没有值，设置成功 false:当前key已有值，设置失败
     */
    default boolean setIfAbsent(String key, String value) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 批量设置String缓存
     *
     * @param mapData 需要缓存的map
     */
    default void setBatch(Map<String, String> mapData) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 设置Long缓存
     *
     * @param key   key
     * @param value 缓存值
     */
    default void setLong(String key, Long value) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 设置对象缓存
     *
     * @param key   key
     * @param value 缓存值
     * @param <V>   值类型
     */
    default <V extends Serializable> void setObject(String key, V value) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 替换list缓存
     *
     * @param key   key
     * @param value 缓存值列表
     * @param <V>   值类型
     */
    default <V extends Serializable> void replaceList(String key, List<V> value) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 批量添加list缓存
     *
     * @param key   key
     * @param value 缓存值列表
     * @param <V>   值类型
     */
    default <V extends Serializable> void addAllList(String key, List<V> value) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 单个添加list缓存
     *
     * @param key   key
     * @param value 缓存值
     * @param <V>   值类型
     */
    default <V extends Serializable> void appendList(String key, V value) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 批量设置set缓存
     *
     * @param key   key
     * @param value 缓存值Set
     * @param <V>   值类型
     */
    default <V extends Serializable> void addAllSet(String key, Set<V> value) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 单个添加set缓存
     *
     * @param key   key
     * @param value 缓存值
     * @param <V>   值类型
     */
    default <V extends Serializable> void addSet(String key, V value) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 批量设置hash缓存
     *
     * @param key   key
     * @param value 缓存值Map
     * @param <V>   值类型
     */
    default <V extends Serializable> void putAllHash(String key, Map<String, V> value) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 单个设置hash缓存
     *
     * @param key   key
     * @param key   hash结构的key
     * @param value 缓存值
     * @param <V>   值类型
     */
    default <V extends Serializable> void putHash(String key, String hKey, V value) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 根据key获取String类型的缓存，如果缓存不存在（null），没有额外操作
     *
     * @param key key
     * @return 缓存值
     */
    default String get(String key) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 根据key获取String类型的缓存，如果缓存不存在（null），则调用valueLoader重新缓存新值
     * <p>
     * 如果valueLoader也没有得到新值，则给当前key缓存默认值None，防止缓存击穿
     * </p>
     *
     * @param key         key
     * @param valueLoader 缓存不存在时的值提供器
     * @return 缓存值
     */
    default String get(String key, Function<String, String> valueLoader) {
        throw new CacheException("暂未实现该方法");
    }

    default Long getLong(String key) {
        throw new CacheException("暂未实现该方法");
    }

    default Long getLong(String key, ToLongFunction<String> valueLoader) {
        throw new CacheException("暂未实现该方法");
    }

    default <V extends Serializable> V getObject(String key, Class<V> clazz) {
        throw new CacheException("暂未实现该方法");
    }

    default <V extends Serializable> V getObject(String key, Class<V> clazz,
            Function<String, V> valueLoader) {
        throw new CacheException("暂未实现该方法");
    }

    default <V extends Serializable> List<V> getList(String key, Class<V> clazz) {
        throw new CacheException("暂未实现该方法");
    }

    default <V extends Serializable> List<V> getList(String key, Class<V> clazz,
            Function<String, List<V>> valueLoader) {
        throw new CacheException("暂未实现该方法");
    }

    default <V extends Serializable> Set<V> getSet(String key, Class<V> clazz) {
        throw new CacheException("暂未实现该方法");
    }

    default <V extends Serializable> Set<V> getSet(String key, Class<V> clazz,
            Function<String, Set<V>> valueLoader) {
        throw new CacheException("暂未实现该方法");
    }

    default <V extends Serializable> Map<String, V> getMap(String key, Class<V> clazz) {
        throw new CacheException("暂未实现该方法");
    }

    default <V extends Serializable> Map<String, V> getMap(String key, Class<V> clazz,
            Function<String, Map<String, V>> valueLoader) {
        throw new CacheException("暂未实现该方法");
    }

    default boolean delete(String key) {
        throw new CacheException("暂未实现该方法");
    }

    default boolean hdel(String key, String... fields) {
        throw new CacheException("暂未实现该方法");
    }

    default boolean delete(List<String> keys) {
        throw new CacheException("暂未实现该方法");
    }

    default long increment(String key) {
        throw new CacheException("暂未实现该方法");
    }

    default long increment(String key, Long delta) {
        throw new CacheException("暂未实现该方法");
    }

    default long decrement(String key) {
        throw new CacheException("暂未实现该方法");
    }

    default long decrement(String key, Long delta) {
        throw new CacheException("暂未实现该方法");
    }

    default boolean expire(String key, Duration ttl) {
        throw new CacheException("暂未实现该方法");
    }

    default boolean expireIfExists(String key, Duration ttl) {
        throw new CacheException("暂未实现该方法");
    }

    default boolean expireAtIfExists(String key, LocalDateTime until) {
        throw new CacheException("暂未实现该方法");
    }

    default boolean expireAt(String key, LocalDateTime until) {
        throw new CacheException("暂未实现该方法");
    }

    default boolean exists(String key) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 在分布式锁中处理某些逻辑
     * <p>
     * 注意，请在同一个线程中使用，否则锁释放会报错
     * </p>
     *
     * @param lockName   锁名
     * @param waitTime   获取锁的等待时间（秒），请根据自己的业务合理设置
     * @param leaseTime  自动释放时间（秒），请根据自己的业务合理设置
     * @param executable 需要执行的逻辑
     * @throws InterruptedException 没有获取到锁抛出异常
     */
    default void doInLock(String lockName, long waitTime, long leaseTime, Executable executable)
            throws InterruptedException {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 注册流控器
     *
     * @param key key
     * @param qps QPS
     * @return 注册结果
     */
    default Boolean registerRateLimiter(String key, Integer qps) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 注销流控器
     *
     * @param key key
     * @return
     */
    default Boolean unRegisterRateLimiter(String key) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 获取流控令牌
     *
     * @param key     key
     * @param timeout 超时
     * @param unit    单位
     * @return
     */
    default Boolean acquireRateLimit(String key, long timeout, TimeUnit unit) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 发送消息
     *
     * @param topic 主体
     * @param msg   消息
     */
    default void getTopicAndPublishMsg(String topic, String msg) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 订阅主题
     *
     * @param topic    主题
     * @param listener 监听器 redisson必须实现org.redisson.api.listener.MessageListener
     *                 且泛型是String!!
     * @return listenerId
     * @link org.redisson.api.listener.MessageListener
     */
    default Integer registerSubscribeListener(String topic, EventListener listener) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 删除topic监听器
     *
     * @param topic       主题
     * @param listenerIds 监听器ids
     */
    default void unRegisterSubscribeListener(String topic, Integer... listenerIds) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 初始化semaphore
     *
     * @param key     semaphore的key值
     * @param permits 凭证数量
     * @return 是否设置成功，ture：成功；false：失败
     */
    default boolean initSemaphore(String key, int permits) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * semaphore是否存在
     *
     * @param key semaphore的key值
     * @return true:存在 false:不存在
     */
    default boolean isSemaphoreExists(String key) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 获取semaphore当前可用的剩余许可数
     *
     * @param key semaphore的key值
     * @return 当前可用的剩余许可数
     */
    default int getSemaphoreAviablePermits(String key) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 释放凭证，如果凭证ID错误，会抛出异常
     *
     * @param key       semaphore的key值
     * @param permitsId 凭证id
     */
    default void releaseSemaphore(String key, String permitsId) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 释放凭证，如果凭证ID错误，不会抛出异常
     *
     * @param key       semaphore的key值
     * @param permitsId 凭证id
     */
    default boolean tryReleaseSemaphore(String key, String permitsId) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 尝试获取凭证
     *
     * @param key       semaphore的key值
     * @param waitTime  获取凭证的等待时间
     * @param leaseTime 自动释放凭证的时间
     * @param timeUnit  时间单位
     * @return 凭证id
     */
    default String tryAcquireSemaphore(String key, long waitTime, long leaseTime, TimeUnit timeUnit)
            throws InterruptedException {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 在semaphore块中，执行某段逻辑
     *
     * @param key        semaphore的key值
     * @param waitTime   获取凭证的等待时间
     * @param leaseTime  自动释放凭证的时间
     * @param timeUnit   时间单位
     * @param executable 需要执行的逻辑
     */
    default void doInSemaphore(String key, long waitTime, long leaseTime, TimeUnit timeUnit, Executable executable) {
        throw new CacheException("暂未实现该方法");
    }

    /**
     * 重置semaphore（删除重建）
     * @param key semaphore的key值
     * @param permits 重置的数量
     * @return true:重置成功 false:重置失败
     */
    default boolean resetSemaphore(@NonNull String key, int permits) {
        throw new CacheException("暂未实现该方法");
    }
}
