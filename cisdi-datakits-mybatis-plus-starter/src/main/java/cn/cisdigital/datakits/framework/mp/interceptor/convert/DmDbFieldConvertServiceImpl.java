package cn.cisdigital.datakits.framework.mp.interceptor.convert;

import com.baomidou.mybatisplus.annotation.DbType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 达梦数据库字段替换接口
 *
 * @author xxx
 * @implNote 将双引号内字段替换为 大写不带引号
 * @since 2023/9/14 9:05
 */
public class DmDbFieldConvertServiceImpl implements DbFieldConvertService {

    /**
     * 将双引号内字段替换为 大写不带引号
     */
    private static final String COMPILE_STR = "\"([^\"]+)\"";

    @Override
    public String convertSql(String sql) {
        Matcher m = Pattern.compile(COMPILE_STR).matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "\"" + m.group(1).toUpperCase() + "\"");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    @Override
    public DbType getDbType() {
        return DbType.DM;
    }
}
