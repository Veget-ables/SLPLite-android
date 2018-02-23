package ac.slpl.slplite.database.crud.read;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ac.slpl.slplite.database.SLPLite;
import ac.slpl.slplite.database.crud.SLPLiteLogic;
import ac.slpl.slplite.database.listener.ReadProcess;

public class ReadLogicImpl<E> extends SLPLiteLogic implements ReadProcess<E> {
    private final ReadProcess mReadListener;

    public ReadLogicImpl(SLPLite slpLite, ReadProcess listener) {
        super(slpLite);
        mReadListener = listener;
    }

    public void executeReadTask(String tableName) {
        E targetEntity = newEntityInstance();
        new ReadAsyncTask(mSLPLite, mReadListener, targetEntity, schemaMapping(targetEntity)).execute(tableName);
    }

    /**
     * @link {http://d.hatena.ne.jp/Nagise/20131121/1385046248}
     */
    private E newEntityInstance() {
        try {
            Class<?> clazz = mReadListener.getClass();
            Type type = clazz.getGenericInterfaces()[0];
            ParameterizedType pt = (ParameterizedType) type;
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            Class<?> entityClass = (Class<?>) actualTypeArguments[0];
            return (E) entityClass.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Method> schemaMapping(E entity) {
        Map<String, Method> schemaMap = new HashMap<>();
        for (Method method : entity.getClass().getMethods()) {
            final String columnName = methodToCamelCase(method, "set");
            if (columnName != null) {
                schemaMap.put(columnName, method);
            }
        }
        return schemaMap;
    }

    @Override
    public void onFailedProcess(String message) {
        throw new RuntimeException("It is necessary to implement the interface.");
    }

    @Override
    public void onSucceededReading(List<E> result) {
        throw new RuntimeException("It is necessary to implement the interface.");
    }
}
