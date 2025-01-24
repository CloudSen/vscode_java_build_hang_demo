package cn.cisdigital.datakits.framework.util.sqlparser;

import cn.cisdigital.datakits.framework.common.TestApplication;
import cn.cisdigital.datakits.framework.common.util.JsonUtils;
import cn.cisdigital.datakits.framework.model.dto.DataSourceDto;
import cn.cisdigital.datakits.framework.model.enums.DataBaseTypeEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.enums.ParseOperationEnum;
import cn.cisdigital.datakits.framework.util.sqlparser.parser.ParserContext;
import cn.cisdigital.datakits.framework.util.sqlparser.parser.ParserContextManager;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.LineageParseResult;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.OperationParseResult;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.ParseSqlParam;
import cn.cisdigital.datakits.framework.util.sqlparser.pojo.SelectParseResult;
import cn.cisdigital.datakits.framework.util.sqlparser.util.Convertor;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author xxx
 * @since 2024/4/22 13:41
 */
@SpringBootTest(classes = TestApplication.class)
//@TestPropertySource("classpath:saffron.properties")
public class SqlServiceTest {

    @Autowired
    private ISqlParserService parserService;
    @Autowired
    private ParserContextManager parserContextManager;

    private static DataSourceDto dataSourceDto;
    private static DataSourceDto dataSourceDto2;


    private static String pattern = "^\\s*(USE|SHOW|SELECT|INSERT|UPDATE|UPSHERT|DELETE|DROP|TRUNCATE)\\b";

    private static Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

    private static String regex(String sql) {
        Matcher matcher = regex.matcher(sql);

        if (matcher.find()) {
            String sqlType = matcher.group(1);
            System.out.println("SQL Type: " + sqlType);
            return sqlType;
        } else {
            System.out.println("Unable to extract SQL type.");
            return "";
        }
    }

    @BeforeAll
    public static void init() {
        dataSourceDto = new DataSourceDto();
        dataSourceDto.setHost("10.106.251.120");
        dataSourceDto.setPort(9030);
        dataSourceDto.setUsername("root");
        dataSourceDto.setPassword("");
        dataSourceDto.setDatabaseDriver("com.mysql.cj.jdbc.Driver");
        dataSourceDto.setUrl("jdbc:mysql://10.106.251.120:9030/dw");
        dataSourceDto.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
//        dataSourceDto.setSchemaName("dw");

        dataSourceDto2 = new DataSourceDto();
        dataSourceDto2.setHost("10.73.13.55");
        dataSourceDto2.setPort(9030);
        dataSourceDto2.setUsername("root");
        dataSourceDto2.setPassword("cisdi123456");
        dataSourceDto2.setDatabaseDriver("com.mysql.cj.jdbc.Driver");
        dataSourceDto2.setUrl("jdbc:mysql://10.73.13.52:9030/dw");
        dataSourceDto2.setDataBaseTypeEnum(DataBaseTypeEnum.DORIS);
        dataSourceDto2.setSchemaName("dw");
    }

    private final static String SELECT_LARGEINT = "SELECT * FROM insert_t6 where v='打发士大夫' LIMIT 0,1";
    private final static String SELECT_DATETIME = "select \n" +
            "    * \n" +
            "from \n" +
            "dw_dev.dy_test_NewTable2";
    private final static String SELECT_LARGEINT_LIMIT = "select\n" +
            "  *\n" +
            "from\n" +
            "  dw_dev.sssc_t1 t1\n" +
            "  LEFT JOIN dw_dev.sddsdssd_hello t2 ON t1.id = t2.id\n" +
            "  limit 0,1";
    private final static String SELECT_BIGINT = "SELECT * FROM dw.insert_t7 where v='打发士大夫'";
    private final static String TABLE_NOT_EXISTS = "SELECT * FROM dw.DB2_DORIS1222";
    private final static String VIEW = "SELECT * FROM type_test2_v ORDER BY id";
    private final static String CREATE = "CREATE TABLE `etl_testa` (\n" +
            "  `id` bigint(20) NULL,\n" +
            "  `name` varchar(30) NULL,\n" +
            "  `columna` varchar(30) NULL,\n" +
            "  `columnb` varchar(30) NULL,\n" +
            "  `date` date NULL,\n" +
            "  `datetime` datetime NULL,\n" +
            "  `timestamp` datetime NULL\n" +
            ") ENGINE=OLAP\n" +
            "UNIQUE KEY(`id`)\n" +
            "COMMENT 'OLAP'\n" +
            "DISTRIBUTED BY HASH(`id`) BUCKETS 1\n" +
            "PROPERTIES (\n" +
            "\"replication_allocation\" = \"tag.location.default: 3\",\n" +
            "\"in_memory\" = \"false\",\n" +
            "\"storage_format\" = \"V2\",\n" +
            "\"disable_auto_compaction\" = \"false\"\n" +
            ");";
    private final static String SELECT = "SELECT * FROM type_test2_v where id = '打发士大夫' ORDER BY id";
    private final static String SELECT_FUNCTION1 = "SELECT date_format('1987-01-31','YYYY') as `date`";

