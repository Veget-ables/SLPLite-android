package ac.slpl.slplite.database.crud.read;

import android.database.Cursor;
import android.os.AsyncTask;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ac.slpl.slplite.database.SLPLite;
import ac.slpl.slplite.database.listener.ReadProcess;

public class ReadAsyncTask<E> extends AsyncTask<Object, List<E>, List<E>> {
    private final SLPLite mSLPLite;
    private final ReadProcess mListener;
    private final E mTargetEntity;
    private final Map<String, Method> mSchemaMap;

    public ReadAsyncTask(SLPLite slpLite, ReadProcess listener, E entity,
                         Map<String, Method> schemaMap) {
        mSLPLite = slpLite;
        mListener = listener;
        mTargetEntity = entity;
        mSchemaMap = schemaMap;
    }

    @Override
    protected List<E> doInBackground(Object... tables) {
        if (!mSLPLite.getTables().containsKey(tables[0])) {
            return null;
        }

        mSLPLite.openRead();
        // TODO 取得するデータを絞り込み選択できるようにする
        Cursor cursor = mSLPLite.getDatabase().query(String.valueOf(tables[0]),
                null, null, null,
                null, null, null
        );
        List<E> entityList = createEntityList(cursor);
        cursor.close();
        mSLPLite.close();
        return entityList;
    }

    private List<E> createEntityList(Cursor cursor) {
        List<E> entityList = new ArrayList<>();
        boolean isMove = cursor.moveToFirst();
        while (isMove) {
            try {
                E entity = (E) mTargetEntity.getClass().newInstance(); // 格納用に空のインスタンスを作成する
                entity = toEntity(cursor, entity);
                isMove = cursor.moveToNext();
                entityList.add(entity);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        cursor.close();
        return entityList;
    }

    /**
     * setterをもつMethodからColumn名を抽出し,Entityオブジェクトを構成する
     * TODO Bitmapに対応させる
     *
     * @param entity Tableのオブジェクトである空のインスタンス
     *               cursorからの値抽出 + 抽出されたデータの格納用
     */
    private E toEntity(Cursor cursor, E entity) throws IllegalAccessException, InvocationTargetException {
        for (String columnName : mSchemaMap.keySet()) {
            Method method = mSchemaMap.get(columnName);
            final int columnIndex = cursor.getColumnIndex(columnName);

            switch (cursor.getType(columnIndex)) {
                case Cursor.FIELD_TYPE_INTEGER:
                    int value = cursor.getInt(columnIndex);
                    // SQLiteではbooleanはIntegerで格納されるため,変換する
                    if (method.getParameterTypes()[0].equals(boolean.class)) {
                        boolean bool = value == 1;
                        method.invoke(entity, bool);
                    } else {
                        method.invoke(entity, value);
                    }
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    method.invoke(entity, cursor.getString(columnIndex));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    method.invoke(entity, cursor.getFloat(columnIndex));
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    break;
                case Cursor.FIELD_TYPE_NULL:
                    break;
                default:
                    break;
            }
        }
        return entity;
    }

    @Override
    protected void onPostExecute(List<E> result) {
        if (result.size() > 0) {
            mListener.onSucceededReading(result);
        } else {
            mListener.onFailedProcess("There are no items.");
        }
    }
}