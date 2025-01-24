package cn.cisdigital.datakits.framework.web;

import java.io.Serializable;
import lombok.Data;

/**
 * @author xxx
 * @since 2024-05-17
 */
@Data
public class TestParam implements Serializable {

    private TestEnum testEnum;
}
