package ac.slpl.example.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;

/**
 * テーブルになるJavaBeans
 * <p>
 * Parcelableの実装に対応している
 *
 * @see <a href="http://y-anz-m.blogspot.jp/2010/03/androidparcelable.html">Parcelableの参考</a>
 * @see ac.slpl.slplite.database.SLPLite
 * クラス変数名がTableのColumn名になる
 * getterを持つ変数が書き込みの対象となり、それ以外は対象外
 * 現状すべてのgetterが書き込み対象になるので、関係のないgetterは書かない
 * DB操作時にgenericでインスタンス生成するため、空のコンストラクタが必須
 */

@Getter
@Setter
public class InputData implements Parcelable {
    private long dataId; //UserとInputDataを結びつけるためのId
    private String text;
    private double time;
    private int errorCount;

    public InputData() { // genericで参照されるため必須
    }

    public InputData(String text, double time, int errorCount) {
        this.text = text;
        this.time = time;
        this.errorCount = errorCount;
    }

    protected InputData(Parcel in) {
        dataId = in.readLong();
        text = in.readString();
        time = in.readDouble();
        errorCount = in.readInt();
    }

    public static final Creator<InputData> CREATOR = new Creator<InputData>() {
        @Override
        public InputData createFromParcel(Parcel in) {
            return new InputData(in);
        }

        @Override
        public InputData[] newArray(int size) {
            return new InputData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(dataId);
        dest.writeString(text);
        dest.writeDouble(time);
        dest.writeInt(errorCount);
    }
}
