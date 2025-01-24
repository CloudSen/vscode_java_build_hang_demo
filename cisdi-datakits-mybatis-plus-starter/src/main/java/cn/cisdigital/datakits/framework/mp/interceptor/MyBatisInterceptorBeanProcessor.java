package cn.cisdigital.datakits.framework.mp.interceptor;

import cn.cisdigital.datakits.framework.mp.MybatisStarterConstants;
import cn.cisdigital.datakits.framework.mp.interceptor.convert.DbFieldConvertService;
import cn.cisdigital.datakits.framework.mp.properties.MybatisPlusConfigProperties;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * SpringBean初始化后置处理
 * <p>扩展MybatisPlusInterceptor, 在其内部增加新的拦截器来进行字段转换</p>
 *
 * @author xxx
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = MybatisStarterConstants.MYBATIS_PLUS_INTERCEPTOR_PROPERTIES, havingValue = "true")
public class MyBatisInterceptorBeanProcessor implements BeanPostProcessor {

    private final MybatisPlusConfigProperties mybatisPlusConfigProperties;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //针对mybatisPlus拦截器进行扩展
        if (bean instanceof MybatisPlusInterceptor) {
            try {
                //加载实现类
                Class<?> convertServiceClass = Class.forName(
                    mybatisPlusConfigProperties.getFieldConvertInterceptorImpl());
                log.info(MybatisStarterConstants.LOADING_MYBATIS_FIELD_INTERCEPTOR, convertServiceClass);
                DbFieldConvertService convertService = (DbFieldConvertService) convertServiceClass.newInstance();
                MybatisPlusInterceptor mybatisPlusInterceptor = (MybatisPlusInterceptor) bean;
                MyBatisFieldInterceptor fieldInterceptor = new MyBatisFieldInterceptor(convertService);
                //增加字段转换拦截器
                List<InnerInterceptor> interceptors = mybatisPlusInterceptor.getInterceptors();
                List<InnerInterceptor> innerInterceptors = new ArrayList<>();
                innerInterceptors.add(fieldInterceptor);
                innerInterceptors.addAll(interceptors);
                //重置拦截器顺序,避免其他拦截器提前退出导致不执行的情况
                mybatisPlusInterceptor.setInterceptors(innerInterceptors);

            } catch (Exception e) {
                log.error("构建内部SQL转换拦截器失败");
            }
        }
        return bean;
    }
}
