package cn.cisdigital.datakits.framework.mp.generator;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import java.util.Collections;

/**
 * @author xxx
 * @since 2022-07-11
 */
public final class RunCodeGenerator {

    public static void main(String[] args) {
        CodeGenerator.GeneratorProperty property = new CodeGenerator.GeneratorProperty()
            .setDataSourceBuilder(new DataSourceConfig.Builder(
                "jdbc:mysql://10.106.253.24:3306/metadata_dev",
                "metadata_dev", "Cisdi@123456"
            ))
            // 如果不需要生成controller就注释掉这一行
            .setDisableConfig(new CodeGenerator.DisableConfig().setDisableController(false))
            .setTablePrefixList(Collections.singletonList("metadata_"))
            .setSkipBusinessDomain(false)
            .setIsMultiModuleProject(true)
            .setOverwriteFiles(true)
            .setPackagePrefix("cn.cisdigital.datakits.")
            .setBusinessDomain("linage-module")
            .setProjectNamePrefix("")
            .setProjectNameSeparator("-")
            .setProjectNameSeparatorReplacer(GeneratorConstants.DOT)
            .setProjectName("metadata-management");
        CodeGenerator.generate(property);
    }
}
