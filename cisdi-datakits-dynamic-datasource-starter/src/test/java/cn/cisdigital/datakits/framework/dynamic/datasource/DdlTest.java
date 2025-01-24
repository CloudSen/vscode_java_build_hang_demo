package cn.cisdigital.datakits.framework.dynamic.datasource;

import cn.cisdigital.datakits.framework.dynamic.datasource.database.ddl.DatabaseIndexBuilderFactory;
import cn.cisdigital.datakits.framework.dynamic.datasource.database.ddl.DorisDdlBuilder;
import cn.cisdigital.datakits.framework.dynamic.datasource.database.ddl.MysqlDdlBuilder;
import cn.cisdigital.datakits.framework.dynamic.datasource.toolkit.DatabaseDdlHolder;
import cn.cisdigital.datakits.framework.dynamic.datasource.toolkit.DynamicDataSourceHolder;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.model.dto.database.*;
import cn.cisdigital.datakits.framework.model.dto.doris.BucketConfigDto;
import cn.cisdigital.datakits.framework.model.dto.doris.DorisTableConfigDto;
import cn.cisdigital.datakits.framework.model.dto.doris.PartitionConfigDto;
import cn.cisdigital.datakits.framework.model.enums.*;
import cn.hutool.core.collection.CollUtil;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

/**
 * @author xxx
 * @since 2024-03-16-17:52
 */
public class DdlTest {

    @Test
    public void testAlterTableComment() {
        AlterTableCommentDto alterTableCommentDto = new AlterTableCommentDto();
        alterTableCommentDto.setTableComment("测试打发撒旦");
        alterTableCommentDto.setTableName("full_column_type_test");
        alterTableCommentDto.setSchema("ods_prod");
        alterTableCommentDto.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
        System.out.println(DatabaseDdlHolder.alterTableCommentSql(alterTableCommentDto));
    }

    @Test
    public void testAlterTableColumnComment() {
        AlterColumnCommentDto alterColumnCommentDto = new AlterColumnCommentDto();
        alterColumnCommentDto.setTableName("full_column_type_test");
        alterColumnCommentDto.setSchema("ods_prod");
        alterColumnCommentDto.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
        alterColumnCommentDto.setColumnCommentDtos(Lists.newArrayList());
        alterColumnCommentDto.getColumnCommentDtos().add(new ColumnCommentDto("emp_no", "的手法对付"));
        alterColumnCommentDto.getColumnCommentDtos().add(new ColumnCommentDto("string", "的手法对付"));
        alterColumnCommentDto.getColumnCommentDtos().add(new ColumnCommentDto("jsonb", "的手法对付"));
        System.out.println(DatabaseDdlHolder.alterTableColumnCommentSql(alterColumnCommentDto));
    }

