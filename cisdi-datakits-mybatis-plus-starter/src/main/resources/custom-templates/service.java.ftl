package ${package.Service};

import lombok.RequiredArgsConstructor;
import ${package.ServiceImpl}.${table.serviceImplName};
import org.springframework.stereotype.Service;

/**
 * ${table.comment!} 服务类
 *
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
class ${table.serviceName}
<#else>
@Service
@RequiredArgsConstructor
public class ${table.serviceName} {

    private final ${table.serviceImplName} ${table.serviceImplName?uncap_first};

}
</#if>
