package cn.cisdigital.datakits.framework.dynamic.datasource.parser;

import cn.cisdigital.datakits.framework.dynamic.datasource.database.executor.jdbc.DruidSimpleSqlParser;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author xxx
 */
public class DruidSimpleSqlParserTest {

    public static final int DEFAULT_ROW_COUNT = 1000;

    @Test
    void modifyLimit_pageQuery() {
        String sql = "select t1.identity from bz_digit_rolling_billet1 t1 inner join bz_digit_rolling_billet2 t2 order by t1.identity desc, t2.id asc limit 1, 10000";
        String handledSql = DruidSimpleSqlParser.modifyOrAppendLimit(sql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL);
        System.out.println(handledSql);
    }

    @Test
    void modifyLimit_sqlJoin() {
        String sql = "select t1.identity from bz_digit_rolling_billet1 t1 inner join bz_digit_rolling_billet2 t2 order by t1.identity desc, t2.id asc limit 10000";
        String handledSql = DruidSimpleSqlParser.modifyOrAppendLimit(sql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL);
        System.out.println(handledSql);
    }

    @Test
    void appendLimit_sqlJoin() {
        String sql = "select t1.identity from bz_digit_rolling_billet1 t1 inner join bz_digit_rolling_billet2 t2 order by t1.identity desc, t2.id asc";
        String handledSql = DruidSimpleSqlParser.modifyOrAppendLimit(sql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL);
        System.out.println(handledSql);
    }

    @Test
    void modifyLimit_nestedSql() {
        String sql = "select * from (select t1.identity from bz_digit_rolling_billet1 t1 inner join bz_digit_rolling_billet2 t2 order by t1.identity desc, t2.id asc) as t limit 10000";
        String handledSql = DruidSimpleSqlParser.modifyOrAppendLimit(sql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL);
        System.out.println(handledSql);
    }

    @Test
    void appendLimit_nestedSql() {
        String sql = "select * from (select t1.identity from bz_digit_rolling_billet1 t1 inner join bz_digit_rolling_billet2 t2 order by t1.identity desc, t2.id asc) as t";
        String handledSql = DruidSimpleSqlParser.modifyOrAppendLimit(sql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL);
        System.out.println(handledSql);
    }

    @Test
    void appendLimit_nestedLimit() {
        String sql = "select * from (select t1.identity from bz_digit_rolling_billet1 t1 inner join bz_digit_rolling_billet2 t2 order by t1.identity desc, t2.id asc limit 10) as t";
        String handledSql = DruidSimpleSqlParser.modifyOrAppendLimit(sql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL);
        System.out.println(handledSql);
    }

    @Test
    void appendLimit_nestedLimitOverMax() {
        String sql = "select \n" +
                "    * \n" +
                "from \n" +
                "    ods_prod.ods_test_stream_load_delete_first_20241104_for_one_week_99 as qq \n" +
                "    left join ( \n" +
                "        select \n" +
                "            * \n" +
                "        from \n" +
                "            ods_prod.ods_test_stream_load_delete_first_20241104_for_one_week_98 \n" +
                "    ) as qqq on qq.id = qqq.id limit 30000" ;
        String handledSql = DruidSimpleSqlParser.modifyOrAppendLimit(sql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL);
        System.out.println(handledSql);
        String target = "SELECT *\n" +
                "FROM ods_prod.ods_test_stream_load_delete_first_20241104_for_one_week_99 qq\n" +
                "\tLEFT JOIN (\n" +
                "\t\tSELECT *\n" +
                "\t\tFROM ods_prod.ods_test_stream_load_delete_first_20241104_for_one_week_98\n" +
                "\t) qqq\n" +
                "\tON qq.id = qqq.id\n" +
                "LIMIT 1000;";
        Assertions.assertTrue(target.trim().equalsIgnoreCase(handledSql.trim()));
        Assertions.assertTrue(handledSql.contains("LIMIT 1000"));
        Assertions.assertFalse(handledSql.contains("30000"));
    }
    @Test
    void testUnionAllMaxLimit(){
        String sql = "select * from table1 \n" +
                "\n" +
                "union\n" +
                "\n" +
                "select * from table2";
        String handledSql = DruidSimpleSqlParser.modifyOrAppendLimit(sql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL);
        System.out.println(handledSql);
        String target = "SELECT *\n" +
                "FROM table1\n" +
                "LIMIT 1000\n" +
                "UNION\n" +
                "(SELECT *\n" +
                "FROM table2\n" +
                "LIMIT 1000);";
        Assertions.assertTrue(target.trim().equalsIgnoreCase(handledSql.trim()));
        Assertions.assertTrue(handledSql.contains("LIMIT 1000"));
    }

