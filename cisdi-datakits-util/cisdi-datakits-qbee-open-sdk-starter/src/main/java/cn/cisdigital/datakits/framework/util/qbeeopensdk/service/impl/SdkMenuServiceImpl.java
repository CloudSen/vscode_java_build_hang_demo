package cn.cisdigital.datakits.framework.util.qbeeopensdk.service.impl;

import cn.cisdigital.datakits.framework.util.qbeeopensdk.service.SdkMenuService;
import cn.cisdigital.datakits.framework.util.qbeeopensdk.vo.SdkMenuVo;
import cn.hutool.core.collection.CollUtil;
import im.qingtui.qbee.open.platfrom.auth.model.param.menu.GetEmployeeMenuParam;
import im.qingtui.qbee.open.platfrom.auth.model.vo.menu.EmployeeMenuTree;
import im.qingtui.qbee.open.platfrom.auth.service.MenuService;
import im.qingtui.qbee.open.platfrom.base.model.vo.base.BaseList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author xxx
 * @since 2024/3/28 11:15
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class SdkMenuServiceImpl implements SdkMenuService {

    public static final String EMPTY_STRING = "";

    public static final SdkMenuVo EMPTY_MENU_TREE = new SdkMenuVo();

    @Override
    public SdkMenuVo searchMenu(String employeeId, String subSystem, String linkUrl, Integer type) {
        GetEmployeeMenuParam searchDto = new GetEmployeeMenuParam();
        searchDto.setEmployeeId(employeeId);
        searchDto.setType(type);
        BaseList<EmployeeMenuTree> employeeMenuTree = MenuService.getEmployeeMenuTree(searchDto);
        List<EmployeeMenuTree> menuList = employeeMenuTree.getList();
        if (CollUtil.isNotEmpty(menuList)) {
            EmployeeMenuTree menuTreeDto = recursiveSearch(menuList, subSystem, linkUrl);
            return Objects.nonNull(menuTreeDto) ? SdkMenuVo.employeeMenuTree2SdkMenuTreeVo(menuTreeDto) : EMPTY_MENU_TREE;
        } else {
            return EMPTY_MENU_TREE;
        }
    }

    public EmployeeMenuTree recursiveSearch(List<EmployeeMenuTree> employeeMenuTree, String subsystem, String linkUrl) {
        EmployeeMenuTree result = null;
        for (EmployeeMenuTree menuTree : employeeMenuTree) {
            // 轻蜂平台配置菜单时前后有可能会带空格, 为了避免接下来的判断过不了, 我们兼容一下算了
            String subSystemTrimmed = Optional.ofNullable(menuTree.getSubSystem()).orElse(EMPTY_STRING).trim();
            String linkUrlTrimmed = Optional.ofNullable(menuTree.getLinkUrl()).orElse(EMPTY_STRING).trim();

            if (Objects.equals(subsystem, subSystemTrimmed) && Objects.equals(linkUrl, linkUrlTrimmed)) {
                result = menuTree;
                return result;
            }
            if (CollUtil.isNotEmpty(menuTree.getChildren())) {
                result = recursiveSearch(menuTree.getChildren(), subsystem, linkUrl);
                if (result != null) {
                    return result;
                }
            }
        }
        return result;
    }
}
