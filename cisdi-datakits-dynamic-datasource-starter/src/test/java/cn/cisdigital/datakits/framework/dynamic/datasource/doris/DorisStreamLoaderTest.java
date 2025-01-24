package cn.cisdigital.datakits.framework.dynamic.datasource.doris;


import cn.cisdigital.datakits.framework.cloud.alibaba.convertor.RestTemplatePropertiesConvertor;
import cn.cisdigital.datakits.framework.cloud.alibaba.properties.CustomHttpProperties;
import cn.cisdigital.datakits.framework.cloud.alibaba.properties.RestTemplateProperties;
import cn.cisdigital.datakits.framework.cloud.alibaba.resttemplate.RestTemplateConfiguration;
import cn.cisdigital.datakits.framework.dynamic.datasource.common.DorisStatusConstants;
import cn.cisdigital.datakits.framework.dynamic.datasource.doris.model.dto.DorisColumnValueDto;
import cn.cisdigital.datakits.framework.dynamic.datasource.doris.model.dto.DorisStorageDataDto;
import cn.cisdigital.datakits.framework.dynamic.datasource.doris.model.dto.DorisStreamLoadConfigDto;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author xxx
 */

public class DorisStreamLoaderTest {
    static RestTemplateConfiguration restTemplateConfiguration;

    static {
        RestTemplateProperties restTemplateProperties = new RestTemplateProperties();
        restTemplateConfiguration = new RestTemplateConfiguration(new SimpleMeterRegistry(), null, null, restTemplateProperties);
    }

    @Test
    public void testSavaData() {
        DorisStreamLoader dorisStreamLoader = new DorisStreamLoader(getDorisStreamLoadConfigDto(), getOkHttpClient(), new CustomHttpProperties());
        String result = dorisStreamLoader.saveData(getInsertDataList(), null);
        Assertions.assertEquals(DorisStatusConstants.SUCCESS, result);
    }

    private DorisStreamLoadConfigDto getDorisStreamLoadConfigDto() {
        DorisStreamLoadConfigDto dorisStreamLoadConfigDto = new DorisStreamLoadConfigDto();
        dorisStreamLoadConfigDto.setHost("10.106.251.72");
        dorisStreamLoadConfigDto.setPort(8030);
        dorisStreamLoadConfigDto.setUser("datakits_prod");
        dorisStreamLoadConfigDto.setPasswd("cisdi@123456");
        dorisStreamLoadConfigDto.setDatabaseName("ads_prod");
        dorisStreamLoadConfigDto.setTableName("student_test");
        return dorisStreamLoadConfigDto;
    }

    private static OkHttpClient getOkHttpClient() {
        CustomHttpProperties customHttpProperties = RestTemplatePropertiesConvertor.convertToCustomHttpProperties(new RestTemplateProperties());
        return restTemplateConfiguration.okHttpConfigClient(customHttpProperties, restTemplateConfiguration.okHttpMetricsEventListener(new HashSet<>()));
    }

    private List<DorisStorageDataDto> getInsertDataList() {
        DorisStorageDataDto data1 = new DorisStorageDataDto();
        data1.setTargetTableName("student_test");
        data1.setTargetDatabaseName("ads_prod");
        DorisColumnValueDto c1 = new DorisColumnValueDto("id", 1, false);
        DorisColumnValueDto c2 = new DorisColumnValueDto("name", "zhagnsan", false);
        DorisColumnValueDto c3 = new DorisColumnValueDto("etl_time", "CURRENT_TIMESTAMP(3)", true);
        data1.setTargetColumnValueList(Arrays.asList(c1, c2, c3));
        DorisStorageDataDto data2 = new DorisStorageDataDto();
        data2.setTargetTableName("student_test");
        data2.setTargetDatabaseName("ads_prod");
        DorisColumnValueDto c12 = new DorisColumnValueDto("id", 2, false);
        DorisColumnValueDto c22 = new DorisColumnValueDto("name", "zhagnsan2", false);
        DorisColumnValueDto c32 = new DorisColumnValueDto("etl_time", "CURRENT_TIMESTAMP(3)", true);
        data2.setTargetColumnValueList(Arrays.asList(c12, c22, c32));
        return Arrays.asList(data1, data2);
    }
}
