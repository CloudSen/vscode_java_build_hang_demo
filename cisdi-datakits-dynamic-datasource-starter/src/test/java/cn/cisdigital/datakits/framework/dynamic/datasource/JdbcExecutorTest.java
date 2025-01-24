package cn.cisdigital.datakits.framework.dynamic.datasource;

import cn.cisdigital.datakits.framework.cloud.alibaba.service.RemoteCountService;
import cn.cisdigital.datakits.framework.dynamic.datasource.database.executor.jdbc.ColumnInfo;
import cn.cisdigital.datakits.framework.dynamic.datasource.database.executor.jdbc.JdbcExecutor;
import cn.cisdigital.datakits.framework.dynamic.datasource.database.executor.jdbc.QueryResult;
import cn.cisdigital.datakits.framework.dynamic.datasource.toolkit.DynamicDataSourceHolder;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author xxx
 * @since 2024-07-05
 */
public class JdbcExecutorTest extends BaseIntegrationTest{


    @Autowired
    JdbcExecutor jdbcExecutor;

    @Test
    public void testQueryDoris() {

        String sql = "select item_id,item_name from ods_prod.ods_item_info_di limit 10";

        DataSourceDto dataSourceDto = new DataSourceDto();
        dataSourceDto.setUrl("jdbc:mysql://127.0.0.1:19030/ods_prod?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&useSSL=false&rewriteBatchedStatements=true&allowMultiQueries=true");
        dataSourceDto.setUsername("datakits_query_prod");
        dataSourceDto.setPassword("cisdi@123456");
        dataSourceDto.setPort(19030);
        dataSourceDto.setHost("127.0.0.1");
        dataSourceDto.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
        dataSourceDto.setDatabaseDriver("com.mysql.cj.jdbc.Driver");
        dataSourceDto.setDatasourceName("ods_prod");
        dataSourceDto.setSchemaName("ods_prod");


        QueryResult queryResult = jdbcExecutor.doQueryWithDefaultControl(dataSourceDto, sql);


        List<ColumnInfo> metas = queryResult.getMetas();

        for (ColumnInfo meta : metas) {

            System.out.println(meta.toString());
        }


    }

    @Test
    public void testQueryMysql() {

        String sql = " SELECT\n" +
                "            wpi.id AS processInstanceId,\n" +
                "            wpi.state AS state,\n" +
                "            wpi.end_time,\n" +
                "            wpi.business_id,\n" +
                "            wti.id AS taskId\n" +
                "        FROM\n" +
                "            wf_process_instance wpi\n" +
                "                LEFT JOIN ( SELECT id, process_instance_id FROM wf_task_instance ) wti ON wpi.id = wti.process_instance_id\n" +
                "   ";

        DataSourceDto dataSourceDto = new DataSourceDto();
        dataSourceDto.setUrl("jdbc:mysql://127.0.0.1:33061/datakits?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&useSSL=false&rewriteBatchedStatements=true&allowMultiQueries=true");
        dataSourceDto.setUsername("datakits");
        dataSourceDto.setPassword("Cisdi@123456");
        dataSourceDto.setPort(33061);
        dataSourceDto.setHost("127.0.0.1");
        dataSourceDto.setDataBaseTypeEnum(DataBaseTypeEnum.MYSQL);
        dataSourceDto.setDatabaseDriver("com.mysql.cj.jdbc.Driver");
        dataSourceDto.setDatasourceName("datakits");
        dataSourceDto.setSchemaName("datakits");


        QueryResult queryResult = jdbcExecutor.doQueryWithDefaultControl(dataSourceDto, sql);


        List<ColumnInfo> metas = queryResult.getMetas();

        for (ColumnInfo meta : metas) {

            System.out.println(meta.toString());
        }


    }
}
