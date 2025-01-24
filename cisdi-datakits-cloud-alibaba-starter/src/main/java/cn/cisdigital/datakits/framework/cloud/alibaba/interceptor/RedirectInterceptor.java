package cn.cisdigital.datakits.framework.cloud.alibaba.interceptor;


import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.LOCATION;

/**
 * 自定义的重定向拦截器，使用前需先关闭默认重定向拦截器，OkHttpClient.newBuilder.followRedirects(false)
 *
 * @author xxx
 */
public class RedirectInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        // 检测是否需要重定向
        if (response.isRedirect()) {
            // 关闭原始响应
            response.close();
            String location = response.header(LOCATION);
            if (location != null) {
                // 构造新的请求
                Request newRequest = request.newBuilder().url(location).build();
                return chain.proceed(newRequest);
            }
        }
        return response;
    }
}
