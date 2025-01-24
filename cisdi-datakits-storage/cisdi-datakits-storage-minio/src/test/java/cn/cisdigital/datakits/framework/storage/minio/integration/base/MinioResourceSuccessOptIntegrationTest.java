package cn.cisdigital.datakits.framework.storage.minio.integration.base;

import cn.cisdigital.datakits.framework.storage.abs.CisdiAbstractResource;
import cn.cisdigital.datakits.framework.storage.abs.ResourceStream;
import cn.cisdigital.datakits.framework.storage.abs.ResourceUtils;
import cn.cisdigital.datakits.framework.storage.minio.MinioAutoConfiguration;
import cn.cisdigital.datakits.framework.storage.minio.config.CisdiMinioProperties;
import cn.cisdigital.datakits.framework.storage.minio.integration.BaseIntegrationTest;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Minio 资源对象基本操作成功的测试
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MinioResourceSuccessOptIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private MinioClient minioClient;

    @AfterAll
    void delete_all_test_resource() {
        CisdiMinioProperties properties = context.getBean(CisdiMinioProperties.class);
        assertThat(properties).isNotNull();

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(properties.getBucket())
                        .recursive(true)
                        .build());

        try {
            for (Result<Item> result : results) {
                Item item = result.get();
                String objectName = item.objectName();
                System.out.println("删除测试资源：" + objectName);
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(properties.getBucket())
                                .object(objectName)
                                .build());
            }
        } catch (Exception e) {
            System.err.println("清理测试资源失败");
        }

    }

    @Test
    @Order(1)
    @DisplayName("成功加载MinioClient")
    void success_register_beans() {
        assertThat(context).isNotNull();
        assertThat(context.getBean(MinioAutoConfiguration.class)).isNotNull();
        assertThat(context.getBean(CisdiMinioProperties.class)).isNotNull();
        assertThat(context.getBean(MinioClient.class)).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("成功判断目录资源不存在")
    void success_check_path_resource_not_exists() {
        String folderFullPath = "minio://integration-test/not-exists-folder/";
        assertDoesNotThrow(() -> {
            CisdiAbstractResource resource = ResourceUtils.getResourceFrom(resourceLoader, folderFullPath);
            assertThat(resource).isNotNull();
            assertThat(resource.exists()).isFalse();
        });
    }

    @Test
    @Order(3)
    @DisplayName("成功判断文件资源不存在")
    void success_check_file_resource_not_exists() {
        String fileFullPath = "minio://integration-test/not-exists-folder/not-exits-file.txt";
        assertDoesNotThrow(() -> {
            CisdiAbstractResource resource = ResourceUtils.getResourceFrom(resourceLoader, fileFullPath);
            assertThat(resource).isNotNull();
            assertThat(resource.exists()).isFalse();
        });
    }

    @Test
    @Order(4)
    @DisplayName("成功上传文件")
    void success_upload_file() {
        String fileFullPath = "minio://integration-test/test-folder/test-file.txt";
        String fileContent = "This is a test file content.";

        assertDoesNotThrow(() -> {
            CisdiAbstractResource resource = ResourceUtils.getResourceFrom(resourceLoader, fileFullPath);
            assertThat(resource).isNotNull();

            assertThat(resource.exists()).isFalse();
            resource.setSelfInputStream(new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)));
            boolean uploadSuccess = resource.upload();
            assertThat(uploadSuccess).isTrue();
            assertThat(resource.exists()).isTrue();
        });
    }

    @Test
    @Order(5)
    @DisplayName("成功下载上传的文件")
    void success_download_uploaded_file() {
        String fileFullPath = "minio://integration-test/test-folder/test-file.txt";
        String expectedContent = "This is a test file content.";

        assertDoesNotThrow(() -> {
            CisdiAbstractResource resource = ResourceUtils.getResourceFrom(resourceLoader, fileFullPath);
            assertThat(resource).isNotNull();
            assertThat(resource.exists()).isTrue();

            try (ResourceStream downloadStream = resource.download()) {
                InputStream inputStream = downloadStream.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder contentBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    contentBuilder.append(line);
                }
                String downloadedContent = contentBuilder.toString();
                assertThat(downloadedContent).isEqualTo(expectedContent);
            }
        });
    }


}