    private final static String SELECT_FUNCTION2 = "SELECT YEAR('1987-01-31') as `year`";
    private final static String SELECT_FUNCTION3 = "SELECT day('1987-01-31') as `day`";
    private final static String SELECT_FUNCTION4 = "SELECT extract(year from '2022-09-22 17:01:30')";
    private final static String SELECT_FUNCTION5 = "SELECT extract(hour from '2022-09-22 17:01:30')";
    private final static String SELECT_FUNCTION6 = "SELECT extract(minute from '2022-09-22 17:01:30')";
    private final static String SELECT_FUNCTION7 = "SELECT extract(second from '2022-09-22 17:01:30')";
    private final static String SELECT_FUNCTION8 = "SELECT MAX(15)";
    private final static String DROP = "DROP TABLE aa.bb";
    private final static String TRUNCATE = "DROP TABLE aa.bb";
    private final static String ALTER_SQL = "ALTER TABLE example_db.my_table\n" +
            "ADD COLUMN new_col INT KEY DEFAULT \"0\" AFTER key_1;";

    private final static String CREATE_DB = "CREATE DATABASE HH";
    private final static String CREATE_VIEW = "CREATE VIEW ww.HH AS SELECT * FROM AA";
    private final static String UPDATE = "update ddd.tesTC set name = 'cc' where id = 1;";
    private final static String USE = "use dbddd";
    private final static String CREATE_TABLE = "CREATE TABLE `qrtz_scheduler_state` (\n" +
            "  `SCHED_NAME` varchar(120) NOT NULL,\n" +
            "  `INSTANCE_NAME` varchar(200) NOT NULL,\n" +
            "  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,\n" +
            "  `CHECKIN_INTERVAL` bigint(13) NOT NULL,\n" +
            "  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`) USING BTREE\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;";

    private final static String deleteTable = "DELETE FROM ddd.tesTC  where id = 1;";
    private final static String INSERT = "insert into dw_ods.insert_t1 select * from dw.insert_t3";
    private final static String INSERT2 = "insert into insert_t1 (`bigint_1`,varchar_1) select b,'大师傅士大夫' from dw.insert_t3";
    private final static String INSERT3 = "insert into dw_ods.insert_t1 select * from dw.insert_t3";
    private final static String INSERT_VALUES = "insert into dw_ods.insert_t1 values (1,2,'3')";


    @Test
    public void unDatabaseTest() throws JsonProcessingException {
        String var = "{\"dataSourceDto\":{\"dataBaseTypeEnum\":\"3\",\"databaseDriver\":\"com.mysql.cj.jdbc.Driver\",\"dorisFePort\":8030,\"host\":\"10.106.251.108\",\"password\":\"cisdi@123456\",\"port\":9030,\"schemaName\":\"ads_prod\",\"url\":\"jdbc:mysql://10.106.251.108:9030?rewriteBatchedStatements=true\",\"username\":\"datakits_prod\"},\"sql\":\"select id,etl_time from test_decimal_compare3 limit 20\"}";
        final ParseSqlParam param = JsonUtils.parseObject(var, ParseSqlParam.class);
        final SelectParseResult selectParseResult = parserService.parseSelect(param, true);
        assertNotNull(selectParseResult);
    }

    @Test
    public void functionTest() {
        ParseSqlParam param = new ParseSqlParam();
        param.setDataSourceDto(dataSourceDto);
        param.setSql(SELECT_FUNCTION2);
        final SelectParseResult selectParseResult = parserService.parseSelect(param, false);

        param.setSql(SELECT_FUNCTION7);
        final SelectParseResult selectParseResult2 = parserService.parseSelect(param, false);

        assertNotNull(selectParseResult);
    }

