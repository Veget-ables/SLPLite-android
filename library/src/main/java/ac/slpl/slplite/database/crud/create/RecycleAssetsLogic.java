package ac.slpl.slplite.database.crud.create;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ac.slpl.slplite.database.SLPLite;
import ac.slpl.slplite.database.crud.SLPLiteLogic;
import ac.slpl.slplite.database.config.SLPLiteConfig;

public class RecycleAssetsLogic extends SLPLiteLogic {
    private final Context mContext;

    public RecycleAssetsLogic(SLPLite slpLite, Context context) {
        super(slpLite);
        mContext = context;
    }

    public void recycle(SLPLiteConfig cfg) {
        SQLiteDatabase db;
        try {
            createEmptyDataBase(cfg);
            db = mSLPLite.getReadableDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw e;
        }
        db.close();
    }

    /**
     * asset に格納したDBをコピーするための空のDBを作成する
     */
    private void createEmptyDataBase(SLPLiteConfig cfg) throws IOException {
        boolean dbExist = checkDataBaseExists(cfg);
        if (dbExist) {
            return;
        }

        mSLPLite.getReadableDatabase();  // configで指定した空のDBがアプリのデフォルトシステムパスに作られる
        try {
            // assets に格納したDBをコピーする
            copyDataBaseFromAsset(cfg);
            String dbPath = cfg.getAbsoluteDBPath();
            SQLiteDatabase checkDB;
            try {
                checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
            } catch (SQLiteException e) {
                throw new SQLException(e.getMessage());
            }
            if (checkDB != null) {
                checkDB.setVersion(cfg.getDBVersion());
                checkDB.close();
            }
        } catch (IOException e) {
            Log.e(getClass().toString(), "AssetsにDBがないから新規作成するで。");
        }
    }

    /**
     * 再コピーを防止するためにすでにDBがあるかどうか判定する
     */
    private boolean checkDataBaseExists(SLPLiteConfig cfg) {
        String dbPath = cfg.getAbsoluteDBPath();
        SQLiteDatabase checkDB = null;

        // DBの存在を確認
        try {
            checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.e(getClass().toString(), e.getMessage());
        }
        if (checkDB == null) {
            return false;
        }

        // DBのバージョンを確認
        int oldVersion = checkDB.getVersion();
        if (oldVersion == cfg.getDBVersion()) {
            checkDB.close();
            return true;
        } else {
            File f = new File(dbPath);
            f.delete();
            return false;
        }
    }

    /**
     * asset に格納したデーだベースをデフォルトのDBパスに作成した空のDBにコピーする
     */
    private void copyDataBaseFromAsset(SLPLiteConfig cfg) throws IOException {
        InputStream input = mContext.getAssets().open(cfg.getAssetDBName());
        OutputStream output = new FileOutputStream(cfg.getAbsoluteDBPath());
        byte[] buffer = new byte[1024];
        int size;
        while ((size = input.read(buffer)) > 0) {
            output.write(buffer, 0, size);
        }

        output.flush();
        output.close();
        input.close();
    }
}
