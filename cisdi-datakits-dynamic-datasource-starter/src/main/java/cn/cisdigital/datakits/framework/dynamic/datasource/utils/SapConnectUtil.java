package cn.cisdigital.datakits.framework.dynamic.datasource.utils;

import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class SapConnectUtil {

    private static MyDestinationDataProvider myProvider;
    private static final String ZH = "zh";

    /**
     * 配置生成连接
     * qj
     */
    public static class MyDestinationDataProvider implements DestinationDataProvider {
        private DestinationDataEventListener eL;
        private final HashMap<String, Properties> secureDBStorage = new HashMap<>();

        // 实现接口：获取连接配置属性
        public Properties getDestinationProperties(String destinationName) {
            try {
                //read the destination from DB
                Properties p = secureDBStorage.get(destinationName);
                if (p != null) {
                    //check if all is correct, for example
                    if (p.isEmpty())
                        throw new DataProviderException(DataProviderException.Reason.INVALID_CONFIGURATION, "destination configuration is incorrect", null);
                    return p;
                }
                return null;
            } catch (RuntimeException re) {
                throw new DataProviderException(DataProviderException.Reason.INTERNAL_ERROR, re);
            }
        }

        public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
            this.eL = eventListener;
        }

        public boolean supportsEvents() {
            return true;
        }


        public void changeProperties(String destName, Properties properties) {
            synchronized (secureDBStorage) {
                if (properties == null) {
                    if (secureDBStorage.remove(destName) != null)
                        eL.deleted(destName);
                } else {
                    secureDBStorage.put(destName, properties);
                    eL.updated(destName); // create or updated
                }
            }
        }
    }

    /**
     * 注册 DestinationDataProvider，确保只注册一次
     */
    public synchronized static void registerProvider() {
        if (Objects.nonNull(myProvider)) {
            return;
        }
        MyDestinationDataProvider provider = new MyDestinationDataProvider();
        Environment.registerDestinationDataProvider(provider);
        myProvider = provider;
    }

    public static void createDestinationDataFile(String destinationName, DataSourceDto sourceDto) {
        Properties connectProperties = new Properties();
        // 服务器
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, sourceDto.getHost());
        // 系统编号
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, sourceDto.getSysnr());
        // SAP集团
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, sourceDto.getClient());
        // SAP用户名
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, sourceDto.getUsername());
        // 密码
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, sourceDto.getPassword());
        // 系统标识
        connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME, sourceDto.getR3name());
        // 登录语言
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, ZH);
        // 最小空闲连接数, 默认值: 5
        String jcoPoolCapacity = Objects.isNull(sourceDto.getMinimumIdle()) ? "5" : String.valueOf(sourceDto.getMinimumIdle());
        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, jcoPoolCapacity);
        // 最大连接池大小, 默认值: 10
        String jcoPeakLimit = Objects.isNull(sourceDto.getMaximumPoolSize()) ? "10" : String.valueOf(sourceDto.getMaximumPoolSize());
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, String.valueOf(jcoPeakLimit));
        myProvider.changeProperties(destinationName, connectProperties);
    }
}