    @Test
    public void testBracket() {
        String sql = "SELECT NOW(）";
        Assertions.assertThrows(ParserException.class, () -> DruidSimpleSqlParser.modifyOrAppendLimit(sql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL));
        String correctSql = "SELECT NOW()";
        String handledSql = DruidSimpleSqlParser.modifyOrAppendLimit(correctSql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL);
        System.out.println(handledSql);
        Assertions.assertEquals(handledSql.trim(),"SELECT NOW()\n" + "LIMIT 1000;".trim());
    }

    @Test
    public void testBracketWithWhiteSpace() {
        String sql = "SELECT NOW （)";
        Assertions.assertThrows(ParserException.class, () -> DruidSimpleSqlParser.modifyOrAppendLimit(sql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL));
        String correctSql = "SELECT NOW()";
        String handledSql = DruidSimpleSqlParser.modifyOrAppendLimit(correctSql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL);
        System.out.println(handledSql);
        Assertions.assertEquals(handledSql.trim(),"SELECT NOW()\n" + "LIMIT 1000;".trim());
    }

    @Test
    public void testLeftBracket() {
        String sql = "SELECT NOW（)";
        Assertions.assertThrows(ParserException.class, () -> DruidSimpleSqlParser.modifyOrAppendLimit(sql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL));
        String correctSql = "SELECT NOW()";
        String handledSql = DruidSimpleSqlParser.modifyOrAppendLimit(correctSql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL);
        System.out.println(handledSql);
        Assertions.assertEquals(handledSql.trim(),"SELECT NOW()\n" + "LIMIT 1000;".trim());
    }


    @Test
    void testUnionMulAllMaxLimit(){
        String sql = "select * from table1 \n" +
                "\n" +
                "union\n" +
                "\n" +
                "select * from table2\n" +
                "union \n" +
                "select * from table3";
        String handledSql = DruidSimpleSqlParser.modifyOrAppendLimit(sql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL);
        System.out.println(handledSql);
        String target = "SELECT *\n" +
                "FROM table1\n" +
                "LIMIT 1000\n" +
                "UNION\n" +
                "(SELECT *\n" +
                "FROM table2\n" +
                "LIMIT 1000)\n" +
                "UNION\n" +
                "(SELECT *\n" +
                "FROM table3\n" +
                "LIMIT 1000);";
        Assertions.assertTrue(target.trim().equalsIgnoreCase(handledSql.trim()));
        Assertions.assertTrue(handledSql.contains("LIMIT 1000"));
    }
    @Test
    public void testAggMaxLimit(){
        String sql = "select table1.*, (select sum(table2.money) totalMoney from table2 where table2.id = table1.id ) from table1";
        String handledSql = DruidSimpleSqlParser.modifyOrAppendLimit(sql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL);
        System.out.println(handledSql);
    }


    @Test
    void appendLimit_multiSQL() {
        String sql = "select t1.identity from bz_digit_rolling_billet1 t1 inner join bz_digit_rolling_billet2 t2 order by t1.identity desc, t2.id asc limit 10000;select t1.identity from bz_digit_rolling_billet1 t1 inner join bz_digit_rolling_billet2 t2 order by t1.identity desc, t2.id asc";
        String handledSql = DruidSimpleSqlParser.modifyOrAppendLimit(sql, DEFAULT_ROW_COUNT, JdbcConstants.MYSQL);
        System.out.println(handledSql);
    }

