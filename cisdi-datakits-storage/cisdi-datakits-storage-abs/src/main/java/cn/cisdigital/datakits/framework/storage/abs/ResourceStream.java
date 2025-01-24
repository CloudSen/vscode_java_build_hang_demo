package cn.cisdigital.datakits.framework.storage.abs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 当前资源的输入输出流，支持自动清理
 *
 * @author xxx
 * @since 2023-07-13
 */
@Slf4j
@Getter
@AllArgsConstructor
public final class ResourceStream implements Closeable {

    /**
     * 当前资源的输入流
     */
    @Nullable
    private final InputStream inputStream;

    /**
     * 当前资源的输出流
     */
    @Nullable
    private final OutputStream outputStream;

    @Override
    public void close() throws IOException {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            log.error("[对象存储] 资源流关闭失败", e);
        }
    }
}
