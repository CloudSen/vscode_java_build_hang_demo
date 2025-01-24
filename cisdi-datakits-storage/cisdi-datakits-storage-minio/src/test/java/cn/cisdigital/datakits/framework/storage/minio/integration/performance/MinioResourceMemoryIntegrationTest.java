package cn.cisdigital.datakits.framework.storage.minio.integration.performance;

import cn.cisdigital.datakits.framework.storage.abs.CisdiAbstractResource;
import cn.cisdigital.datakits.framework.storage.abs.ResourceStream;
import cn.cisdigital.datakits.framework.storage.abs.ResourceUtils;
import cn.cisdigital.datakits.framework.storage.minio.integration.BaseIntegrationTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 内存溢出测试
 * ====================
 * 开始运行时的内存使用情况:
 * 堆内存:
 * Init: 512 MB
 * Used: 57 MB
 * Committed: 504 MB
 * Max: 7282 MB
 * 非堆内存:
 * Init: 2 MB
 * Used: 59 MB
 * Committed: 63 MB
 * Max: 0 MB
 * ====================
 * 运行完成后的内存使用情况:
 * 堆内存:
 * Init: 512 MB
 * Used: 151 MB
 * Committed: 597 MB
 * Max: 7282 MB
 * 非堆内存:
 * Init: 2 MB
 * Used: 75 MB
 * Committed: 79 MB
 * Max: 0 MB
 * ====================
 * GC后的内存使用情况:
 * 堆内存:
 * Init: 512 MB
 * Used: 23 MB
 * Committed: 596 MB
 * Max: 7282 MB
 * 非堆内存:
 * Init: 2 MB
 * Used: 75 MB
 * Committed: 79 MB
 * Max: 0 MB
 *
 * @author xxx
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MinioResourceMemoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private static final String LOCATION_PREFIX = "minio://integration-test/performance/";

    private static final byte[] ONE_MB_DATA;

    static {
        int sizeInBytes = 1024 * 1024;
        ONE_MB_DATA = new byte[sizeInBytes];
        for (int i = 0; i < sizeInBytes; i++) {
            ONE_MB_DATA[i] = 'a';
        }
    }

    @BeforeAll
    public void showInitMemoryInfo() {
        System.out.println("====================");
        System.out.println("开始运行时的内存使用情况:");
        logMemoryUsage();
    }

    @AfterAll
    public void showFinishMemoryInfo() {
        assertDoesNotThrow(() -> {
            System.out.println("====================");
            System.out.println("运行完成后的内存使用情况:");
            logMemoryUsage();

            System.gc();
            TimeUnit.SECONDS.sleep(5L);

            System.out.println("====================");
            System.out.println("GC后的内存使用情况:");
            logMemoryUsage();
        });
    }

    @RepeatedTest(500)
    public void testUploadAndDownloadMemoryLeak() throws Exception {
        assertDoesNotThrow(() -> {
            String fileLocation = LOCATION_PREFIX + "test-upload-download-memory-leak.txt";
            CisdiAbstractResource minioResource = ResourceUtils.getResourceFrom(resourceLoader, fileLocation);
            /* 上传1MB的文件 */
            InputStream inputStream = new ByteArrayInputStream(ONE_MB_DATA);
            minioResource.setSelfInputStream(inputStream);
            boolean uploadResult = minioResource.upload();
            assertTrue(uploadResult, "文件上传失败");
            /* 下载文件 */
            try (ResourceStream resourceStream = minioResource.download();) {
                InputStream downloadedInputStream = resourceStream.getInputStream();
                byte[] downloadedData = IOUtils.toByteArray(downloadedInputStream);
                assertTrue(Arrays.equals(ONE_MB_DATA, downloadedData), "文件内容不一致");
            }
            minioResource.delete(false);
            TimeUnit.MICROSECONDS.sleep(300);
        });
    }

    private void logMemoryUsage() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        System.out.println("堆内存:");
        System.out.println("  Init: " + heapMemoryUsage.getInit() / (1024 * 1024) + " MB");
        System.out.println("  Used: " + heapMemoryUsage.getUsed() / (1024 * 1024) + " MB");
        System.out.println("  Committed: " + heapMemoryUsage.getCommitted() / (1024 * 1024) + " MB");
        System.out.println("  Max: " + heapMemoryUsage.getMax() / (1024 * 1024) + " MB");
        System.out.println("非堆内存:");
        System.out.println("  Init: " + nonHeapMemoryUsage.getInit() / (1024 * 1024) + " MB");
        System.out.println("  Used: " + nonHeapMemoryUsage.getUsed() / (1024 * 1024) + " MB");
        System.out.println("  Committed: " + nonHeapMemoryUsage.getCommitted() / (1024 * 1024) + " MB");
        System.out.println("  Max: " + nonHeapMemoryUsage.getMax() / (1024 * 1024) + " MB");
    }

}
