package ac.slpl.slplite.database.crud.update.csv;

import android.content.Context;
import android.os.AsyncTask;

import com.orangesignal.csv.Csv;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.handlers.BeanListHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static ac.slpl.slplite.database.config.ExcludeParameter.INSTANT_RUN_CHANGE;
import static ac.slpl.slplite.database.config.ExcludeParameter.INSTANT_RUN_UID;
import static ac.slpl.slplite.database.config.ExcludeParameter.PARCELABLE;

public class WriteCsvAsyncTask<T> extends AsyncTask<Object, Boolean, Boolean> {
    private final String mFileDirPath; // 端末内のfile保存先のpath
    private static CsvConfig sCfg = new CsvConfig();

    static {
        sCfg.setUtf8bomPolicy(true); // 文字化けするのでBOMをつける
        sCfg.setIgnoreEmptyLines(true); // これがないと読み込み時にEntityに変換できない場合がある
    }

    public WriteCsvAsyncTask(Context context) {
        mFileDirPath = context.getFilesDir().getPath();
    }

    @Override
    protected final Boolean doInBackground(Object... dataSet) {
        try {
            insertToCsv((List<T>) dataSet[0]);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return true;
    }

    private void insertToCsv(List<T> newData) throws IOException {
        Class clazz = newData.get(0).getClass();
        File csvFile = new File(mFileDirPath + "/" + clazz.getSimpleName() + ".csv");
        // すでに存在するファイルを読み込み，新しいDataと連結させる
        try {
            List<T> oldData = Csv.load(csvFile, sCfg, new BeanListHandler<T>(clazz));
            newData.addAll(0, oldData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            // Parcelableの使用, Instant Run有効の場合には余分な変数が含まれてしまうため除外する
            Csv.save(newData, csvFile, sCfg, new BeanListHandler<T>(clazz)
                    .excludes(PARCELABLE, INSTANT_RUN_UID, INSTANT_RUN_CHANGE));
        }
    }

    @Override
    protected void onPostExecute(Boolean isWritten) {
    }
}
