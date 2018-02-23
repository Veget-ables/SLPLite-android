package ac.slpl.slplite.database.crud.update.csv;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import ac.slpl.slplite.database.crud.update.WriteLogic;


public class WriteCsvLogicImpl<E> extends WriteLogic<E> {
    private final String NEW_LINE = System.getProperty("line.separator");
    private final Context mContext;

    public WriteCsvLogicImpl(Context context) {
        super(null);
        mContext = context;
    }

    @Override
    protected void execute(E data) throws InvocationTargetException, IllegalAccessException {
        // Listで処理する必要があるため,送られてきたdataをListに変換する
        // 取得したEntityがListである場合,1つずつ対応する
        List<E> dataList = new ArrayList<>();
        if (data.getClass().getSuperclass().equals(AbstractList.class)) {
            for (E d : (ArrayList<E>) data) {
                dataList.add(d);
            }
        } else {
            dataList.add(data);
        }
        if (dataList.size() == 0) return;

        dataList = removeNewLine(dataList); // 改行が入るとcsv操作でこけるので改行を削除
        executeWriteTask(dataList);
    }

    private List<E> removeNewLine(final List<E> dataList) throws IllegalAccessException, InvocationTargetException {
        List<E> removedNewLineList = new ArrayList<>();
        for (E data : dataList) {
            Class dataClass = data.getClass();

            // Entityのgetterとsetterを利用して改行を取り除く
            for (Method method : dataClass.getMethods()) {
                if (!method.getReturnType().equals(String.class)) continue;
                try {
                    String message = (String) method.invoke(data);
                    if (message == null) continue;
                    message = message.replaceAll(NEW_LINE, "l");
                    Method setter = getterToSetter(dataClass, method);
                    setter.invoke(data, message);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            removedNewLineList.add(data);
        }
        return removedNewLineList;
    }

    private Method getterToSetter(Class clazz, Method getter) throws NoSuchMethodException {
        String setterName = "set" + getter.getName().replace("get", "");
        return clazz.getMethod(setterName, String.class);
    }

    private void executeWriteTask(List<E> dataList) {
        new WriteCsvAsyncTask(mContext).execute(dataList);
    }
}