    @Test
    public void testDorisCreate() {
        DorisDdlBuilder dorisDdlBuilder = new DorisDdlBuilder();
        List<String> list = new ArrayList<>(Arrays.asList("element1", "element2", "element3"));
        CreateDto createDto = new CreateDto();
        createDto.setSchema("schemaName1");
        createDto.setTableName("tableName1");
        createDto.setTableComment("这是建表描述");
        createDto.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
        Properties properties = new Properties();
        properties.setProperty("key1", "value1");
        properties.setProperty("REPLICATION_NUM2", "2");
        properties.setProperty("key12", "aaaa");
        BucketConfigDto bucketConfig = new BucketConfigDto();
        bucketConfig.setBucketColumnList(list).setBucketNum("2");
        PartitionConfigDto partitionConfig = new PartitionConfigDto();
        partitionConfig.setPartitionColumnList(list).setPartitionModel(DorisPartitionModelEnum.RANGE);
        DorisTableConfigDto dorisCreateTableConfig = new DorisTableConfigDto();
        dorisCreateTableConfig.setDataModel(DorisDataModelEnum.UNIQUE).setBucketConfig(bucketConfig).setPartitionConfig(partitionConfig);
        //DorisCreateTableConfig dorisCreateTableConfig = new DorisCreateTableConfig(DorisDataModelEnum.AGGREGATE,bucketConfig,null);
        createDto.setProperties(properties);
        createDto.setDorisConfig(dorisCreateTableConfig);
        ColumnBase c1 = ColumnBase.builder().columnName("col1").primaryKey(true).required(true).comment("上述")
            .columnType(DorisColumnEnum.DORIS_BIGINT).precision(12L).aggregateType(DorisAggregateEnum.MAX).build();
        ColumnBase c2 = ColumnBase.builder().columnName("name2").primaryKey(true).required(false)
            .columnType(DorisColumnEnum.DORIS_DECIMAL).precision(12L).scale(10).aggregateType(DorisAggregateEnum.SUM).build();
        ColumnBase c3 = ColumnBase.builder().columnName("name3").primaryKey(false).required(true).comment("啦啦啦")
            .columnType(DorisColumnEnum.DORIS_VARCHAR).precision(1234L).build();
        List<ColumnBase> columnList = Arrays.asList(c1, c2, c3);
        List<IndexAttrDto> indexAttrDto = new ArrayList<>();
        IndexAttrDto idx1= new IndexAttrDto();
        idx1.setIndexType(DorisIndexTypeEnum.DORIS_INVERTED);
        idx1.setIndexName("IDX1");
        idx1.setColumnNameLists(CollUtil.newArrayList("name"));
//        idx1.setProperties();
        idx1.setIndexComment("c1");
        indexAttrDto.add(idx1);
        createDto.setIndexAttrDtoList(indexAttrDto);
        createDto.setColumnList(columnList);
        String createTableSql = dorisDdlBuilder.createTableSql(createDto);
        System.out.println("生产的SQL: " + createTableSql);
        System.out.println("Doris建表语句：");
//        String tableSql = dorisDdlBuilder.createTableSql(createDto);
        String tableSql = "CREATE TABLE `ods_cus_info_di_test` (\n" +
            "  `cus_id` bigint(20) NOT NULL COMMENT '客户编号',\n" +
            "  `cus_name` varchar(300) NULL COMMENT '客户姓名',\n" +
            "  `age` int(11) NULL COMMENT '客户年龄',\n" +
            "  `address` varchar(300) NULL COMMENT '客户地址',\n" +
            "  `ip` varchar(300) NULL COMMENT '客户网络地址',\n" +
            "  `id_card` varchar(300) NULL COMMENT '客户身份证号',\n" +
            "  `etl_time` datetime NULL COMMENT '记录数据的写入时间',\n" +
            "  `operation_type` int(11) NULL COMMENT '来源表数据操作类型，1代表insert、2代表update、3代表delete'\n" +
            ") ENGINE=OLAP\n" +
            "UNIQUE KEY(`cus_id`)\n" +
            "COMMENT '客户表'\n" +
            "DISTRIBUTED BY HASH(`cus_id`) BUCKETS 5\n" +
            "PROPERTIES (\n" +
            "\"replication_allocation\" = \"tag.location.writer: 1, tag.location.reader: 1\",\n" +
            "\"is_being_synced\" = \"false\",\n" +
            "\"storage_format\" = \"V2\",\n" +
            "\"enable_unique_key_merge_on_write\" = \"true\",\n" +
            "\"light_schema_change\" = \"true\",\n" +
            "\"disable_auto_compaction\" = \"false\",\n" +
            "\"enable_single_replica_compaction\" = \"false\"\n" +
            ");";
        System.out.println(tableSql);


//        DataSourceDto dataSourceDto = new DataSourceDto();
//        dataSourceDto.setUrl("jdbc:mysql://127.0.0.1:19030/ods_prod?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&useSSL=false&rewriteBatchedStatements=true&allowMultiQueries=true");
//        dataSourceDto.setUsername("datakits_query_prod");
//        dataSourceDto.setPassword("cisdi@123456");
//        dataSourceDto.setPort(19030);
//        dataSourceDto.setHost("127.0.0.1");
//        dataSourceDto.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
//        dataSourceDto.setDatabaseDriver("com.mysql.cj.jdbc.Driver");
//        dataSourceDto.setDatasourceName("ods_prod");
//        dataSourceDto.setSchemaName("ods_prod");
//
//        DynamicDataSourceHolder.executeDDL(dataSourceDto, tableSql);
    }

