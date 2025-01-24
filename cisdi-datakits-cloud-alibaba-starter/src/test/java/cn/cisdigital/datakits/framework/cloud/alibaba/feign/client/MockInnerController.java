package cn.cisdigital.datakits.framework.cloud.alibaba.feign.client;

import cn.cisdigital.datakits.framework.model.vo.ResVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xxx
 * @since 2024-05-24
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("innerApi/")
public class MockInnerController {

    @GetMapping("/mock")
    public ResVo<Void> mockGet() {
        return ResVo.ok();
    }
}
