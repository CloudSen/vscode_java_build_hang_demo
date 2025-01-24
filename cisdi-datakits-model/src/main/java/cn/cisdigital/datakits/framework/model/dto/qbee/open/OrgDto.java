package cn.cisdigital.datakits.framework.model.dto.qbee.open;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xxx
 * @since 2024-04-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrgDto extends OrgBaseDto {

    /**
     * 组织机构全路径
     */
    private List<OrgBaseDto> orgPathList = new ArrayList<>();


    public OrgDto() {
    }

    public OrgDto(String orgId) {
        super(orgId);
    }

    /**
     * 获取组织机构路径 eg:  /组织机构维度/中冶赛迪/赛迪信息
     *
     * @return
     */
    public String getPath() {
        if (CollUtil.isNotEmpty(orgPathList)) {
            StringBuilder stringBuilder = new StringBuilder();
            orgPathList.forEach(p -> stringBuilder.append("/").append(p.getOrgName()));
            return stringBuilder.toString();
        }
        return "";
    }
}
