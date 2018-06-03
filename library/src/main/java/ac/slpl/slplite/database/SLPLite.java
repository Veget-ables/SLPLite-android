package ac.slpl.slplite.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

import ac.slpl.slplite.database.config.SLPLiteConfig;
import ac.slpl.slplite.database.crud.create.CreateLogic;
import ac.slpl.slplite.database.crud.create.RecycleAssetsLogic;
import ac.slpl.slplite.database.listener.ReadProcess;
import ac.slpl.slplite.database.listener.WriteProcess;
import ac.slpl.slplite.database.crud.read.ReadLogicImpl;
import ac.slpl.slplite.database.crud.update.csv.WriteCsvLogicImpl;
import ac.slpl.slplite.database.crud.update.WriteLogicImpl;

public class SLPLite<E> extends SQLiteOpenHelper {
    private final Context mContext;
    private SLPLiteConfig mCfg;

    private SQLiteDatabase mSqLiteDatabase; // SQLite本体

    public SLPLite(Context context, SLPLiteConfig cfg) {
        super(context, cfg.getDBName(), null, cfg.getDBVersion());
        mContext = context;
        mCfg = cfg;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> queryList = new CreateLogic().createSchemaQuery(mCfg.getTables());
        for (String query : queryList) {
            db.execSQL(query);
        }
    }

    public void write(@NonNull E dataSet, @NonNull WriteProcess listener) {
        new WriteLogicImpl(this, listener).extractEntityAndExecute(dataSet);

        if (mCfg.isSavedCsv()) {
            new WriteCsvLogicImpl(mContext, null).extractEntityAndExecute(dataSet);
        }
    }

    public void writeCsvOnly(@NonNull E dataSet, @NonNull WriteProcess listener) {
            new WriteCsvLogicImpl(mContext, listener).extractEntityAndExecute(dataSet);
    }

    public void readData(@NonNull String table, @Nullable ReadProcess listener) {
        new ReadLogicImpl(this, listener).executeReadTask(table);
    }

    public Map<String, Class> getTables() {
        return mCfg.getTables();
    }

    public SQLiteDatabase getDatabase() {
        return mSqLiteDatabase;
    }

    public void openWrite() {
        mSqLiteDatabase = getWritableDatabase();
    }

    public void openRead() {
        mSqLiteDatabase = getReadableDatabase();
    }

    public void recycleAssetsDataBase() {
        new RecycleAssetsLogic(this, mContext).recycle(mCfg);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
