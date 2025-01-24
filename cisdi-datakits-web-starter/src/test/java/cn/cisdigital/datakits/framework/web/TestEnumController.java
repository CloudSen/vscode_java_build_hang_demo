package cn.cisdigital.datakits.framework.web;

import cn.cisdigital.datakits.framework.model.vo.ResVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xxx
 * @since 2024-03-08
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/test/enum")
public class TestEnumController {

    @GetMapping("/param")
    public ResVo<TestEnum> getParam(@RequestParam(required = false) TestEnum testEnum) {
        return ResVo.ok(testEnum);
    }

    @PostMapping("/param")
    public ResVo<TestParam> getParamBody(@RequestBody TestParam testParam) {
        return ResVo.ok(testParam);
    }
}
