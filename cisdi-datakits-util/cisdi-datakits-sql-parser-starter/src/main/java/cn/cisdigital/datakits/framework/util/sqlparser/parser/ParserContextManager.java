package cn.cisdigital.datakits.framework.util.sqlparser.parser;

import cn.cisdigital.datakits.framework.common.util.Preconditions;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.util.sqlparser.catalog.ICatalogDiscover;
import cn.cisdigital.datakits.framework.util.sqlparser.config.DatasourceProperties;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ContextDataSourceDto;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.SqlParserConfig;
import cn.cisdigital.datakits.framework.util.sqlparser.util.Convertor;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 用于处理catalog对象(如表、视图、函数和类型)的管理器。
 * 内部保存了所有可用的catalog
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ParserContextManager implements DisposableBean {

    private final DatasourceProperties datasourceProperties;
    private final ParserConfigBuilderFactory parserConfigBuilderFactory;
    private LoadingCache<ContextDataSourceDto, ParserContext> parserContextMap;


    @PostConstruct
    public void init() {
        parserContextMap = CacheBuilder.newBuilder()
                .maximumSize(datasourceProperties.getMaxCacheSize())
                .expireAfterAccess(datasourceProperties.getExpireTime(), TimeUnit.MINUTES)
                .removalListener((RemovalListener<ContextDataSourceDto, ParserContext>) element -> {
                    final String catalogName = Convertor.buildUniqueCodeForCatalog(element.getKey());
                    log.info("parse context {} removed", catalogName);
                    try {
                        element.getValue().close();
                    } catch (Exception e) {
                        log.info("parse context {} removed error", element.getKey(), e);
                    }
                }).build(new CacheLoader<ContextDataSourceDto, ParserContext>() {
                    @Override
                    public ParserContext load(@NonNull ContextDataSourceDto dataSourceDto) throws Exception {
                        final String catalogName = Convertor.buildUniqueCodeForCatalog(dataSourceDto);
                        log.info("add parse context {}", catalogName);
                        return registerContext(dataSourceDto);
                    }
                });
    }

    /**
     * 根据名称返回parser context
     *
     * @param dataSourceDto 数据源连接信息
     * @return 请求的catalog，如果不存在则为空
     * @throws RuntimeException 注册parser context 失败时会抛出异常
     */
    public Optional<ParserContext> getCatalogParserContext(ContextDataSourceDto dataSourceDto) {
        try {
            return Optional.of(parserContextMap.get(dataSourceDto));
        } catch (ExecutionException e) {
            log.error("get {} cache error", dataSourceDto, e);
            throw new RuntimeException("get parser context error", e);
        }
    }

    /**
     * 注册一个新的catalog parser context，catalog名称必须唯一
     *
     * @param dataSourceDto 数据源信息
     * @throws RuntimeException 注册失败
     */
    private ParserContext registerContext(final ContextDataSourceDto dataSourceDto) throws RuntimeException {
        try {
            // 注册解析上下文
            return buildParserContext(dataSourceDto);
        } catch (Exception e) {
            log.error("register add parserContext error {}", dataSourceDto, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private ParserContext buildParserContext(final ContextDataSourceDto dataSourceDto) {
        final IParserConfigBuildStrategy nonnullParserConfigBuilder = parserConfigBuilderFactory.getNonnullParserConfigBuilder(dataSourceDto.getDataBaseTypeEnum());
        final ICatalogDiscover catalogDiscover = nonnullParserConfigBuilder.buildCatalogDiscover(dataSourceDto);

        return ParserContext.builder()
                .config(nonnullParserConfigBuilder.buildSqlParserConfig(new SqlParserConfig()))
                .catalogDiscover(catalogDiscover)
                .parserConfigBuildStrategy(nonnullParserConfigBuilder)
                .catalogContext(() -> dataSourceDto)
                .build();
    }

    /**
     * 取消注册catalog。catalog名必须存在。
     *
     * @throws IllegalArgumentException catalogName is null
     */
    public void unregisterContext(DataSourceDto dataSourceDto) {
        Preconditions.checkNotNull(dataSourceDto,
                "datasource dto cannot be null or empty.");
        parserContextMap.invalidate(Convertor.fromDataSourceDto(dataSourceDto));
    }

    @Override
    public void destroy() throws Exception {
        parserContextMap.asMap().values().forEach(parseContext -> {
            try {
                parseContext.close();
            } catch (Exception e) {
                log.error("bean destroy close {} parser context error ", parseContext, e);
            }
        });
    }
}
