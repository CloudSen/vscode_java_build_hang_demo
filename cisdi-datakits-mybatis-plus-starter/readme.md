# Mybatis-Plus 增强Starter

## 功能

### 基础模型

- 提供常用的标准字段Entity

### 代码生成器

- 生成业务模块的ORM层代码

### 处理器

- 自动填充处理器
- 枚举转换处理器

### 拦截器
- 适配数据库大小写敏感的字段转换拦截器
- 乐观锁拦截器
- 分页拦截器
- 防全表更新和删除拦截器

## 基础模型

- IdEntity: 提供雪花id字段
- TimeEntity: 提供创建时间create_time、更新时间update_time字段
- VersionEntity: 提供乐观锁version字段
- OperatorEntity: 提供操作人id字段create_by、更新人id字段update_by
- OperatorAndTimeEntity: 提供操作人id字段create_by、更新人id字段update_by、创建时间create_time、更新时间update_time字段
- StandardEntity: 提供IdEntity、TimeEntity、OperatorEntity、VersionEntity的组合

## 代码生成器

todo

## 处理器

todo

## 拦截器

### 字段转换拦截器

> 背景：业主的国产数据库配置可能不统一，每个数据库关键字也不同，存在大小写敏感问题。  
> 例如达梦数据库，大小写敏感时：不加双引号，系统会自动转换成大写，加双引号不会自动转换;
> 大小写不敏感时：加不加双引号都没影响

当业主使用达梦数据库，并且开启大小写敏感时，需要在Nacos的`public-config`中添加以下全局配置：

```
datakits:
  default-config:
    mp:
      enable-field-convert-interceptor: true
```

如果后续有KingBase之类的其他数据库，需要实现`cn.cisdigital.datakits.framework.mp.interceptor.convert.DbFieldConvertService
`接口，然后指定具体的实现类:  
```
datakits:
  default-config:
    mp:
      enable-field-convert-interceptor: true
      field-convert-interceptor-impl: cn.cisdigital.datakits.framework.mp.interceptor.convert.XXXFieldConvertService
```

开启加载后，服务启动时会打印以下日志：

```
[ 自动装配 ] 加载MP 字段拦截器：  cn.cisdigital.datakits.framework.mp.interceptor.convert.XXXFieldConvertService
```