    /**
     * 此方案意义不大, 即便解析出来了排序字段, 拿到外层去也会因为上下文不同导致表别名不可用
     * select * from (select t1.identity from bz_digit_rolling_billet1 t1 inner join bz_digit_rolling_billet2 t2 order by t1.identity desc, t2.id asc) order by t1.identity desc, t2.id asc limit 10;
     */
//    @Test
    void parseOrderByStatement_sqlJoin() {
        String sql = "select t1.identity from bz_digit_rolling_billet1 t1 inner join bz_digit_rolling_billet2 t2 order by t1.identity desc, t2.id asc";
        String dbType = JdbcConstants.MYSQL.name();
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement sqlStatement = statementList.get(0);
        sqlStatement.accept(new MySqlASTVisitorAdapter(){
            @Override
            public boolean visit(SQLOrderBy x) {
                System.out.println(x.getItems());
                return super.visit(x);
            }
        });
    }

    @Test
    void validateDropStatement() {
        String sql = "DROP /***/ database a;";
        Lexer sqlLexer = new DruidSimpleSqlParser().getLexer(sql);
        Assertions.assertThrows(RuntimeException.class, () ->{
            Token sqlTypeToken = sqlLexer.token();
            if (sqlTypeToken == Token.DROP) {
                while (sqlTypeToken != Token.EOF) {
                    if (sqlTypeToken == Token.DATABASE) {
                        throw new RuntimeException("检测到drop database语句");
                    }
                    sqlLexer.nextTokenValue();
                    sqlTypeToken = sqlLexer.token();
                }
            }
        });
    }

