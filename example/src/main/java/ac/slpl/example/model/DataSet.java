package ac.slpl.example.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * テーブルになるJavaBeansを保持
 *
 * @see ac.slpl.slplite.database.SLPLite
 * クラス変数名がTableのColumn名になる
 * getterを持つ変数が書き込みの対象となり、それ以外は対象外
 * 現状すべてのgetterが書き込み対象になるので、関係のないgetterは書かない
 */

@Getter
@Setter
public class DataSet {
    private User user;
    private List<InputData> inputData;

    public DataSet() { // genericで参照されるため必須
    }
}