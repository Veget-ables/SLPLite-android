package ac.slpl.slplite.database.config;

import android.content.Context;

import java.io.File;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@ToString(exclude = {"dbPath"})
@Getter
@Setter
public class SLPLiteConfig {
    private String DBName;
    private int DBVersion;
    private Map<String, Class> tables;

    private boolean isSavedCsv = false;
    private File dbPath;
    private String assetDBName;

    public SLPLiteConfig(String dbName, int dbVersion, Map<String, Class> tables) {
        this.DBName = dbName;
        this.DBVersion = dbVersion;
        this.tables = tables;
    }

    public SLPLiteConfig setDBPath(Context context) {
        this.dbPath = context.getDatabasePath(this.DBName);
        return this;
    }

    public String getAbsoluteDBPath() {
        return this.dbPath.getAbsolutePath();
    }
}
