package cn.cisdigital.datakits.framework.util.sqlparser.parser;

import cn.cisdigital.datakits.framework.common.util.Preconditions;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 各个数据库类型parser配置构建工厂类
 *
 * @author xxx
 * @since 2024/4/18 18:23
 */
@Component
@Slf4j
public class ParserConfigBuilderFactory implements ApplicationContextAware {

    /**
     * 用于保存接口实现类名及对应的类
     */
    private final Map<DataBaseTypeEnum, IParserConfigBuildStrategy> builderMap = new HashMap<>();

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        final Map<String, IParserConfigBuildStrategy> beansOfType = applicationContext.getBeansOfType(IParserConfigBuildStrategy.class);
        beansOfType.values().forEach(builder -> builderMap.put(builder.dbType(), builder));
    }

    /**
     * 获取配置构造器
     * @param dataBaseTypeEnum 数据库类型
     * @return 构造器
     * @throws NullPointerException 参数为空
     * @throws UnsupportedOperationException 不支持的类型
     */
    public IParserConfigBuildStrategy getNonnullParserConfigBuilder(final DataBaseTypeEnum dataBaseTypeEnum) throws NullPointerException,UnsupportedOperationException {
        Preconditions.checkNotNull(dataBaseTypeEnum,"db type can not be null");
        final IParserConfigBuildStrategy parserConfigBuilder = builderMap.get(dataBaseTypeEnum);
        if(Objects.isNull(parserConfigBuilder)){
            throw new UnsupportedOperationException();
        }
        return parserConfigBuilder;
    }

}
