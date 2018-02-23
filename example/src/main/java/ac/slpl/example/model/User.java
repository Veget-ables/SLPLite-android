package ac.slpl.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * テーブルになるJavaBeans
 *
 * @see ac.slpl.slplite.database.SLPLite
 * クラス変数名がTableのColumn名になる
 * getterを持つ変数が書き込みの対象となり、それ以外は対象外
 * 現状すべてのgetterが書き込み対象になるので、関係のないgetterは書かない
 * DB操作時にgenericでインスタンス生成するため、空のコンストラクタが必須
 */

@Accessors(chain = true)
@Getter
@Setter
public class User {
    public long deviceId;       //UserとInputDataを結びつけるためのId
    public String name;
    public int age;
    public boolean sex;        // true: 男 false: 女
    public boolean handedness; // true: 右 false: 左
    public String describeMerit;
    public String describeDemerit;

    public User() { // genericで参照されるため必須
    }
}