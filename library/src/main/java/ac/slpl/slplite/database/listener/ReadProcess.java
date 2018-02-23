package ac.slpl.slplite.database.listener;

import java.util.List;

// データ読み込み後に呼ばれる処理
public interface ReadProcess<E> extends Process {

    /**
     * データの読み込みが成功したときに呼ばれる
     *
     * @param result 指定したEntityオブジェクトのリスト
     */
    void onSucceededReading(List<E> result);
}
