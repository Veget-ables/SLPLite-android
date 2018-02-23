package ac.slpl.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ac.slpl.example.model.DataSet;
import ac.slpl.example.model.InputData;
import ac.slpl.example.model.User;
import ac.slpl.slplite.database.SLPLite;
import ac.slpl.slplite.database.config.SLPLiteConfig;
import ac.slpl.slplite.database.listener.ReadProcess;
import ac.slpl.slplite.database.listener.WriteProcess;

// DBを初めから作成する場合のサンプル
public class SampleActivity extends Activity {
    private SLPLite mSLPLite;

    // SLPLite作成必須パラメータ
    private final String DATABASE_NAME = "slpl.db";
    private final int DATABASE_VERSION = 1;
    private final String TABLE_USER = "user_info";
    private final String TABLE_INPUT = "input_data";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupDatabase();
        DataSet dataSet = createDataSet(); // DataSetを作成
        mSLPLite.writeData(dataSet, new WriteProcess() { // SLPLiteに書き出す.結果はcallbackで受取る．
            @Override
            public void onSucceededWriting() {
            }

            @Override
            public void onFailedProcess(String message) {
                Log.e(getClass().toString(), message);
            }
        });
    }

    // SLPLiteを初期化
    private void setupDatabase() {
        // Table名 -> ColumnとなるEntityクラス
        Map<String, Class> tables = new HashMap<>(); // これがそのままDBのスキーマになる
        tables.put(TABLE_USER, User.class);
        tables.put(TABLE_INPUT, InputData.class);

        SLPLiteConfig cfg = new SLPLiteConfig(DATABASE_NAME, DATABASE_VERSION, tables)
                .setSavedCsv(true)
                .setDBPath(this);
        mSLPLite = new SLPLite(this, cfg);
    }

    private void read() {
        //読み込みたいTableを指定しSLPLiteから読み込む. 結果はcallbackで受取る．
        mSLPLite.readData(TABLE_INPUT, new ReadProcess<InputData>() {
            @Override
            public void onSucceededReading(List<InputData> result) {
            }

            @Override
            public void onFailedProcess(String message) {
                Log.e(getClass().toString(), message);
            }
        });
    }


    private DataSet createDataSet() {
        // Tableの値として指定したEntity毎にデータを作成.
        List<InputData> inputDataList = createInputDataList();
        User user = createUser();

        // データを1つのDataSetにまとめる．
        DataSet dataSet = new DataSet();
        dataSet.setInputData(inputDataList);
        dataSet.setUser(user);
        return dataSet;
    }

    private static List<InputData> createInputDataList() {
        List<InputData> list = new ArrayList<>();
        new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            InputData data = new InputData("huga" + i, i, i);
            list.add(data);
        }
        return list;
    }

    private static User createUser() {
        return new User()
                .setDeviceId(1)
                .setName("user1")
                .setAge(20)
                .setSex(false)
                .setHandedness(true);
    }
}
