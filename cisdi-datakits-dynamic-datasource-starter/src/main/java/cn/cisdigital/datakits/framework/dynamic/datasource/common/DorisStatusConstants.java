package cn.cisdigital.datakits.framework.dynamic.datasource.common;

import cn.hutool.core.collection.ListUtil;

import java.util.List;

public class DorisStatusConstants {
    /**
     * 超时
     */
    public static final String PUBLISH_TIMEOUT = "Publish Timeout";
    /**
     * 成功
     */
    public static final String SUCCESS = "Success";
    /**
     * 被视为成功的状态列表
     */
    public static final List<String> SUCCESS_STATUS_LIST = ListUtil.toList(SUCCESS, PUBLISH_TIMEOUT);
}