    @Test
    public void expireTimeTest() throws InterruptedException {
        final Optional<ParserContext> catalogParserContext1 = parserContextManager.getCatalogParserContext(Convertor.fromDataSourceDto(dataSourceDto));

        Thread.sleep(60000l);

        final Optional<ParserContext> catalogParserContext = parserContextManager.getCatalogParserContext(Convertor.fromDataSourceDto(dataSourceDto2));

        assertNotNull(catalogParserContext);
    }

    @Test
    public void insertTest() throws InterruptedException {
        ParseSqlParam param = new ParseSqlParam();
        param.setDataSourceDto(dataSourceDto);
        param.setSql(INSERT2);
        final LineageParseResult lineageParseResult = parserService.parseLineage(param);

        assertNotNull(lineageParseResult);
    }


    @Test
    public void alterTest() {
        ParseSqlParam param = new ParseSqlParam();
        param.setDataSourceDto(dataSourceDto2);
        param.setSql(SELECT);
        final OperationParseResult result = parserService.parseOperation(param);

        assertNotNull(result);
    }

    @Test
    public void useTest() {
        ParseSqlParam param = new ParseSqlParam();
        param.setDataSourceDto(dataSourceDto);
        param.setSql(USE);
        final OperationParseResult result = parserService.parseOperation(param);

        assertNotNull(result);
    }

    @Test
    public void updateTest() {
        ParseSqlParam param = new ParseSqlParam();
        param.setDataSourceDto(dataSourceDto);
        param.setSql(UPDATE);
        final OperationParseResult result = parserService.parseOperation(param);

        assertNotNull(result);
    }

    @Test
    public void deleteTest() {
        ParseSqlParam param = new ParseSqlParam();
        param.setDataSourceDto(dataSourceDto);
        param.setSql(deleteTable);
        final OperationParseResult result = parserService.parseOperation(param);

        assertNotNull(result);
    }

    @Test
    public void parseOperatorTest() {
        ParseSqlParam param = new ParseSqlParam();
        param.setDataSourceDto(dataSourceDto);
        param.setSql(CREATE_VIEW);
        final OperationParseResult result = parserService.parseOperation(param);

        assertNotNull(result);
    }

    @Test
    public void createTableTest() {
        ParseSqlParam param = new ParseSqlParam();
        param.setDataSourceDto(dataSourceDto);
        param.setSql(CREATE);
        final OperationParseResult result = parserService.parseOperation(param);

        assertNotNull(result);
    }

    @Test
    public void alterTableTest() {
        ParseSqlParam param = new ParseSqlParam();
        param.setDataSourceDto(dataSourceDto);
        param.setSql(ALTER_SQL);
        final OperationParseResult result = parserService.parseOperation(param);

        assertNotNull(result);
    }

    @Test
    public void parseSelectLimitTest() {
        ParseSqlParam param = new ParseSqlParam();
        param.setDataSourceDto(dataSourceDto);
        param.setSql(SELECT_DATETIME);
        final SelectParseResult selectParseResult = parserService.parseSelect(param, true);


        assertFalse(selectParseResult.getFieldParseResults().isEmpty());
    }

    @Test
    public void parseSelectTest() {
        ParseSqlParam param = new ParseSqlParam();
        param.setDataSourceDto(dataSourceDto);
        param.setSql(SELECT_LARGEINT);
        final SelectParseResult selectParseResult = parserService.parseSelect(param, true);

        ParseSqlParam param2 = new ParseSqlParam();
        param2.setDataSourceDto(dataSourceDto);
        param2.setSql(SELECT_BIGINT);
        final SelectParseResult selectParseResult2 = parserService.parseSelect(param2, true);

        assertFalse(selectParseResult.getFieldParseResults().isEmpty());
    }

    @Test
    public void ddlTest() {
        ParseSqlParam param = new ParseSqlParam();
        param.setDataSourceDto(dataSourceDto);
        param.setSql(CREATE);
        final ParseOperationEnum parseOperationEnum = parserService.checkSqlOperation(param);

        assertNotNull(parseOperationEnum);
    }


}
