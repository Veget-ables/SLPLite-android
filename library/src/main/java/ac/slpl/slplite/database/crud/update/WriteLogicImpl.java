package ac.slpl.slplite.database.crud.update;

import android.content.ContentValues;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import ac.slpl.slplite.database.SLPLite;
import ac.slpl.slplite.database.listener.WriteProcess;

public class WriteLogicImpl<E> extends WriteLogic<E> implements WriteProcess {
    private final WriteProcess mListener;

    public WriteLogicImpl(SLPLite slpLite, WriteProcess listener) {
        super(slpLite);
        mListener = (listener == null) ? this : listener;
    }

    @Override
    protected void execute(E data) throws InvocationTargetException, IllegalAccessException{
        // Listで処理する必要があるため,送られてきたdataをListに変換する
        // 取得したEntityがListである場合,1つずつ対応する
        List<ContentValues> contentValuesList = new ArrayList<>();
        Class valuesClass = null;

        if (data.getClass().getSuperclass().equals(AbstractList.class)) {
            for (E d : (ArrayList<E>) data) {
                contentValuesList.add(createContentValues(d));
                valuesClass = d.getClass();
            }
        } else {
            contentValuesList.add(createContentValues(data));
            valuesClass = data.getClass();
        }
        if (contentValuesList.size() == 0) return;

        executeWriteTask(valuesClass, contentValuesList);
    }

    /**
     * dataからリフレクションでColumn名を抽出しdataの値と対応付けてContentValueをつくる
     */
    private ContentValues createContentValues(final E data) throws IllegalAccessException, InvocationTargetException {
        ContentValues values = new ContentValues();
        for (Method method : data.getClass().getMethods()) {
            final String columnName = methodToCamelCase(method, "get", "is");
            if (columnName == null) {
                continue;
            }
            Object value = method.invoke(data);
            if (value == null) continue;

            final Class type = method.getReturnType();
            if (type.equals(long.class) || type.equals(Long.class)) {
                values.put(columnName, (long) value);
            } else if (type.equals(int.class) || type.equals(Integer.class)) {
                values.put(columnName, (int) value);
            } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                values.put(columnName, (boolean) value);
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                values.put(columnName, (float) value);
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                values.put(columnName, (double) value);
            } else if (type.equals(String.class)) {
                values.put(columnName, (String) value);
            }
        }
        return values;
    }

    private void executeWriteTask(Class clazz, List<ContentValues> contentValuesList) {
        String tableName = tableName(clazz);
        if (tableName != null) {
            new WriteAsyncTask(mListener, mSLPLite, tableName).execute(contentValuesList);
        } else {
            mListener.onFailedProcess(String.format("There is no table related to %s", clazz));
        }
    }

    @Override
    public void onSucceededWriting() {
        Log.i(getClass().toString(), "onSucceededWriting");
    }

    @Override
    public void onFailedProcess(String message) {
        Log.e(getClass().toString(), message);
        throw new RuntimeException(message);
    }
}