    @Test
    public void testQueryCreate() {

        String sql = "select * from ods_prod.ods_item_info_di limit 10";

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

        List<Map<String, Object>> maps = DynamicDataSourceHolder.queryForList(dataSourceDto, sql);
        //Map<String, Object> maps = DynamicDataSourceHolder.queryForMap(dataSourceDto, sql);

        for (Map<String, Object> map : maps) {

            System.out.println("============================================");
            for (String s : map.keySet()) {
                System.out.println(s + "======" + map.get(s).toString());
            }
        }


    }


    @Test
    public void testMysqlCreate() {
        MysqlDdlBuilder ddlBuilder = new MysqlDdlBuilder();
        List<String> list = new ArrayList<>(Arrays.asList("element1", "element2", "element3"));
        CreateDto createDto = new CreateDto();
        createDto.setSchema("schemaName1");
        createDto.setTableName("tableName1");
        createDto.setTableComment("这是建表描述");
        createDto.setDataBaseTypeEnum(DataBaseTypeEnum.MYSQL);
        ColumnBase c1 = ColumnBase.builder().columnName("col1").primaryKey(true).required(true).comment("上述")
            .columnType(MysqlColumnEnum.MYSQL_BIGINT).precision(12L).build();
        ColumnBase c2 = ColumnBase.builder().columnName("name2").primaryKey(true).required(false)
            .columnType(MysqlColumnEnum.MYSQL_DECIMAL).precision(12L).scale(10).build();
        ColumnBase c3 = ColumnBase.builder().columnName("name3").primaryKey(false).required(true).comment("啦啦啦").defaultValue("morenzhi")
            .columnType(MysqlColumnEnum.MYSQL_VARCHAR).precision(1234L).build();
        List<ColumnBase> columnList = Arrays.asList(c1, c2, c3);
        createDto.setColumnList(columnList);
        System.out.println("mysql建表语句：");
        String tableSql = ddlBuilder.createTableSql(createDto);
        System.out.println(tableSql);


        DataSourceDto dataSourceDto = new DataSourceDto();
        dataSourceDto.setUrl("jdbc:mysql://127.0.0.1:33307/?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&useSSL=false&rewriteBatchedStatements=true&allowMultiQueries=true");
        dataSourceDto.setUsername("dbz_read");
        dataSourceDto.setPassword("Abcd1234!");
        dataSourceDto.setPort(33307);
        dataSourceDto.setHost("127.0.0.1");
        dataSourceDto.setDataBaseTypeEnum(DataBaseTypeEnum.MYSQL);
        dataSourceDto.setDatabaseDriver("com.mysql.cj.jdbc.Driver");
        dataSourceDto.setDatasourceName("test_read");

        DynamicDataSourceHolder.executeDDL(dataSourceDto, tableSql);

    }

