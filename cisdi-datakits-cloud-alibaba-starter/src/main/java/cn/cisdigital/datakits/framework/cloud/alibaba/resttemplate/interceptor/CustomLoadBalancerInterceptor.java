package cn.cisdigital.datakits.framework.cloud.alibaba.resttemplate.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;

import java.io.IOException;
import java.net.URI;

/**
 * @author xxx
 * @since 2024-05-24
 */
@Slf4j
@RequiredArgsConstructor
public class CustomLoadBalancerInterceptor implements ClientHttpRequestInterceptor {

    private final LoadBalancerClient loadBalancerClient;
    private final ClientHttpRequestFactory clientHttpRequestFactory;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        URI originalUri = request.getURI();

        if (log.isDebugEnabled()) {
            log.debug("载均衡请求: {} {}", request.getMethod(), originalUri);
        }
        ServiceInstance instance = loadBalancerClient.choose(originalUri.getHost());
        if (instance == null) {
            if (log.isDebugEnabled()) {
                log.debug("注册中心中无法找到服务实例: {}，尝试直接调用", originalUri.getHost());
            }
            return executeRequest(originalUri, request.getMethod(), request.getHeaders(), body);
        }

        URI instanceUri = LoadBalancerUriTools.reconstructURI(instance, originalUri);
        return executeRequest(instanceUri, request.getMethod(), request.getHeaders(), body);
    }

    private ClientHttpResponse executeRequest(URI uri, HttpMethod method, HttpHeaders headers, byte[] body) throws IOException {
        ClientHttpRequest clientHttpRequest = clientHttpRequestFactory.createRequest(uri, method);
        clientHttpRequest.getHeaders().putAll(headers);
        clientHttpRequest.getBody().write(body);
        return clientHttpRequest.execute();
    }
}
