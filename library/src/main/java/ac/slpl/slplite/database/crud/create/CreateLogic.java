package ac.slpl.slplite.database.crud.create;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ac.slpl.slplite.database.crud.SLPLiteLogic;

public class CreateLogic extends SLPLiteLogic {
    private static final String PRIMARY_KEY_ID = "id";

    public CreateLogic() {
        super(null);
    }

    /**
     * Tableは複数作成される場合があるので，ひとつずつqueryを作成しTableを作成する.
     * デフォルトでカラムのidを持つ.
     */
    public List<String> createSchemaQuery(Map<String, Class> tables) {
        List<String> queryList = new ArrayList<>();

        for (String table : tables.keySet()) {
            String tableSchema = buildTableSchema(tables.get(table));
            String query = String.format("create table %s(%s INTEGER PRIMARY KEY, %s);",
                    table, PRIMARY_KEY_ID, tableSchema);

            queryList.add(query);
        }
        return queryList;
    }

    /**
     * classからgetterを取得し，Columnとして抽出する．
     */
    private String buildTableSchema(Class clazz){
        String tableSchema = "";
        for (Method method : clazz.getMethods()) {
            String columnName = methodToCamelCase(method, "get", "is");
            if (columnName != null) {
                tableSchema += columnName + ",";
            }
        }
        return tableSchema.substring(0, tableSchema.length() - 1); // queryになる最後の","を削除
    }
}
