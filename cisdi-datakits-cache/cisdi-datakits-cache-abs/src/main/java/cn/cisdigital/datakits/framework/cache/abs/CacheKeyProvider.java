package cn.cisdigital.datakits.framework.cache.abs;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.prefs.PreferencesFactory;

/**
 * 缓存key提供器
 *
 * <p>缓存key =  前缀:模块:唯一识别码（例如id）</p>
 * gateway:xxx:user:1
 */
@RequiredArgsConstructor
public abstract class CacheKeyProvider implements Serializable {

    /**
     * 缓存key模板 占位符使用{0}这种形式 底层使用的MessageFormat
     * @link java.text.MessageFormat
     */
    private final String keyTemplate;


    /**
     * 获取Key
     * @param keys 模板中的占位符
     * @return 生成的key
     */
    public String getKey(Object... keys) {
        if (StrUtil.isBlank(keyTemplate)) {
            throw new CacheException("key模板不能为空");
        }
       return MessageFormat.format(keyTemplate,keys);
    }

}