    @Test
    public void testDorisAlter() {
        DorisDdlBuilder dorisDdlBuilder = new DorisDdlBuilder();
        AlterDto alterDto = new AlterDto(new TableBase(DataBaseTypeEnum.DORIS, "schemaName", "tableName"));
        List<AlterOperationDto> alterList = new ArrayList<>();
        AlterOperationDto alterOperationDto = new AlterOperationDto();
        List<AlterColumnDto> columnList = new ArrayList<>();

        ColumnBase c1 = ColumnBase.builder().columnName("col1").primaryKey(false).required(true).comment("上述")
            .columnType(DorisColumnEnum.DORIS_BIGINT).precision(12L).aggregateType(DorisAggregateEnum.MAX).build();
        ColumnBase c2 = ColumnBase.builder().columnName("name2").primaryKey(true).required(false)
            .columnType(DorisColumnEnum.DORIS_DECIMAL).precision(12L).scale(10).aggregateType(DorisAggregateEnum.SUM).build();
        ColumnBase c3 = ColumnBase.builder().columnName("name3").primaryKey(false).required(true).comment("啦啦啦")
            .columnType(DorisColumnEnum.DORIS_VARCHAR).precision(1234L).build();

        AlterColumnDto alterColumnDto1 = new AlterColumnDto(c1);
        alterColumnDto1.setFirst(true);
        AlterColumnDto alterColumnDto2 = new AlterColumnDto(c2);
        alterColumnDto2.setAfterColumnName("id");
        AlterColumnDto alterColumnDto3 = new AlterColumnDto(c3);
        columnList.add(alterColumnDto1);
        columnList.add(alterColumnDto2);
        columnList.add(alterColumnDto3);
        alterOperationDto.setAlterEnum(AlterEnum.ADD);
        alterOperationDto.setColumnList(columnList);
        alterList.add(alterOperationDto);
        alterDto.setAlterList(alterList);

        String alterTableSql = dorisDdlBuilder.alterTableSql(alterDto);
        System.out.println(alterTableSql);
    }

    @Test
    public void testMysqlAlter() {
        MysqlDdlBuilder mysqlDdlBuilder = new MysqlDdlBuilder();
        AlterDto alterDto = new AlterDto(new TableBase(DataBaseTypeEnum.MYSQL, "schemaName", "tableName"));
        List<AlterOperationDto> alterList = new ArrayList<>();
        AlterOperationDto alterOperationDto = new AlterOperationDto();
        List<AlterColumnDto> columnList = new ArrayList<>();

        ColumnBase c1 = ColumnBase.builder().columnName("col1").primaryKey(false).required(true).comment("上述")
            .columnType(MysqlColumnEnum.MYSQL_BIGINT).precision(12L).build();
        ColumnBase c2 = ColumnBase.builder().columnName("name2").primaryKey(true).required(false)
            .columnType(MysqlColumnEnum.MYSQL_DECIMAL).precision(12L).scale(10).build();
        ColumnBase c3 = ColumnBase.builder().columnName("name3").primaryKey(false).required(true).comment("啦啦啦")
            .columnType(MysqlColumnEnum.MYSQL_VARCHAR).precision(1234L).build();

        AlterColumnDto alterColumnDto1 = new AlterColumnDto(c1);
        alterColumnDto1.setFirst(true);
        AlterColumnDto alterColumnDto2 = new AlterColumnDto(c2);
        alterColumnDto2.setAfterColumnName("id");
        AlterColumnDto alterColumnDto3 = new AlterColumnDto(c3);
        columnList.add(alterColumnDto1);
        columnList.add(alterColumnDto2);
        columnList.add(alterColumnDto3);
        alterOperationDto.setAlterEnum(AlterEnum.ADD);
        alterOperationDto.setColumnList(columnList);
        alterList.add(alterOperationDto);
        alterDto.setAlterList(alterList);

        String alterTableSql = mysqlDdlBuilder.alterTableSql(alterDto);
        System.out.println(alterTableSql);
    }

