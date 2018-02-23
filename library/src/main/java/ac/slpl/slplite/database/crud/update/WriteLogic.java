package ac.slpl.slplite.database.crud.update;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import ac.slpl.slplite.database.SLPLite;
import ac.slpl.slplite.database.crud.SLPLiteLogic;

public abstract class WriteLogic<E> extends SLPLiteLogic {
    public WriteLogic(SLPLite slpLite) {
        super(slpLite);
    }

    protected abstract void execute(E data) throws InvocationTargetException, IllegalAccessException;

    /**
     * dataSetに含まれているEntityはgetterを利用して一つずつ抽出している．
     *
     * @param dataSet 含まれているEntityの中にはCollectionも含まれている．
     */
    public void extractEntityAndExecute(E dataSet) {
        Class clazz = dataSet.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            try {
                String methodName = connectInCamelCase("get", field.getName());
                final E data = (E)clazz.getMethod(methodName).invoke(dataSet);
                execute(data); // Entityごとに書き出す
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * ex:
     * head: "get", body: "deviceId"
     * return: "getDeviceId"
     * @param head 先頭につける文字列,'get', 'set', 'is' など
     */
    private String connectInCamelCase(final String head, final String body) {
        return head + Character.toTitleCase(body.charAt(0)) + body.substring(1);
    }
}
