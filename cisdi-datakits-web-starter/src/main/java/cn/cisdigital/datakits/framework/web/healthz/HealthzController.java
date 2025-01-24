package cn.cisdigital.datakits.framework.web.healthz;

import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 波塞冬探针
 *
 * @author xxx
 */
@RestController
@Slf4j
@RequestMapping("/innerApi/healthz")
public class HealthzController {

    /**
     * 存活探针
     */
    @GetMapping(value = "/liveness")
    public void liveness(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * 就绪探针
     **/
    @GetMapping(value = "/readiness")
    public void readiness(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
