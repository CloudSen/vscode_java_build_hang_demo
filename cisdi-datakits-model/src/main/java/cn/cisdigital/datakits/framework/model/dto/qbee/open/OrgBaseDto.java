package cn.cisdigital.datakits.framework.model.dto.qbee.open;

import lombok.*;

/**
 * @author xxx
 * @since 2024-04-23
 */

@Getter
@Setter
public class OrgBaseDto {

    /**
     * 组织id
     */
    protected String orgId;
    /**
     * 组织名称
     */
    protected String orgName;
    /**
     * 组织机构编码
     */
    protected String orgCode;

    public OrgBaseDto(String orgId, String orgName, String orgCode){
        this.orgId = orgId;
        this.orgName = orgName;
        this.orgCode = orgCode;
    }

    public OrgBaseDto(String orgId, String orgName){
        this.orgId = orgId;
        this.orgName = orgName;
    }

    public OrgBaseDto(String orgId){
        this.orgId = orgId;
    }

    public OrgBaseDto(){}
}
