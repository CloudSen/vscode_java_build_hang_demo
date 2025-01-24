package cn.cisdigital.datakits.framework.common.identity;

import cn.cisdigital.datakits.framework.common.util.IdentifierGenerator;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 从请求头中提取出来的用户标识信息
 *
 * @author xxx
 */
@Data
@Accessors(chain = true)
public class UserIdentity {

    /**
     * 请求链路id, 部分非client发出的请求解析出来可能为null(如: 定时任务发起的请求)
     */
    private String requestId;

    /**
     * 开放应用ID，非开放应用发出的请求解析出来为null(如: 数据服务应用)
     */
    private String appId;

    /**
     * 开放应用KEY，非开放应用发出的请求解析出来为null(如: 数据服务应用)
     */
    private String appKey;

    /**
     * 平台用户id，非鉴权接口, 解析出来可能为null
     */
    private String userId;

    /**
     * 平台团队id，非鉴权接口 / 平台未启用团队信息 时解析出来可能为null
     */
    private String partyId;

    /**
     * 平台员工id，非鉴权接口解析出来可能为null
     */
    private String employeeId;

    /**
     * 平台员工姓名，非鉴权接口解析出来可能为null
     */
    private String employeeName;

    /**
     * 平台登录帐号，非鉴权接口解析出来可能为null
     */
    private String account;

    /**
     * 是否为超级管理员
     */
    private boolean admin;

    /**
     * 调用方服务名
     */
    private String applicationName;

    public static String generateRequestId() {
        return IdentifierGenerator.generateRequestCode();
    }
}
