package cn.cisdigital.datakits.framework.mp.handler;

import cn.cisdigital.datakits.framework.common.identity.AuthHolder;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.LocalDateTime;
import org.apache.ibatis.reflection.MetaObject;

/**
 * @author xxx
 * @since 2024-03-11
 */
public class CustomMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasSetter("version")) {
            this.strictInsertFill(metaObject, "version", () -> 0L, Long.class);
        }
        if (metaObject.hasSetter("createTime")) {
            this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        }
        if (metaObject.hasSetter("updateTime")) {
            this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        }
        if (metaObject.hasSetter("createBy")) {
            this.strictInsertFill(metaObject, "createBy", AuthHolder::getEmployeeId, String.class);
        }
        if (metaObject.hasSetter("updateBy")) {
            this.strictInsertFill(metaObject, "updateBy", AuthHolder::getEmployeeId, String.class);
        }
        if (metaObject.hasSetter("updateByName")) {
            this.strictInsertFill(metaObject, "updateByName", AuthHolder::getEmployeeName, String.class);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.hasSetter("updateTime")) {
            this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        }
        if (metaObject.hasSetter("updateBy")) {
            this.setFieldValByName("updateBy", AuthHolder.getEmployeeId(), metaObject);
        }
        if (metaObject.hasSetter("updateByName")) {
            this.setFieldValByName("updateByName", AuthHolder.getEmployeeName(), metaObject);
        }
    }
}
