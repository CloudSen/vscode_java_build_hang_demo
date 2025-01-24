package cn.cisdigital.datakits.framework.mp.generator;


import cn.cisdigital.datakits.framework.common.exception.BusinessException;
import cn.cisdigital.datakits.framework.mp.model.entity.StandardAuditEntity;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Property;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.stream.Stream;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>代码生成器二次封装</p>
 * <p>约定包结构：cn.cisdigital.datakits.项目名.模块名.领域.mvc层</p>
 * <p>例如，区分业务领域的包结构：</p>
 * <pre>
 *  app-datakits-resource
 *      app-datakits-resource-boot
 *      app-datakits-resource-biz
 *          cn.cisdigital.datakits.resource.biz
 *              cn.cisdigital.datakits.resource.biz.业务1
 *                  cn.cisdigital.datakits.resource.biz.业务1.controller
 *                  cn.cisdigital.datakits.resource.biz.业务1.service
 *                  cn.cisdigital.datakits.resource.biz.业务1.repository
 *                      cn.cisdigital.datakits.resource.biz.业务1.repository.mapper
 *                  cn.cisdigital.datakits.resource.biz.业务1.model
 *                      cn.cisdigital.datakits.resource.biz.业务1.model.dto
 *                      cn.cisdigital.datakits.resource.biz.业务1.model.vo
 *                      cn.cisdigital.datakits.resource.biz.业务1.model.param
 *                      cn.cisdigital.datakits.resource.biz.业务1.model.entity
 *                      cn.cisdigital.datakits.resource.biz.业务1.model.enums
 *                      cn.cisdigital.datakits.resource.biz.业务1.model.converter 基于getter/setter的转换器
 *              cn.cisdigital.datakits.resource.biz.业务2
 *                  cn.cisdigital.datakits.resource.biz.业务2.controller
 *                  cn.cisdigital.datakits.resource.biz.业务2.service
 *                  cn.cisdigital.datakits.resource.biz.业务2.repository
 *                      cn.cisdigital.datakits.resource.biz.业务2.repository.mapper
 *                  cn.cisdigital.datakits.resource.biz.业务2.model
 *                      cn.cisdigital.datakits.resource.biz.业务2.model.dto
 *                      cn.cisdigital.datakits.resource.biz.业务2.model.vo
 *                      cn.cisdigital.datakits.resource.biz.业务2.model.param
 *                      cn.cisdigital.datakits.resource.biz.业务2.model.entity
 *                      cn.cisdigital.datakits.resource.biz.业务2.model.enums
 *                      cn.cisdigital.datakits.resource.biz.业务2.model.converter 基于getter/setter的转换器
 *      app-datakits-resource-client
 *          cn.cisdigital.datakits.resource.client.api
 *      app-datakits-resource-openapi
 *          cn.cisdigital.datakits.resource.openapi.api
 *
 * </pre>
 *
 * <p>参考mybatis-plus配置</p>
 * <pre>
 * mybatis-plus:
 *   configuration:
 *     # sql日志
 *     log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
 *     # 关闭一级和二级缓存
 *     local-cache-scope: statement
 *     cache-enabled: false
 *   global-config:
 *     banner: false
 *     db-config:
 *       # 自带的雪花算法，全局唯一id
 *       id-type: assign_id
 *       table-prefix: tm
 * </pre>
 *
 * @author xxx
 * @since 2022-06-24
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CodeGenerator {

    /**
     * 生成mvc代码
     *
     * @param property 代码生成器配置
     */
    public static void generate(@Valid GeneratorProperty property) {
        checkAndSetPropertyInteractively(property);
        setMybatisGenerator(property).execute();
        createEmptyDirectory(property);
    }

    /**
     * 交互式设置某些参数
     *
     * @param property 代码生成器配置
     */
    private static void checkAndSetPropertyInteractively(GeneratorProperty property) {
        String projectNamePrefix = property.getProjectNamePrefix();
        boolean invalidProjectName = CharSequenceUtil.isNotBlank(projectNamePrefix)
            && !property.getProjectName().startsWith(projectNamePrefix);
        if (invalidProjectName) {
            throw new BusinessException("项目名错误！后端项目名必须以" + projectNamePrefix + "开头");
        }
        Scanner sc = new Scanner(System.in);
        if (CharSequenceUtil.isBlank(property.getAuthor())) {
            System.out.println("> 请输入代码作者:");
            String author = Optional.ofNullable(sc.nextLine()).filter(CharSequenceUtil::isNotBlank)
                .orElseThrow(() -> new BusinessException("作者名不能为空"));
            property.setAuthor(author);
        }
        if (property.getSkipBusinessDomain()) {
            property.setBusinessDomain(CharSequenceUtil.EMPTY);
        }
        if (CharSequenceUtil.isBlank(property.getBusinessDomain()) && !property.getSkipBusinessDomain()) {
            System.out.println("> 请输入业务模块名，可以为空");
            String businessDomain = sc.nextLine();
            property.setBusinessDomain(
                CharSequenceUtil.isBlank(businessDomain) ? CharSequenceUtil.EMPTY : businessDomain);
        }
        property.setBusinessDomain(property.getBusinessDomain().toLowerCase())
            .setPackagePrefix(property.getPackagePrefix().toLowerCase())
            .setProjectNamePrefix(property.getProjectNamePrefix().toLowerCase());
        System.out.println("===> 当前生成器配置:");
        System.out.println(property);
    }

    /**
     * 配置Mybatis Plus Generator
     *
     * @param property 代码生成器配置
     */
    private static FastAutoGenerator setMybatisGenerator(GeneratorProperty property) {
        String author = property.getAuthor();
        String packagePrefix = property.getPackagePrefix();
        String projectName = property.getProjectName();
        String businessDomain = property.getBusinessDomain();
        String projectNameSeparator = property.getProjectNameSeparator();
        String replacer = property.getProjectNameSeparatorReplacer();
        // 自定义变量配置，用于ftl模板中
        Map<String, Object> customConfig = new HashMap<>(1);
        customConfig.put("businessDomain", businessDomain);
        customConfig.put("superClassColumns",
            Arrays.asList("version", "createBy", "updateBy", "createTime", "updateTime"));
        return FastAutoGenerator.create(property.getDataSourceBuilder())
            // 全局配置
            .globalConfig((scanner, builder) -> {
                builder.author(author).disableOpenDir();
                if (property.getOverwriteFiles() != null && property.getOverwriteFiles()) {
                    builder.fileOverride();
                }
            })
            // 包配置
            .packageConfig((scanner, builder) ->
                builder.parent(packagePrefix)
                    .entity(
                        CharSequenceUtil.isBlank(businessDomain)
                            ? GeneratorConstants.MODEL + GeneratorConstants.DOT + GeneratorConstants.ENTITY
                            : businessDomain + GeneratorConstants.DOT + GeneratorConstants.MODEL
                                + GeneratorConstants.DOT + GeneratorConstants.ENTITY)
                    .service(
                        CharSequenceUtil.isBlank(businessDomain)
                            ? GeneratorConstants.SERVICE
                            : businessDomain + GeneratorConstants.DOT + GeneratorConstants.SERVICE)
                    .serviceImpl(
                        CharSequenceUtil.isBlank(businessDomain)
                            ? GeneratorConstants.REPOSITORY
                            : businessDomain + GeneratorConstants.DOT + GeneratorConstants.REPOSITORY
                    )
                    .mapper(
                        CharSequenceUtil.isBlank(businessDomain)
                            ? GeneratorConstants.REPOSITORY + GeneratorConstants.DOT + GeneratorConstants.MAPPER
                            : businessDomain + GeneratorConstants.DOT + GeneratorConstants.REPOSITORY
                                + GeneratorConstants.DOT + GeneratorConstants.MAPPER)
                    .controller(
                        CharSequenceUtil.isBlank(businessDomain)
                            ? GeneratorConstants.CONTROLLER + GeneratorConstants.DOT + GeneratorConstants.API
                            : businessDomain + GeneratorConstants.DOT + GeneratorConstants.CONTROLLER
                                + GeneratorConstants.DOT + GeneratorConstants.API)
                    .pathInfo(buildPathInfo(property)))
            // 策略配置
            .strategyConfig((scanner, builder) -> {
                builder.addInclude(getTables(scanner.apply("> 请输入表名，多个英文逗号分隔，所有输入 all:")))
                    .controllerBuilder()
                    .formatFileName("%sController")
                    // 使用@RestController
                    .enableRestStyle()
                    // 驼峰名转换
                    .enableHyphenStyle()
                    // service类名模板
                    .serviceBuilder()
                    .convertServiceFileName(entityName -> entityName + "Service")
                    // serviceImpl名称修改: 使用repository类进行数据库操作, 避免和service里面的业务逻辑混在一起
                    .convertServiceImplFileName(entityName -> entityName + "Repository")
                    // mapper设置
                    .mapperBuilder()
                    .formatMapperFileName("%sMapper")
                    .entityBuilder()
                    // 使用lombok注解
                    .enableLombok()
                    .superClass(StandardAuditEntity.class)
                    // 链式调用
                    .enableChainModel()
                    // entity类名模板
                    .formatFileName("%sEntity")
                    // Boolean类型字段移除is前缀
                    .enableRemoveIsPrefix()
                    // 生成mybatis字段注解
                    .enableTableFieldAnnotation();
                if (property.getEntitySuperClass() != null) {
                    builder.entityBuilder().superClass(property.getEntitySuperClass());
                }
                if (CollUtil.isNotEmpty(property.getTablePrefixList())) {
                    builder.addTablePrefix(property.getTablePrefixList().toArray(new String[]{}));
                }
                // 自动填充创建时间，修改时间
                builder.entityBuilder()
                    .addTableFills(
                        new Property("create_time", FieldFill.INSERT),
                        new Property("update_time", FieldFill.INSERT_UPDATE))
                    .build();
            })
            // 模板配置
            .templateEngine(new FreemarkerTemplateEngine())
            .templateConfig((string, builder) -> {
                builder
                    .disable(property.getDisableConfig().disableTemplateTypes)
                    .entity("custom-templates/entity.java")
                    .controller("custom-templates/controller.java")
                    .service("custom-templates/service.java")
                    .mapper("custom-templates/mapper.java")
                    .serviceImpl("custom-templates/serviceImpl2Repository.java");
                if (property.disableConfig.disableController != null && property.disableConfig.disableController) {
                    builder.controller(CharSequenceUtil.EMPTY);
                }
            })
            .injectionConfig(builder -> builder.customMap(customConfig));
    }

    /**
     * 创建不存在的路径或文件
     *
     * @param path 需要创建的路径
     */
    private static void createFileAndParentPath(Path path) {
        if (path == null) {
            return;
        }
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建不存在的目录
     *
     * @param path 需要创建的目录路径
     */
    private static void createDirectoryIfNotExists(Path path) {
        if (path == null) {
            return;
        }
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取表名
     *
     * @param tables 逗号分割的表名，可以为"all"
     * @return 表名列表，如果为"all"，则返回空列表
     */
    private static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }

    /**
     * 自定义文件生成路径
     *
     * @param property 代码生成器配置
     * @return 文件对应的路径map
     */
    public static Map<OutputFile, String> buildPathInfo(GeneratorProperty property) {
        Map<OutputFile, String> pathInfo = new EnumMap<>(OutputFile.class);
        PathParam pathParam = parseGeneratorProperty(property);
        String businessPkgPath = buildBusinessPkgPath(pathParam);

        String entityPkgPath =
            businessPkgPath + GeneratorConstants.MODEL + File.separator + GeneratorConstants.ENTITY + File.separator;
        String mapperPath = businessPkgPath + GeneratorConstants.REPOSITORY + File.separator + GeneratorConstants.MAPPER
            + File.separator;
        // 默认生成到mysql目录下
        String mapperXmlPath = pathParam.businessModuleResourcePath + GeneratorConstants.MAPPER + File.separator
            + GeneratorConstants.MYSQL;
        String controllerApiPath =
            businessPkgPath + GeneratorConstants.CONTROLLER + File.separator + GeneratorConstants.API + File.separator;
        String servicePath = businessPkgPath + GeneratorConstants.SERVICE + File.separator;
        String serviceImpl2RepositoryPath = businessPkgPath + GeneratorConstants.REPOSITORY + File.separator;

        // 将serviceImpl生成为repository
        pathInfo.put(OutputFile.serviceImpl, serviceImpl2RepositoryPath);
        pathInfo.put(OutputFile.service, servicePath);
        pathInfo.put(OutputFile.mapper, mapperPath);
        pathInfo.put(OutputFile.mapperXml, mapperXmlPath);
        pathInfo.put(OutputFile.entity, entityPkgPath);
        pathInfo.put(OutputFile.controller, controllerApiPath);

        return pathInfo;
    }

    private static @org.jetbrains.annotations.NotNull
    String buildBusinessPkgPath(PathParam pathParam) {
        return pathParam.businessModuleJavaPath
            + (
            CharSequenceUtil.isBlank(pathParam.businessDomain)
                ? pathParam.packagePathPrefix
                : pathParam.packagePathPrefix + pathParam.businessDomain + File.separator
        );
    }

    private static @org.jetbrains.annotations.NotNull
    String buildResourceMapperPath(PathParam pathParam) {
        return pathParam.businessModuleResourcePath + GeneratorConstants.MAPPER + File.separator;
    }

    private static void createEmptyDirectory(GeneratorProperty property) {
        PathParam pathParam = parseGeneratorProperty(property);
        String businessPkgPath = buildBusinessPkgPath(pathParam);
        String businessResourcePath = buildResourceMapperPath(pathParam);

        String controllerInnerApiPath =
            GeneratorConstants.CONTROLLER + File.separator + GeneratorConstants.INNER_API + File.separator
                + GeneratorConstants.GIT_KEEP_FILE_NAME;
        String controllerOpenApiPath =
            GeneratorConstants.CONTROLLER + File.separator + GeneratorConstants.OPEN_API + File.separator
                + GeneratorConstants.GIT_KEEP_FILE_NAME;
        String converterPkgPath =
            GeneratorConstants.MODEL + File.separator + GeneratorConstants.CONVERTER + File.separator
                + GeneratorConstants.GIT_KEEP_FILE_NAME;
        String dtoPath = GeneratorConstants.MODEL + File.separator + GeneratorConstants.DTO + File.separator
            + GeneratorConstants.GIT_KEEP_FILE_NAME;
        String voPath = GeneratorConstants.MODEL + File.separator + GeneratorConstants.VO + File.separator
            + GeneratorConstants.GIT_KEEP_FILE_NAME;
        String paramPath = GeneratorConstants.MODEL + File.separator + GeneratorConstants.PARAM + File.separator
            + GeneratorConstants.GIT_KEEP_FILE_NAME;
        String enumsPath = GeneratorConstants.MODEL + File.separator + GeneratorConstants.ENUMS + File.separator
            + GeneratorConstants.GIT_KEEP_FILE_NAME;
        String mapperXmlDmPath = GeneratorConstants.DM + File.separator + GeneratorConstants.GIT_KEEP_FILE_NAME;
        String mapperXmlKingBasePath =
            GeneratorConstants.KING_BASE + File.separator + GeneratorConstants.GIT_KEEP_FILE_NAME;

        Stream.of(controllerInnerApiPath, controllerOpenApiPath, converterPkgPath, dtoPath, voPath, paramPath,
                enumsPath)
            .map(path -> Paths.get(businessPkgPath + path))
            .forEach(CodeGenerator::createFileAndParentPath);

        Stream.of(mapperXmlDmPath, mapperXmlKingBasePath)
            .map(path -> Paths.get(businessResourcePath + path))
            .forEach(CodeGenerator::createFileAndParentPath);
    }

    private static PathParam parseGeneratorProperty(GeneratorProperty property) {
        String userDir = System.getProperty(GeneratorConstants.USER_DIR);
        String projectName = property.getProjectName();
        String projectNameForModule = property.getIsMultiModuleProject() ?
            projectName + GeneratorConstants.SUFFIX_BIZ
            : CharSequenceUtil.EMPTY;
        String projectNameForPackage = projectName.replace(property.getProjectNamePrefix(), CharSequenceUtil.EMPTY)
            .replace(property.getProjectNameSeparator(), property.projectNameSeparatorReplacer);
        String businessDomain = property.getBusinessDomain();
        String packagePathPrefix =
            property.getPackagePrefix().replace(GeneratorConstants.DOT, Matcher.quoteReplacement(File.separator))
                + File.separator;
        String modulePathPrefix = userDir + File.separator + projectNameForModule.replace(GeneratorConstants.DOT,
            Matcher.quoteReplacement(File.separator));
        String businessModuleJavaPath =
            modulePathPrefix + File.separator + GeneratorConstants.SRC + File.separator + GeneratorConstants.MAIN
                + File.separator + GeneratorConstants.JAVA + File.separator;
        String businessModuleResourcePath =
            modulePathPrefix + File.separator + GeneratorConstants.SRC + File.separator + GeneratorConstants.MAIN
                + File.separator + GeneratorConstants.RESOURCES + File.separator;
        return new PathParam()
            .setUserDir(userDir)
            .setProjectName(projectName)
            .setProjectNameForPackage(projectNameForPackage)
            .setProjectNameForModule(projectNameForModule)
            .setBusinessDomain(businessDomain)
            .setPackagePathPrefix(packagePathPrefix)
            .setModulePathPrefix(modulePathPrefix)
            .setBusinessModuleJavaPath(businessModuleJavaPath)
            .setBusinessModuleResourcePath(businessModuleResourcePath);
    }

    /**
     * 代码生成器配置类
     */
    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    public static class GeneratorProperty {

        /**
         * 【必须】数据源配置Builder
         */
        @NotNull
        private DataSourceConfig.Builder dataSourceBuilder;

        /**
         * 【可选】禁用配置，默认不会生成controller
         */
        private DisableConfig disableConfig;
        /**
         * 【必须】包名前缀
         */
        private String packagePrefix;
        /**
         * 【可选】项目名前缀，用于校验项目名是否正确，默认app-datakits-
         */
        private String projectNamePrefix;
        /**
         * 【可选】代码作者，可通过交互式设置
         */
        private String author;
        /**
         * 【必须】项目名
         */
        @NotBlank
        private String projectName;
        /**
         * 【可选】业务领域名，可通过交互式设置，如果配置了该项，则mvc层都创建在当前业务包内，否则mvc层是平铺的
         */
        private String businessDomain;
        /**
         * 生成文件时是否覆盖已有的文件
         */
        private Boolean overwriteFiles;
        /**
         * DO实体类的父类
         */
        private Class<?> entitySuperClass;
        /**
         * 项目名分割符替代品，用于转换项目名为包名，默认替换为空字符串
         */
        private String projectNameSeparatorReplacer;
        /**
         * 【可选】项目名分割符，默认为横杠-
         */
        private String projectNameSeparator;
        /**
         * 【可选】是否是多模块项目，默认是
         */
        private Boolean isMultiModuleProject;
        /**
         * 【可选】跳过业务域配置，默认false
         */
        private Boolean skipBusinessDomain;
        /**
         * 【可选】表名前缀，如果指定，生成的类会去掉前缀
         */
        private List<String> tablePrefixList;


        public GeneratorProperty() {
            this.projectNamePrefix = "app-datakits-";
            DisableConfig defaultDisable = new DisableConfig();
            defaultDisable.setDisableController(true);
            this.disableConfig = defaultDisable;
            this.projectNameSeparatorReplacer = CharSequenceUtil.EMPTY;
            this.projectNameSeparator = GeneratorConstants.HYPHEN;
            this.isMultiModuleProject = true;
            this.skipBusinessDomain = false;
        }
    }

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PathParam {

        private String userDir;
        private String projectName;
        private String projectNameForPackage;
        private String projectNameForModule;
        private String businessDomain;
        private String packagePathPrefix;
        private String modulePathPrefix;
        private String businessModuleJavaPath;
        private String businessModuleResourcePath;
    }

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisableConfig {

        /**
         * 禁用模板
         */
        private TemplateType[] disableTemplateTypes;

        /**
         * 禁止生成controller，默认true
         */
        private Boolean disableController;
    }
}