    @Test
    public void testModifyComplexUiononSql(){
        String sql ="select a.*, org_cn_nm, parent_b00, path_b00, path_nm, path_abbr, '总师、总助级管理人员' as type\n" +
                "from dw_prod.DWS_HR_CADRE_ROSTER a\n" +
                "left join (select b00, org_cn_nm, parent_b00, org_cn_abbr, org_cd, path_b00, path_nm, path_abbr, biz_date from dw_prod.DWS_HR_CADRE_B00_PATH) b on a.b00 = b.b00\n" +
                "where array_contains(department, '总师、总助级管理人员') and current_status = '在职'\n" +
                "UNION ALL\n" +
                "select a.*, org_cn_nm, parent_b00, path_b00, path_nm, path_abbr,'党政领导班子、董监事会、纪委' as type\n" +
                "from dw_prod.DWS_HR_CADRE_ROSTER a\n" +
                "left join (select b00, org_cn_nm, parent_b00, org_cn_abbr, org_cd, path_b00, path_nm, path_abbr, biz_date from dw_prod.DWS_HR_CADRE_B00_PATH) b on a.b00 = b.b00\n" +
                "where array_contains(department, '党政领导班子、董监事会、纪委') and current_status = '在职'\n" +
                "  and array_contains(b00_list, 'B6A33EF1-7093-A651-EA33-8BA0417D3B14') and holds_position = 1\n" +
                "UNION ALL\n" +
                "select a.*, org_cn_nm, parent_b00, path_b00, path_nm, path_abbr,'总部机构' as type\n" +
                "from dw_prod.DWS_HR_CADRE_ROSTER as a inner join (select b00, org_cn_nm, parent_b00, org_cn_abbr, org_cd, path_b00, path_nm, path_abbr, biz_date from dw_prod.DWS_HR_CADRE_B00_PATH) as p\n" +
                "  on array_contains(a.b00_list, p.b00)\n" +
                "where path_abbr[-3] = '总部机构' and current_status = '在职'\n" +
                "UNION ALL\n" +
                "select a.*, org_cn_nm, parent_b00, path_b00, path_nm, path_abbr, '中冶集团' as type\n" +
                "from dw_prod.DWS_HR_CADRE_ROSTER as a inner join (select b00, org_cn_nm, parent_b00, org_cn_abbr, org_cd, path_b00, path_nm,\n" +
                "path_abbr, biz_date from dw_prod.DWS_HR_CADRE_B00_PATH) as p\n" +
                "  on array_contains(a.b00_list, p.b00)\n" +
                "where path_abbr[3] = '中冶集团' and current_status = '在职' LIMIT 0,100";

        String targetSql = DruidSimpleSqlParser.modifyOrAppendLimit(sql, 1000);
        System.out.println(targetSql);
        Assertions.assertEquals(targetSql,"SELECT a.*, org_cn_nm, parent_b00, path_b00, path_nm\n" +
                "\t, path_abbr, '总师、总助级管理人员' AS type\n" +
                "FROM dw_prod.DWS_HR_CADRE_ROSTER a\n" +
                "\tLEFT JOIN (\n" +
                "\t\tSELECT b00, org_cn_nm, parent_b00, org_cn_abbr, org_cd\n" +
                "\t\t\t, path_b00, path_nm, path_abbr, biz_date\n" +
                "\t\tFROM dw_prod.DWS_HR_CADRE_B00_PATH\n" +
                "\t) b\n" +
                "\tON a.b00 = b.b00\n" +
                "WHERE array_contains(department, '总师、总助级管理人员')\n" +
                "\tAND current_status = '在职'\n" +
                "LIMIT 1000\n" +
                "UNION ALL\n" +
                "(SELECT a.*, org_cn_nm, parent_b00, path_b00, path_nm\n" +
                "\t, path_abbr, '党政领导班子、董监事会、纪委' AS type\n" +
                "FROM dw_prod.DWS_HR_CADRE_ROSTER a\n" +
                "\tLEFT JOIN (\n" +
                "\t\tSELECT b00, org_cn_nm, parent_b00, org_cn_abbr, org_cd\n" +
                "\t\t\t, path_b00, path_nm, path_abbr, biz_date\n" +
                "\t\tFROM dw_prod.DWS_HR_CADRE_B00_PATH\n" +
                "\t) b\n" +
                "\tON a.b00 = b.b00\n" +
                "WHERE array_contains(department, '党政领导班子、董监事会、纪委')\n" +
                "\tAND current_status = '在职'\n" +
                "\tAND array_contains(b00_list, 'B6A33EF1-7093-A651-EA33-8BA0417D3B14')\n" +
                "\tAND holds_position = 1\n" +
                "LIMIT 1000)\n" +
                "UNION ALL\n" +
                "(SELECT a.*, org_cn_nm, parent_b00, path_b00, path_nm\n" +
                "\t, path_abbr, '总部机构' AS type\n" +
                "FROM dw_prod.DWS_HR_CADRE_ROSTER a\n" +
                "\tINNER JOIN (\n" +
                "\t\tSELECT b00, org_cn_nm, parent_b00, org_cn_abbr, org_cd\n" +
                "\t\t\t, path_b00, path_nm, path_abbr, biz_date\n" +
                "\t\tFROM dw_prod.DWS_HR_CADRE_B00_PATH\n" +
                "\t) p\n" +
                "\tON array_contains(a.b00_list, p.b00)\n" +
                "WHERE path_abbr[-3] = '总部机构'\n" +
                "\tAND current_status = '在职'\n" +
                "LIMIT 1000)\n" +
                "UNION ALL\n" +
                "(SELECT a.*, org_cn_nm, parent_b00, path_b00, path_nm\n" +
                "\t, path_abbr, '中冶集团' AS type\n" +
                "FROM dw_prod.DWS_HR_CADRE_ROSTER a\n" +
                "\tINNER JOIN (\n" +
                "\t\tSELECT b00, org_cn_nm, parent_b00, org_cn_abbr, org_cd\n" +
                "\t\t\t, path_b00, path_nm, path_abbr, biz_date\n" +
                "\t\tFROM dw_prod.DWS_HR_CADRE_B00_PATH\n" +
                "\t) p\n" +
                "\tON array_contains(a.b00_list, p.b00)\n" +
                "WHERE path_abbr[3] = '中冶集团'\n" +
                "\tAND current_status = '在职'\n" +
                "LIMIT 1000)\n" +
                "LIMIT 0, 100;\n");
    }

}
