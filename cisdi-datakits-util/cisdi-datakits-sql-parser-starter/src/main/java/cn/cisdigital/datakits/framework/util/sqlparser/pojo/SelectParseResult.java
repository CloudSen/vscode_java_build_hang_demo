package cn.cisdigital.datakits.framework.util.sqlparser.pojo;


import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Copyright (C), 2019-2022, 中冶赛迪重庆信息技术有限公司
 * <p>
 * ClassName： ParseResult
 * <p>
 * Description：SELECT解析结果
 *
 * @author xxx
 * @version 1.0.0
 * @date 2022/7/15 19:02
 */
@Data
public class SelectParseResult {
    /**
     * 字段解析结果
     */
    @NonNull
    private List<FieldParseResult> fieldParseResults = Lists.newArrayList();

}
