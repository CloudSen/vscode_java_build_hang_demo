package cn.cisdigital.datakits.framework.dynamic.datasource.toolkit;


import cn.cisdigital.datakits.framework.dynamic.datasource.utils.SapConnectUtil;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.model.dto.database.TestResultDto;
import cn.cisdigital.datakits.framework.model.enums.EnvironmentEnum;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import lombok.extern.slf4j.Slf4j;


/**
 * @author xxx
 */
@Slf4j
public class SapDynamicHolder {

    public static final String DOT = ".";

    public static TestResultDto testSapConnection(DataSourceDto sapSourceDto, Long resourceId, EnvironmentEnum environmentEnum) {
        TestResultDto resultDto = new TestResultDto();
        try {
            getJCoDestination(sapSourceDto, resourceId, environmentEnum);
            resultDto.setResult(true);
            return resultDto;
        } catch (Throwable e) {
            log.error("数据源[ {} ]连接测试未成功...", sapSourceDto.getHost(), e);
            resultDto.setResult(false);
            resultDto.setErrorMsg(e.getMessage());
            return resultDto;
        }
    }

    /**
     * 获取 JCoDestination
     */
    public static JCoDestination getJCoDestination(DataSourceDto sourceDto, Long resourceId, EnvironmentEnum environmentEnum) {
        resourceId = resourceId == null ? 0 : resourceId;
        String destinationName = resourceId + DOT + environmentEnum.name();
        // 注册 DestinationDataProvider
        SapConnectUtil.registerProvider();
        // 创建连接配置
        SapConnectUtil.createDestinationDataFile(destinationName, sourceDto);
        // 获取连接实例
        try {
            JCoDestination dest = JCoDestinationManager.getDestination(destinationName);
            dest.ping();
            log.info("SAP连接成功, destinationName={}", destinationName);
            return dest;
        } catch (JCoException e) {
            log.error("SAP连接失败, Execution on destination {} failed", destinationName, e);
            throw new RuntimeException(e);
        }
    }
}
