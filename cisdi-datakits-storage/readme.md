# 统一存储Starter

该starter用于统一存储服务，支持多种协议，目前支持minio协议，有需要可以自行扩展。  
starter务必遵循标准的URI协议，URI格式：`scheme://rootPath/serviceName/path/filename`。  

## 1 使用说明

### 2 Minio Starter

Minio的URI格式：`minio://bucketName/serviceName/path/filename`，其中 objectName是`serviceName/path/filename`。

#### 2.1 引入依赖
Minio Starter maven依赖如下：

```xml
<dependency>
    <groupId>cn.cisdigital.datakits</groupId>
    <artifactId>cisdi-datakits-storage-minio</artifactId>
    <version>1.3.2</version>
</dependency>
```

#### 2.2 配置
在application.yml中配置minio starter相关配置：  
```yaml
datakits:
  default-config:
    storage:
      minio:
        # 以下为必填项
        endpoint: minio地址(可以引用public.minio.endpoint的配置，也可以自己配置)
        access-key: minio用户名(可以引用public.minio.access-key的配置，也可以自己配置)
        secret-key: minio密码(可以引用public.minio.secret-key的配置，也可以自己配置)
        bucket: 桶名称(可以引用public.minio.bucket的配置，也可以自己配置)
        # 以下为可选项
        ## 默认是us-east-1，政企环境可能需要改为REGION
        region: 区域(可以引用public.minio.region的配置，也可以自己配置)
        ## 默认20秒，最低不小于10秒
        connect-timeout: 连接超时时间(可以引用public.minio.connect-timeout的配置，也可以自己配置)
        ## 默认2分钟，最低不小于10秒
        write-timeout: 写入超时时间(可以引用public.minio.write-timeout的配置，也可以自己配置)
        ## 默认2分钟，最低不小于10秒
        read-timeout: 读取超时时间(可以引用public.minio.read-timeout的配置，也可以自己配置)
        ## 默认10分钟，最低不小于1分钟
        pre-signed-expire: 预签名过期时间(可以引用public.minio.pre-signed-expiry的配置，也可以自己配置)
        ## 默认为： txt,log,sh,bat,conf,cfg,py,java,sql,xml,hql,properties,json,yml,yaml,ini,js
        allow-view-suffix: 允许预览的文件后缀(可以引用public.minio.allow-view-suffix的配置，也可以自己配置)
```

#### 2.3 在代码中使用

在业务代码中，使用`ResourceLoader`来加载资源，然后对资源进行操作。
```java

@Service
@RequiredArgsConstructor
public class MyService {

    // 在服务类中，注入ResourceLoader
    private final ResourceLoader resourceLoader;

    public void myFunction() {
        // 从业务代码中，拼接好或者直接从数据库中得到资源的完成路径，比如 minio://my-bucket/my-folder/my-file.txt
        String location = getFromBusiness();
        // 使用ResourceUtils来获得资源对象
        CisdiAbstractResource resource = ResourceUtils.getResourceFrom(resourceLoader, location);
        // 对资源对象进行操作
        resource.exists();
        resource.download();
        resource.delete(false);
        resource.setSelfInputStream(xxx);
        resource.upload();
        // ...
    }
}
```

#### 2.4 启动服务检查日志

启动服务后，检查日志中应该存在以下内容，确保资源加载器已经正确加载。  
```
[ 自动装配 ] 加载对象存储服务
[ 自动装配 ] 已注册MinioClient
[ 自动装配 ] 注册资源解析器：minio://
```
