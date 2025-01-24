package cn.cisdigital.datakits.framework.web.transaction.repository;

import cn.cisdigital.datakits.framework.web.transaction.ResourceEntity;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xxx
 * @since 2024-05-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceManagerRepository extends ServiceImpl<CommonResourceMapper, ResourceEntity> {


    @Transactional(rollbackFor = Exception.class)
    public void updateTest(ResourceEntity resource) {
        this.updateById(resource);
        int a = 3/0;
    }

}