    @Test
    public void testIndex() {
        DorisDdlBuilder dorisDdlBuilder = new DorisDdlBuilder();

        CreateIndexDto createIndexDto = new CreateIndexDto();
        createIndexDto.setIndexType(DorisIndexTypeEnum.DORIS_INVERTED);
        createIndexDto.setIndexName("testIndex");
        createIndexDto.setColumnNameLists(CollUtil.newArrayList("name"));
        Properties props = new Properties();
        props.put("parser", "chinese");
        createIndexDto.setProperties(props);
        createIndexDto.setIndexComment("testIndexomment");
        createIndexDto.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
        createIndexDto.setSchema("dev_test_prod");
        createIndexDto.setTableName("xiake_test");
        System.out.println(dorisDdlBuilder.createIndex(createIndexDto));

        DropIndexDto dropIndexDto = new DropIndexDto();
        dropIndexDto.setIndexType(DorisIndexTypeEnum.DORIS_INVERTED);
        dropIndexDto.setIndexName("testIndex");
        dropIndexDto.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
        dropIndexDto.setSchema("dev_test_prod");
        dropIndexDto.setTableName("xiake_test");
        System.out.println(dorisDdlBuilder.dropIndex(dropIndexDto));

        List<IndexAttrDto> createIndexList = new ArrayList<>();

        IndexAttrDto createIndexDto2 = new IndexAttrDto();
        createIndexDto2.setIndexType(DorisIndexTypeEnum.DORIS_INVERTED);
        createIndexDto2.setIndexName("testIndex");
        createIndexDto2.setColumnNameLists(CollUtil.newArrayList("name"));
        Properties props2 = new Properties();
        props2.put("parser", "chinese");
        createIndexDto2.setProperties(props2);
        createIndexDto2.setIndexComment("testIndexomment");
//        createIndexList.add(createIndexDto);
        createIndexList.add(createIndexDto2);
        System.out.println(DatabaseIndexBuilderFactory.getBuilder(dropIndexDto.getIndexType()).createIndexSqlSegment(createIndexList));
    }

    @Test
    public void testAlterIndex() {
        AlterIndexDto alterIndexDto = new AlterIndexDto();
        alterIndexDto.setIndexType(DorisIndexTypeEnum.DORIS_INVERTED);
        alterIndexDto.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
        alterIndexDto.setSchema("ads_prod");
        alterIndexDto.setTableName("jim_test_01");
        List<AlterIndexOperationDto> optList = new ArrayList<>();
        AlterIndexOperationDto o1 = new AlterIndexOperationDto();
        o1.setAlterEnum(AlterEnum.DROP);
        IndexAttrDto indexDto1 = new IndexAttrDto();
        indexDto1.setIndexName("idx1");
        o1.setIndexDto(indexDto1);
        optList.add(o1);

        AlterIndexOperationDto o2 = new AlterIndexOperationDto();
        o2.setAlterEnum(AlterEnum.ADD);
        IndexAttrDto indexDto2 = new IndexAttrDto();
        indexDto2.setIndexName("idx4");
        indexDto2.setIndexComment("idx2_c");
        Properties props2 = new Properties();
        props2.put("parser", "chinese");
        indexDto2.setProperties(props2);
        indexDto2.setIndexType(DorisIndexTypeEnum.DORIS_INVERTED);
        indexDto2.setColumnNameLists(CollUtil.newArrayList("Column6"));
        o2.setIndexDto(indexDto2);
        optList.add(o2);

        AlterIndexOperationDto o4 = new AlterIndexOperationDto();
        o4.setAlterEnum(AlterEnum.ADD);
        IndexAttrDto indexDto4 = new IndexAttrDto();
        indexDto4.setIndexName("idx4");
        indexDto4.setIndexComment("idx4_c");
        indexDto4.setProperties(props2);
        indexDto4.setIndexType(DorisIndexTypeEnum.DORIS_INVERTED);
        indexDto4.setColumnNameLists(CollUtil.newArrayList("Column5"));
        o4.setIndexDto(indexDto4);
        optList.add(o4);


        AlterIndexOperationDto o3 = new AlterIndexOperationDto();
        o3.setAlterEnum(AlterEnum.DROP);
        IndexAttrDto indexDto3 = new IndexAttrDto();
        indexDto3.setIndexName("idx2");
        o3.setIndexDto(indexDto3);
        optList.add(o3);

        alterIndexDto.setAlterIndexOperationDtoList(optList);

        System.out.println(DatabaseDdlHolder.alterTableIndexSql(alterIndexDto));
    }

