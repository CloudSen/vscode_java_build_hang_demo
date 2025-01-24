package cn.cisdigital.datakits.framework.util.doris.api.model;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * @author xxx
 */
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class DorisHttpResponse<T> implements Serializable {

    String msg;
    int code;
    T data;

    public boolean isSuccess(){
        return this.code == 0;
    }
}
