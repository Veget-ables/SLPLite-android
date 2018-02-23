package ac.slpl.slplite.database.listener;


public interface Process {

    /**
     * DB操作が失敗したときに呼ばれる
     */
    void onFailedProcess(String message);
}