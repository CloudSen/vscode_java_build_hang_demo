package cn.cisdigital.datakits.framework.dynamic.datasource.config;

import com.healthmarketscience.jackcess.CryptCodecProvider;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.DateTimeType;
import net.ucanaccess.jdbc.JackcessOpenerInterface;

import java.io.File;
import java.io.IOException;

/**
 * 用于支持打开加密的Access文件
 *
 * @author xxx
 */
public class AccessCryptCodecOpener implements JackcessOpenerInterface {

    @Override
    public Database open(File fl, String pwd) throws IOException {
        DatabaseBuilder dbd = new DatabaseBuilder(fl);
        dbd.setAutoSync(false);
        dbd.setCodecProvider(new CryptCodecProvider(pwd));
        Database db = null;
        try {
            dbd.setReadOnly(false);
            db = dbd.open();
        } catch (IOException e) {
            dbd.setReadOnly(true);
            db = dbd.open();
        }
        db.setDateTimeType(DateTimeType.LOCAL_DATE_TIME);
        return db;
    }
}
