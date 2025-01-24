package cn.cisdigital.datakits.framework.mp.interceptor;

import cn.cisdigital.datakits.framework.mp.interceptor.convert.DbFieldConvertService;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import java.sql.Connection;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 字段转换SQL拦截器
 * <p>基于MybatisPlusInterceptor拦截器</p>
 * <p>实现InnerInterceptor拦截器接口</p>
 * <p>1.实现beforeQuery() 执行查询前拦截</p>
 * <p>2.实现beforePrepare() 执行变更前拦截,包括增,删,改</p>
 *
 * @author xxx
 */
@Slf4j
public class MyBatisFieldInterceptor implements InnerInterceptor {

    private final DbFieldConvertService service;

    public MyBatisFieldInterceptor(DbFieldConvertService service) {
        this.service = service;
    }

    /**
     * 针对查询, 直接重写
     *
     * @param executor executor
     * @param statement statement
     * @param parameter parameter
     * @param rowBounds rowBounds
     * @param resultHandler resultHandler
     * @param boundSql boundSql
     */
    @Override
    public void beforeQuery(Executor executor, MappedStatement statement, Object parameter, RowBounds rowBounds,
        ResultHandler resultHandler, BoundSql boundSql) {
        String sql = boundSql.getSql();
        String newSql = service.convertSql(sql);
        if (!sql.equals(newSql)) {
            //重新构建boundSql
            PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
            mpBs.sql(newSql);
        }
    }

    /**
     * 针对增,删,改的情况. 再预处理参数的时候, 进行sql替换
     *
     * @param sh StatementHandler
     * @param connection connection
     * @param transactionTimeout transactionTimeout
     */
    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpStatementHandler = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpStatementHandler.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        //只针对增,删,改 进行修改
        switch (sct) {
            case INSERT:
            case DELETE:
            case UPDATE:
                PluginUtils.MPBoundSql mpBs = mpStatementHandler.mPBoundSql();
                String sql = mpBs.sql();
                String newSql = service.convertSql(sql);
                if (!sql.equals(newSql)) {
                    mpBs.sql(newSql);
                }
                break;
            default:
        }
    }
}
