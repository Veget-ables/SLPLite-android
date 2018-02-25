
# SLPLite

[![](https://jitpack.io/v/Veget-ables/SLPLite-android.svg)](https://jitpack.io/#Veget-ables/SLPLite-android)


SLPLiteはSQLiteをラップして作成されたO/Rマッパーである．javaの標準機能であるリフレクション，ジェネリックの練習のために作成された．
現在実装している機能はデータベースのCreate, Read, Update(レコードの新規作成)である．

## Usage

まずSLPLiteConfigのインスタンスを作成しDBの初期値を決め，作成したconfigを引数にSLPLiteのインスタンスを作成する．
インスタンスの生成時にDBのスキーマが作成される．  


```java
public class SampleActivity extends Activity {
    private SLPLite mSLPLite;

    private final String DATABASE_NAME = "slpl.db";
    private final int DATABASE_VERSION = 1;
    private final String TABLE_USER = "user_info";
    private final String TABLE_INPUT = "input_data";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Table名 -> ColumnとなるEntityクラス
        Map<String, Class> tables = new HashMap<>();
        tables.put(TABLE_USER, User.class);
        tables.put(TABLE_INPUT, InputData.class);

        SLPLiteConfig cfg = new SLPLiteConfig(DATABASE_NAME, DATABASE_VERSION, tables)
                .setDBPath(this);
        mSLPLite = new SLPLite(this, cfg);
    }
}
```


configで設定したテーブルのEntityに値を格納し，writeData()でDBに書き出す．
第1引数には設定したEntityのオブジェクト，第2引数の書き出し処理後に呼ばれるWriteProcessはNullableである．
```java
public class SampleActivity extends Activity {
        private SLPLite mSLPLite;
        
        // SLPLiteの設定
        // ...
        
        private void write(){
            mSLPLite.writeData(dataSet, new WriteProcess() {
                @Override
                public void onSucceededWriting() {
                }

                @Override
                public void onFailedProcess(String message) {
                    Log.e(getClass().toString(), message);
                }
            });
        }
}
```  

DBに保存されているデータを取得したい場合は，readData()を呼ぶことで取得できる．
第1引数には取得したいEntityのテーブル名，第2引数の読み込み完了後に呼ばれるReadProcessはNonNullである．
```java
public class SampleActivity extends Activity {
        private SLPLite mSLPLite;
        
        // SLPLiteの設定
        // ...
        
        private void read(){    
            mSLPLite.readData(TABLE_INPUT, new ReadProcess<InputData>() {
                @Override
                public void onSucceededReading(List<InputData> result) {
                }

                @Override
                public void onFailedProcess(String message) {
                    Log.e(getClass().toString(), message);
                }
            });
        }
}
```  

AssetsにDBファイルを配置すると，インストール時にそれを再利用する．
```java
        // 既存のDBを再利用したい場合に利用する.再利用するDBはAssetsに入っている前提.
        mSLPLite.recycleAssetsDataBase();
```

DB操作以外にもCSVファイルへの書き出しをサポートしている．デフォルトではCSVファイルに書き出される．
```java
        SLPLiteConfig cfg = new SLPLiteConfig(DATABASE_NAME, DATABASE_VERSION, tables)
                .setDBPath(this)
                .setSavedCsv(false); // csvファイルを書き出さない

```  
## Extra
### 書き出したCSVファイルを端末から取り出す
書き出しに成功したCSVファイルは端末の `data/data/アプリのpackage名/files/Entityのクラス名.csv`に書き出される．  
端末からCSVファイルを取り出すためにはターミナルから以下のようにして書き出す．(adbコマンドを持つパッケージをインストールしていることが前提)

```
    adb exec-out run-as package名 cat files/Entityのクラス名.csv > PCの書き出したい場所
```

ファイルが端末に書き出されているかを確認したいだけの場合には以下のコマンドで確認できる．

```
   adb shell
   run-as package名
   cd で移動
```
