package cn.cisdigital.datakits.framework.util.qbeeopensdk.service;


import cn.cisdigital.datakits.framework.util.qbeeopensdk.vo.SdkMenuVo;

/**
 * @author xxx
 * @since 2024/3/28 11:04
 **/
public interface SdkMenuService {


    /**
     * 根据当前用户与连接搜索菜单
     *
     * @param subSystem
     * @param linkUrl
     * @return
     */
    SdkMenuVo searchMenu(String employeeId, String subSystem, String linkUrl, Integer type);
}
