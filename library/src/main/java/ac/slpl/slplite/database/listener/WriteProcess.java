package ac.slpl.slplite.database.listener;


// データ書き込み後に呼ばれる処理
public interface WriteProcess extends Process {

    /**
     * データの書き込みが成功したときに呼ばれる
     */
    void onSucceededWriting();
}
