package cn.cisdigital.datakits.framework.cache.redisson;

import cn.cisdigital.datakits.framework.cache.abs.CacheException;
import org.junit.jupiter.api.*;
import org.redisson.api.RPermitExpirableSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RedissonCacheServiceImplTest extends BaseIntegrationTest {

    @Autowired
    private CacheServiceImpl cacheService;

    @Autowired
    private RedissonClient redissonClient;

    private static final String TEST_KEY = "testSemaphore";

    @BeforeEach
    public void setUp() {
        RPermitExpirableSemaphore semaphore = redissonClient.getPermitExpirableSemaphore(TEST_KEY);
        semaphore.delete();
    }

    @AfterEach
    void cleanKey() {
        RPermitExpirableSemaphore semaphore = redissonClient.getPermitExpirableSemaphore(TEST_KEY);
        semaphore.delete();
    }

    @Test
    @Order(1)
    @DisplayName("成功获取信号量")
    void testGetSemaphore() {
        assertFalse(cacheService.isSemaphoreExists(TEST_KEY));
        assertTrue(cacheService.initSemaphore(TEST_KEY, 1));
        assertTrue(cacheService.isSemaphoreExists(TEST_KEY));
    }

    @Test
    @Order(2)
    @DisplayName("信号量只能初始化1次")
    void testInitSemaphoreOnlyOnce() {
        boolean result = cacheService.initSemaphore(TEST_KEY, 5);
        assertTrue(result);
        assertEquals(5, cacheService.getSemaphoreAviablePermits(TEST_KEY));
        boolean notInit = cacheService.initSemaphore(TEST_KEY, 6);
        assertEquals(false, notInit);
        assertEquals(5, cacheService.getSemaphoreAviablePermits(TEST_KEY));
    }

    @Test
    @Order(3)
    @DisplayName("获取正确的剩余可用凭证")
    void testGetSemaphoreAvailablePermits() {
        boolean result = cacheService.initSemaphore(TEST_KEY, 5);
        assertTrue(result);
        assertEquals(5, cacheService.getSemaphoreAviablePermits(TEST_KEY));
    }

    @Test
    @Order(4)
    @DisplayName("从存在的semaphore获取凭证，成功获取凭证")
    void testTryAcquirePermits() throws InterruptedException {
        boolean result = cacheService.initSemaphore(TEST_KEY, 5);
        assertTrue(result);
        String semaphoreId = cacheService.tryAcquireSemaphore(TEST_KEY, 1, 1, TimeUnit.SECONDS);
        assertNotNull(semaphoreId);
        assertEquals(4, cacheService.getSemaphoreAviablePermits(TEST_KEY));
    }

    @Test
    @Order(5)
    @DisplayName("从存在的semaphore获取凭证，但凭证耗尽")
    void testAcquireNoPermits() throws InterruptedException {
        boolean result = cacheService.initSemaphore(TEST_KEY, 1);
        assertTrue(result);
        String semaphoreId = cacheService.tryAcquireSemaphore(TEST_KEY, 1, 10, TimeUnit.SECONDS);
        assertNotNull(semaphoreId);
        assertEquals(0, cacheService.getSemaphoreAviablePermits(TEST_KEY));

        String semaphoreId2 = cacheService.tryAcquireSemaphore(TEST_KEY, 1, 10, TimeUnit.SECONDS);
        assertNull(semaphoreId2);
    }

    @Test
    @Order(5)
    @DisplayName("从不存在的semaphore获取凭证，结果是凭证耗尽")
    void testAcquireFromNotExistsSemaphore() throws InterruptedException {
        boolean result = cacheService.initSemaphore(TEST_KEY, 1);
        assertTrue(result);
        String semaphoreId = cacheService.tryAcquireSemaphore(TEST_KEY + "ERROR", 1, 1, TimeUnit.SECONDS);
        assertNull(semaphoreId);
    }

    @Test
    @Order(6)
    @DisplayName("凭证自动管理时，凭证充足情况下，业务逻辑包裹在信号量中执行")
    void testDoInSemaphore() {
        cacheService.initSemaphore(TEST_KEY, 1);
        cacheService.doInSemaphore(TEST_KEY, 1, 1, TimeUnit.SECONDS, () -> {
            assertEquals(0, cacheService.getSemaphoreAviablePermits(TEST_KEY));
        });
        assertEquals(1, cacheService.getSemaphoreAviablePermits(TEST_KEY));
    }

    @Test
    @Order(7)
    @DisplayName("凭证自动管理时，凭证耗尽情况下，业务逻辑不会执行")
    void testDoInSemaphoreNoPermits() {
        AtomicBoolean isBusinessHandled = new AtomicBoolean(false);
        cacheService.initSemaphore(TEST_KEY, 1);
        assertThrows(CacheException.class, () -> {
            cacheService.doInSemaphore(TEST_KEY, 1, 10, TimeUnit.SECONDS, () -> {
                cacheService.doInSemaphore(TEST_KEY, 1, 10, TimeUnit.SECONDS, () -> {
                    // 尝试获取2个许可,但只有1个可用,应该抛出异常
                    isBusinessHandled.compareAndSet(false, true);
                });
            });
        });
        assertEquals(isBusinessHandled.get(), false);
        assertEquals(1, cacheService.getSemaphoreAviablePermits(TEST_KEY));
    }

    @Test
    @Order(8)
    @DisplayName("凭证自动管理时，若凭证已经被自动释放,不会报错")
    void testDoInSemaphoreWithPermitsIllegal() {
        AtomicBoolean isBusinessHandled = new AtomicBoolean(false);
        cacheService.initSemaphore(TEST_KEY, 1);
        assertDoesNotThrow(() -> {
            // 凭证1s自动释放
            cacheService.doInSemaphore(TEST_KEY, 1, 1, TimeUnit.SECONDS, () -> {
                // 业务逻辑执行3s
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
        assertEquals(isBusinessHandled.get(), false);
        assertEquals(1, cacheService.getSemaphoreAviablePermits(TEST_KEY));
    }

    @Test
    @Order(9)
    @DisplayName("使用错误的凭证ID或凭证已经被释放,无法手动释放凭证")
    void testReleaseIdError() {
        cacheService.initSemaphore(TEST_KEY, 1);
        String semaphoreId = null;
        try {
            semaphoreId = cacheService.tryAcquireSemaphore(TEST_KEY, 1, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotNull(semaphoreId);
        assertThrows(CacheException.class, () -> {
            cacheService.releaseSemaphore(TEST_KEY, "48656c6c6f20576f726c64");
        });
    }

    @Test
    @Order(10)
    @DisplayName("成功重建semaphore")
    void testResetSemaphoreSuccess() {
        int oldPermits = 1;
        int newPermits = 2;

        cacheService.initSemaphore(TEST_KEY, 1);
        int oldAvailablePermits = cacheService.getSemaphoreAviablePermits(TEST_KEY);
        assertEquals(oldPermits, oldAvailablePermits);

        boolean resetResult = cacheService.resetSemaphore(TEST_KEY, newPermits);
        assertTrue(resetResult);
        int newAvailablePermits = cacheService.getSemaphoreAviablePermits(TEST_KEY);
        assertEquals(newPermits, newAvailablePermits);
    }

    @Test
    @Order(11)
    @DisplayName("多次释放同一个semaphore，凭证不会超发")
    void testRepeatReleaseSemaphore() {
        assertDoesNotThrow(() -> {
            cacheService.initSemaphore(TEST_KEY, 1);
            assertEquals(1, cacheService.getSemaphoreAviablePermits(TEST_KEY));

            String permitId = cacheService.tryAcquireSemaphore(TEST_KEY, 1L, 5L, TimeUnit.SECONDS);
            assertEquals(0, cacheService.getSemaphoreAviablePermits(TEST_KEY));

            cacheService.tryReleaseSemaphore(TEST_KEY, permitId);
            assertEquals(1, cacheService.getSemaphoreAviablePermits(TEST_KEY));

            cacheService.tryReleaseSemaphore(TEST_KEY, permitId);
            assertEquals(1, cacheService.getSemaphoreAviablePermits(TEST_KEY));

            cacheService.tryReleaseSemaphore(TEST_KEY, permitId);
            assertEquals(1, cacheService.getSemaphoreAviablePermits(TEST_KEY));
        });
    }

    @Test
    @Order(12)
    @DisplayName("获取凭证后，然后重置凭证，再释放之前的凭证，凭证不会超发")
    void testReleaseSemaphoreAfterReset() {
        assertDoesNotThrow(() -> {
            cacheService.initSemaphore(TEST_KEY, 1);
            assertEquals(1, cacheService.getSemaphoreAviablePermits(TEST_KEY));

            String permitId = cacheService.tryAcquireSemaphore(TEST_KEY, 1L, 10L, TimeUnit.SECONDS);
            assertEquals(0, cacheService.getSemaphoreAviablePermits(TEST_KEY));

            cacheService.resetSemaphore(TEST_KEY, 2);
            assertEquals(2, cacheService.getSemaphoreAviablePermits(TEST_KEY));

            cacheService.tryReleaseSemaphore(TEST_KEY, permitId);
            assertEquals(2, cacheService.getSemaphoreAviablePermits(TEST_KEY));

            cacheService.tryReleaseSemaphore(TEST_KEY, permitId);
            assertEquals(2, cacheService.getSemaphoreAviablePermits(TEST_KEY));
        });
    }

    @Test
    @Order(13)
    @DisplayName("并发获取凭证，凭证id不应该重复")
    void testConcurrentFetchPermits() {
        assertDoesNotThrow(() -> {
            int numberOfPermits = 1000;
            cacheService.initSemaphore(TEST_KEY, numberOfPermits);
            assertEquals(numberOfPermits, cacheService.getSemaphoreAviablePermits(TEST_KEY));

            int concurrencyLevel = 20;
            ExecutorService executor = Executors.newFixedThreadPool(concurrencyLevel);

            List<CompletableFuture<String>> futures = new ArrayList<>();

            for (int i = 0; i < numberOfPermits + 100; i++) {
                CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return cacheService.tryAcquireSemaphore(TEST_KEY, 1L, 200L, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        System.out.println("Failed to acquire semaphore: " + e.getMessage());
                        return null;
                    }
                }, executor);
                futures.add(future);
            }

            List<String> permitIdHistories = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(permitId -> permitId != null)
                    .collect(Collectors.toList());
            Set<String> permitIdSet = new HashSet<>(permitIdHistories);

            // 判断是否有重复的permitId
            System.out.println(permitIdSet);
            assertTrue(permitIdHistories.size() > numberOfPermits);
            assertEquals(permitIdHistories.size(), permitIdSet.size(), "存在重复permitId");
            assertThat(permitIdHistories).doesNotContain("PONG");
        });
    }
}
