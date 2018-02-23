package ac.slpl.slplite.database.crud.update;

import android.content.ContentValues;
import android.os.AsyncTask;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import ac.slpl.slplite.database.SLPLite;
import ac.slpl.slplite.database.listener.WriteProcess;


public class WriteAsyncTask extends AsyncTask<Object, String, String> {
    private final WriteProcess mListener;
    private final SLPLite mSLPLite;
    private final String mTableName;

    public WriteAsyncTask(WriteProcess listener, SLPLite slpLite, String tableName) {
        mListener = listener;
        mSLPLite = slpLite;
        mTableName = tableName;
    }

    @Override
    protected final String doInBackground(Object... dataSet) {
        mSLPLite.openWrite();
        StringBuffer errorMessage = new StringBuffer();
        try {
            for (ContentValues values : (List<ContentValues>) dataSet[0]) {
                errorMessage.append(insert(values));
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        mSLPLite.close();
        return errorMessage.toString();
    }

    private String insert(ContentValues contentValues) throws IllegalAccessException, InvocationTargetException {
        long rowId = mSLPLite.getDatabase().insert(mTableName, null, contentValues);
        if (rowId == -1) {// 書き出し失敗
            return buildErrorMessage(contentValues);
        }
        return ""; // 書き込み成功
    }

    private String buildErrorMessage(ContentValues values){
        String head = String.format("Failed:%nTable: %s%nContentValue: key, value%n", mTableName);
        StringBuilder errorMessage = new StringBuilder(head);
        for (Map.Entry<String, Object> m : values.valueSet()) {
            errorMessage.append(String.format("              %s, %s%n", m.getKey(), m.getValue()));
        }
        return errorMessage.toString();
    }

    @Override
    protected void onPostExecute(String errorMessage) {
        if (errorMessage.isEmpty()) {
            mListener.onSucceededWriting();
        } else {
            mListener.onFailedProcess(errorMessage);
        }
    }
}