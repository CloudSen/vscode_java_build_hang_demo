package cn.cisdigital.datakits.framework.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;

/**
 * 单位转换工具
 *
 * @author xxx
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UnitUtils {

    /**
     * 转换字节大小为带单位的大小
     */
    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
