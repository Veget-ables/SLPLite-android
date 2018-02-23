package ac.slpl.slplite.database.crud;


import java.lang.reflect.Method;
import java.util.Map;

import ac.slpl.slplite.database.SLPLite;

public class SLPLiteLogic {
    protected SLPLite mSLPLite;

    protected SLPLiteLogic(SLPLite slpLite) {
        mSLPLite = slpLite;
    }

    /**
     * getter,setter,isを先頭に持つmethodをcolumnへの変換することを想定している.
     * ex:
     * method: getMeritDescribe(), conversions: "get"
     * return: "meritDescribe"
     */
    protected String methodToCamelCase(final Method method, final String... conversions) {
        final String methodName = method.getName();
        for (String c : conversions) {
            if (methodName.startsWith(c) && !methodName.contains("Class")) {
                String rawName = methodName.replace(c, "");
                return rawName.substring(0, 1).toLowerCase() + rawName.substring(1);
            }
        }
        return null;
    }

    /**
     * Classに対応するTable名を返す.
     */
    protected String tableName(Class targetClass) {
        Map<String, Class> tables = mSLPLite.getTables();
        for (String tableName : tables.keySet()) {
            Class tableClass = tables.get(tableName);
            if (tableClass.equals(targetClass)) {
                return tableName;
            }
        }
        return null;
    }
}