    @Test
    public void testView() {
        CreateViewDto dto = new CreateViewDto();
        dto.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
        dto.setViewComment("testVIEW");
        dto.setViewName("jim_test_view");
        List<ColumnBase> colList = new ArrayList<>();
        ColumnBase c1 = new ColumnBase();
        c1.setComment("idXX");
        c1.setColumnName("id");
        ColumnBase c2 = new ColumnBase();
        c2.setColumnName("name");
        ColumnBase c3 = new ColumnBase();
        c3.setColumnName("etl_time");
        c3.setComment("ddss");
        colList.add(c1);
        colList.add(c2);
        colList.add(c3);
        dto.setColumnList(colList);
        dto.setSql("SELECT * FROM test_dev_prod.xiake_test");
        System.out.println(DatabaseDdlHolder.createView(dto));

        DropViewDto dropDto = new DropViewDto();
        dropDto.setViewName("xiake_test");
        dropDto.setSchema("test_dev_prod");
        dropDto.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
        System.out.println(DatabaseDdlHolder.dropView(dropDto));

        AlterViewDto alterViewDto = new AlterViewDto();
        alterViewDto.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
        alterViewDto.setSchema("dev_test_prod");
        alterViewDto.setViewName("jim_test_view");
        alterViewDto.setSql("SELECT * FROM test_dev_prod.xiake_test");
        alterViewDto.setColumnList(colList);
        System.out.println(DatabaseDdlHolder.alterView(alterViewDto));

    }

    @Test
    public void testDataSourceCacheMap() {
        DataSourceDto datasourceDto1 = new DataSourceDto();
        datasourceDto1.setSchemaName("dataktis");
        datasourceDto1.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
        datasourceDto1.setUrl("jdbc:mysql://10.106.253.24:3306");
        datasourceDto1.setUsername("dataktis");
        datasourceDto1.setPassword("Cisdi@123456");

        DataSourceDto datasourceDto2 = new DataSourceDto();
        datasourceDto2.setSchemaName("dataktis");
        datasourceDto2.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
        datasourceDto2.setUrl("jdbc:mysql://10.106.253.24:3306");
        datasourceDto2.setUsername("datakits");
        datasourceDto2.setPassword("Cisdi@123456");

        DataSourceDto datasourceDto3 = new DataSourceDto();
//        datasourceDto3.setSchemaName();
        datasourceDto3.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
        datasourceDto3.setUrl("jdbc:mysql://10.106.253.24:3306");
        datasourceDto3.setUsername("datakits");
        datasourceDto3.setPassword("Cisdi@123456");

        DataSourceDto datasourceDto4 = new DataSourceDto();
        datasourceDto4.setSchemaName("collie_test");
        datasourceDto4.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
        datasourceDto4.setUrl("jdbc:mysql://10.106.253.24:3306");
        datasourceDto4.setUsername("datakits");
        datasourceDto4.setPassword("Cisdi@123456");

        JdbcTemplate j1 = DynamicDataSourceHolder.getJdbcTemplate(datasourceDto1);
        JdbcTemplate j2 = DynamicDataSourceHolder.getJdbcTemplate(datasourceDto2);
        Assertions.assertEquals(j1,j2);
        JdbcTemplate j3 = DynamicDataSourceHolder.getJdbcTemplate(datasourceDto3);
        Assertions.assertNotEquals(j1,j3);
        JdbcTemplate j4 = DynamicDataSourceHolder.getJdbcTemplate(datasourceDto4);
        Assertions.assertNotEquals(j3,j4);
        Assertions.assertNotEquals(j1,j4);

    }
}
