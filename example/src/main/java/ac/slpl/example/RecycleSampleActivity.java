package ac.slpl.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ac.slpl.example.model.User;
import ac.slpl.slplite.database.SLPLite;
import ac.slpl.slplite.database.listener.ReadProcess;
import ac.slpl.slplite.database.config.SLPLiteConfig;


// Assetsに保存されたDBを再利用する場合のサンプル
public class RecycleSampleActivity extends Activity {
    private SLPLite mSLPLite;

    private final int DATABASE_VERSION = 1;
    private final String DATABASE_NAME = "recycle_slpl.db";
    private final String ASSET_DATABASE_NAME = "recycle_slpl.db";
    private final String TABLE_USER = "user_info";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupDatabase();

        read();
    }

    private void setupDatabase() {
        // Table名 -> ColumnとなるEntityクラス
        Map<String, Class> tables = new HashMap<>();
        tables.put(TABLE_USER, User.class);

        SLPLiteConfig cfg = new SLPLiteConfig(DATABASE_NAME, DATABASE_VERSION, tables)
                .setAssetDBName(ASSET_DATABASE_NAME)
                .setDBPath(this);

        mSLPLite = new SLPLite(this, cfg);

        // 既存のDBを再利用したい場合に利用する.再利用するDBはAssetsに入っている前提.
        mSLPLite.recycleAssetsDataBase();
    }

    private void read() {
        //読み込みたいTableを指定しSLPLiteから読み込む．結果はcallbackで受取る．
        mSLPLite.readData(TABLE_USER, new ReadProcess<User>() {
            @Override
            public void onSucceededReading(List<User> result) {
            }

            @Override
            public void onFailedProcess(String message) {
            }
        });
    }
}
