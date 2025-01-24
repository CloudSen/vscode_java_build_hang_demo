package cn.cisdigital.datakits.framework.util.qbeeopensdk.vo;

import im.qingtui.qbee.open.platfrom.auth.model.vo.menu.EmployeeMenuTree;
import java.io.Serializable;
import lombok.Data;

/**
 * @author xxx
 * @since 2024/3/28 11:12
 **/
@Data
public class SdkMenuVo implements Serializable {

    /**
     * 菜单ID
     */
    private String menuId;

    /**
     * 菜单名
     */
    private String name;

    /**
     * 子系统
     */
    private String subSystem;

    /**
     * 连接
     */
    private String linkUrl;

    /**
     * 编码
     */
    private String code;

    public static SdkMenuVo employeeMenuTree2SdkMenuTreeVo(EmployeeMenuTree dto) {
        SdkMenuVo sdkMenuVo = new SdkMenuVo();
        sdkMenuVo.setMenuId(dto.getMenuId());
        sdkMenuVo.setName(dto.getName());
        sdkMenuVo.setSubSystem(dto.getSubSystem());
        sdkMenuVo.setLinkUrl(dto.getLinkUrl());
        sdkMenuVo.setCode(dto.getCode());
        return sdkMenuVo;
    }
}
